#include "bor-util.h"
#include "bor-timer.h"

#define TRUE 1
#define FALSE 0

#define SLOT_NB 30
#define REQSIZE 4096
#define REPSIZE 4096

int mainLoop;

typedef enum {E_LIBRE, E_LIRE_REQUETE, E_ECRIRE_REPONSE, E_LIRE_FICHIER, E_ENVOYER_FICHIER} Etat;
typedef enum { M_NONE, M_GET, M_TRACE} Id_methode;
typedef enum {
	C_OK                = 200,
	C_BAD_REQUEST       = 400,
	C_NOT_FOUND         = 404,
	C_METHOD_UNKNOWN    = 501
} Code_reponse;

typedef struct {
	char    methode[REQSIZE],
			url[REQSIZE],
			version[REQSIZE],
			chemin[REQSIZE];
	Id_methode id_meth;
	Code_reponse code_rep;
} Infos_entete;

typedef struct {
	Etat etat;
	char fic_bin[FICSIZE];	/* memorise [tronçon] in a file */
	int fic_pos;			/* current position in the [tronçon] */
	int fic_len;			/* size of the [tronçon] */
	int soc;                /* Socket de service, defaut : -1 */
	struct sockaddr_in adr; /* Adresse du client */
	char req[REQSIZE];      /* Requete du client */
	int req_pos;            /* position courante lecture requete */
	int fin_entete;         /* position fin de l'entete */
	char rep[REPSIZE];      /* Reponse au client */
	int rep_pos;            /* position reponse non-envoyé */
	int fic_fd;             /* file descriptor of the webpage */
	int handle;				/* handle of a timer */
} Slot;

typedef struct {
	Slot slots[SLOT_NB];
	int soc_ec;             /* Socket d'ecoute */
	struct sockaddr_in adr; /* Adresse du serveur */
} Serveur;

char *get_http_error_message (Code_reponse code) {
	switch (code) {
		case C_OK: return "OK";
		case C_BAD_REQUEST: return "BAD REQUEST";
		case C_NOT_FOUND: return "ERROR 404 : NOT FOUND";
		case C_METHOD_UNKNOWN: return "UNKNOWN METHODE";
		default: return "UNKNOWN ERROR";
	}
}

Id_methode get_id_methode (char *methode) {
	if (strcasecmp(methode, "GET") == 0) {
		return M_GET;
	}

	if (strcasecmp(methode, "TRACE") == 0) {
		return M_TRACE;
	}

	return M_NONE;
}

void init_slot (Slot *o) {
	o->etat = E_LIBRE;
	o->soc = -1;
	o->req[0] = '\0';
	o->req_pos = 0;
	o->fin_entete = 0;
	o->rep[0] = '\0';
	o->rep_pos = 0;
	o->fic_fd = -1;
	o->fic_len = 0;
	o->fic_pos = 0;
}

int slot_est_libre (Slot *o) {
	return o->etat == E_LIBRE;
}

void liberer_slot (Slot *o) {
	if (slot_est_libre(o)) return;
	if (o->fic_fd != -1) close(o->fic_fd);

	bor_timer_remove(o->handle);

	close(o->soc);
	init_slot(o);
}

void init_serveur (Serveur *ser) {
	printf("Server : initialization...\n");
	for (size_t i = 0; i < SLOT_NB; i++) {
		init_slot(&ser->slots[i]);
	}
	ser->soc_ec = -1;
}

int chercher_slot_libre (Serveur *ser) {
	for (size_t i = 0; i < SLOT_NB; i++) {
		if (slot_est_libre(&ser->slots[i])) return i;
	}
	return -1;
}

int demarrer_serveur (Serveur *ser, int port) {
	printf("Server : starting...\n");
	init_serveur(ser);
	ser->soc_ec = bor_create_socket_in( SOCK_STREAM, port, &ser->adr);
	if (ser->soc_ec < 0) {
		return -1;
	}
	if (bor_listen(ser->soc_ec, 8) < 0) {
		close(ser->soc_ec);
		return -1;
	}
	return 0;
}

void fermer_serveur (Serveur *ser) {
	printf("Server : closing...\n");
	close(ser->soc_ec);
	for (size_t i = 0; i < SLOT_NB; i++) {
		liberer_slot(&ser->slots[i]);
	}
}

int accepter_connexion (Serveur *ser) {
	printf("Serveur: connexion en cours...\n");
	struct sockaddr_in adr_tmp;
	int soc_tmp = bor_accept_in(ser->soc_ec, &adr_tmp);
	
	if (soc_tmp < 0) {
		return -1;
	}
	
	int slot = chercher_slot_libre(ser);
	if (slot < 0) {
		printf("Serveur: plus de slot libre : %s\n", bor_adrtoa_in(&adr_tmp));
		close(soc_tmp);
		return 0; //pb temporaire
	}

	printf("Serveur[%d]: connexion etablie avec %s\n", soc_tmp, bor_adrtoa_in(&adr_tmp));
	Slot *o = &ser->slots[slot];
	
	o->handle = bor_timer_add(30000, o);
	o->etat = E_LIRE_REQUETE;
	o->soc = soc_tmp;
	o->adr = adr_tmp;
	return 1;
}

int preparer_fichier (Slot *o, Infos_entete *ie) {
	sscanf(ie->url, "%[^? ]", ie->chemin);
	printf("server [%d]: chemin capté : %s\n", o->soc, ie->chemin);

	int k = open(ie->chemin, O_RDWR, 0644);
	if (k < 0) {
		perror("Server error: echec ouverture fichier");
		return -1;
	}
	o->fic_fd = k;
	return 0;
}

int lire_suite_requete (Slot *o) {
	int k = bor_read_str (o->soc, o->req + o->req_pos, REQSIZE - o->req_pos);
	if (k > 0) {
		o->req_pos += k;
	}
	return k;
}

int chercher_fin_entete (Slot *o, int debut) {
	for (size_t i = debut; o->req[i] != '\0'; i++) {
		if ((o->req[i] == '\n' && o->req[i+1] == '\n') ||
		(o->req[i] == '\r' && o->req[i+1] == '\n' && o->req[i+2] == '\r' && o->req[i+3] == '\n')) {
			return i;
		}
	}
	return -1;
}

void analyser_requete (Slot *o, Infos_entete *ie) {
	ie->methode[0] = 0;
	ie->url[0] = 0;
	ie->version[0] = 0;
	sscanf(o->req, "%s %s %s", ie->methode, ie->url, ie->version);

	printf("Server [%d]: methode : %s url : %s version : %s\n", o->soc, ie->methode, ie->url, ie->version);

	if (ie->methode[0] == 0 || ie->url[0] == 0 || ie->version[0] == 0 ) {
		printf("Billy ?\n");
		ie->code_rep = C_BAD_REQUEST;
		return;
	}

	ie->id_meth = get_id_methode(ie->methode);
	if (ie->id_meth == M_NONE) {
		ie->code_rep = C_METHOD_UNKNOWN;
		return;
	}

	if (ie->id_meth == M_GET) {
		int k = preparer_fichier(o, ie);
		if (k < 0) {
			ie->code_rep = C_NOT_FOUND;
			return;
		}
	}

	ie->code_rep = C_OK;

}

void preparer_reponse (Slot *o, Infos_entete *ie) {
	int pos = 0;
	pos += sprintf (o->rep+pos,"HTTP/1.1 ");
	pos += sprintf (o->rep+pos, "%d", ie->code_rep);
	pos += sprintf (o->rep+pos, "%s", get_http_error_message(ie->code_rep));
	pos += sprintf (o->rep+pos,"\nDate: ");
	time_t tmp;
	time(&tmp);
	pos += sprintf (o->rep+pos, "%d", ctime(&tmp));
	pos += sprintf (o->rep+pos,"Server: serweb2\nConnection: close\nContent-Type: ");

	if (ie->code_rep != C_OK) {
		pos += sprintf (o->rep+pos, "text/html\n\n");
		pos += sprintf (o->rep+pos, "<html><head>\n");
		pos += sprintf (o->rep+pos, "   <title>");
		pos += sprintf (o->rep+pos, "%s", get_http_error_message(ie->code_rep));
		pos += sprintf (o->rep+pos, "</title>\n");
		pos += sprintf (o->rep+pos, "</head><body>\n");
		pos += sprintf (o->rep+pos, "   <h1>%d : %s</h1>\n", ie->code_rep, get_http_error_message(ie->code_rep));
		pos += sprintf (o->rep+pos, "</body></html>\n");
		return;
	}

	switch (ie->id_meth) {
		case M_TRACE:
			pos += sprintf (o->rep+pos, "message/http\n\n");
			pos += sprintf (o->rep+pos, o->req);
			break;
		case M_GET:
			pos += sprintf (o->rep+pos, "text/html\n\n");
			pos += sprintf (o->rep+pos, "<html><head>\n");
			pos += sprintf (o->rep+pos, "   <title>Fichier trouv&eacute;</title>\n");
			pos += sprintf (o->rep+pos, "</head><body>\n");
			pos += sprintf (o->rep+pos, "   <h1>Fichier trouv&eacute;</h1>\n");
			pos += sprintf (o->rep+pos, "</body></html>\n");
			break;
	}
}

int proceder_lecture_requete(Slot *o) {
	/*printf("Server : request detected\n");

	char buf[1024];
	ssize_t res = bor_read_str(o->soc, buf, sizeof(buf));
	if (res <= 0) {
		return res;
	}

	printf("Client %s : %s\n", bor_adrtoa_in(&o->adr), buf);
	o->etat = E_ECRIRE_REPONSE;
	printf("Server [%d]: state -> writing answer\n", o->soc);
	return res;*/

	int prec_pos = o->req_pos;

	int k = lire_suite_requete (o);
	if (k <= 0) {
		return -1;
	}

	int debut = prec_pos - 3;
	if (debut < 0) debut = 0;
	o->fin_entete = chercher_fin_entete (o, debut);
	if (o->fin_entete < 0) {
		printf("Serveur [%d: requete incomplete]\n", o->soc);
		return 1;
	}

	printf("Serveur [%d]: requete complete : %s\n", o-> soc, o->req);

	Infos_entete ie;
	analyser_requete (o, &ie);
	preparer_reponse (o, &ie);
	o->etat = E_ECRIRE_REPONSE;
	return 1;
}

int ecrire_suite_reponse (Slot *o) {
	int k = bor_write_str (o->soc, o->rep + o->rep_pos);
	if (k > 0) {
		o->rep_pos += k;
	}
	return k;
}

int proceder_ecriture_reponse(Slot *o) {
	printf("Server : sending answer\n");

	/*char buf [1024];
	sprintf(buf,
	"HTTP/1.1 500 erreur du serveur \n\n\
	<html><body><h1>Les gauffres sont en cours de cuisson!!!</h1></body></html>"
	);
	ssize_t res = bor_write_str(o->soc, buf);

	if (res <= 0) {
		return res;
	}

	printf("Server -> Client %s : %s\n", bor_adrtoa_in(&o->adr), buf);
	printf("Server [%d]: state -> Reading request\n", o->soc);
	o->etat = E_LIRE_REQUETE;
	return 0; // Couper la connexion pour que le navigateur affiche la réponse*/

	int k = ecrire_suite_reponse(o);
	if (k < 0 ) return -1;
	

	if (o->rep_pos < (int) strlen (o->rep)) {
		printf("Serveur [%d]: reponse incomplete\n", o->soc);
		return 1;
	}

	if (o->fic_fd != -1) {
		o->etat = E_LIRE_FICHIER;
		return 1;
	}
	else
		return -1;
}

int proceder_lecture_fichier(Slot *o) {
	
	o->fic_len = bor_read(o->soc, o->fic_bin, FICSIZE);
	if (o->fic_len <= 0) {
		return -1;
	}
	o->fic_pos = 0;
	o->etat = E_ENVOYER_FICHIER;
	return 1;
}

int proceder_envoi_fichier(Slot *o) {
	int k = bor_write(o->soc, o->fic_bin + o->fic_pos, FICSIZE);
	if(k < 0)
		return k;
	o->fic_pos += k;
	if(o->fic_pos < o->fic_len) {
		printf("Un message que je l'aime\n");
		return 1;
	}
	o->etat = E_LIRE_FICHIER;
	return 1;
}

void traiter_slot_si_eligible (Slot *o, fd_set *set_read, fd_set *set_write) {
	if (slot_est_libre(o)) return;
	int k = 1; //pour eviter une deconnexion par defaut, si k pas initialisé dans le switch
	switch (o->etat) {
		case E_LIRE_REQUETE:
			if (FD_ISSET(o->soc, set_read))
				k = proceder_lecture_requete(o);
			break;
		case E_ECRIRE_REPONSE:
			if (FD_ISSET(o->soc, set_write))
				k = proceder_ecriture_reponse(o);
			break;
		case E_LIRE_FICHIER:
			if (FD_ISSET(o->soc, set_read))
				k = proceder_lecture_fichier(o);
			break;
		case E_ENVOYER_FICHIER:
			if (FD_ISSET(o->soc, set_write))
				k = proceder_envoi_fichier(o);
			break;
		default:;
	}
	if (k <= 0) {
		printf("Serveur[%d]: liberation du slot\n", o->soc);
		liberer_slot(o);
	}
}

void inserer_fd (int fd, fd_set *set, int *maxfd) {
	FD_SET(fd, set);
	if (*maxfd < fd) *maxfd = fd;
}

void preparer_select (Serveur *ser, int *maxfd, fd_set *set_read, fd_set *set_write) {
	FD_ZERO (set_read);
	FD_ZERO (set_write);
	*maxfd = -1;

	inserer_fd (ser->soc_ec, set_read, maxfd);

	for (size_t i = 0; i < SLOT_NB; i++) {
		Slot *o = &ser->slots[i];
		switch (o->etat) {
			case E_LIBRE:
				continue;
				break;
			case E_LIRE_REQUETE:
				inserer_fd (o->soc, set_read, maxfd);
				break;
			case E_ECRIRE_REPONSE:
				inserer_fd (o->soc, set_write, maxfd);
				break;
			case E_LIRE_FICHIER:
				inserer_fd (o->soc, set_read, maxfd);
				break;
			case E_ENVOYER_FICHIER:
				inserer_fd (o->soc, set_write, maxfd);
				break;
			default:;
		}
	}
}

int faire_scrutation (Serveur *ser, int *maxFd, fd_set *set_read, fd_set *set_write) {

	preparer_select(ser, maxFd, set_read, set_write);

	int res = select(*maxFd + 1, set_read, set_write, NULL, bor_timer_delay());
	
	if (res < 0) {
		if (errno == EINTR) {
			printf("Signal received\n");
			return res;
		}
		else {
			perror("select");
			return res;
		}
	}
	else if (res == 0){
		Slot *o = bor_timer_data();
		printf("Connection interrompu[%d] : timeout\n", o->soc);
		liberer_slot(o);
	}
		
	// FD's processing
	else {
		if(FD_ISSET(ser->soc_ec, set_read)) {
			res = accepter_connexion(ser);
			if (res < 0) {
				return res;
			}
		}
		for (size_t i = 0; i < SLOT_NB; ++i) {
			traiter_slot_si_eligible(&ser->slots[i], set_read, set_write);
		}
	}
	return 1;
}

void interruptionHandler(int sig) {
	(void) sig;
	printf("Server : interruption detected\n");
	mainLoop = 0;
}

int main (int argc, char *argv[]) {

	if (argc - 1 != 1) {
		fprintf(stderr, "Error, not enough arguments.\nUsage : %s port", argv[0]);
		return 1;
	}

	int port;
	if (sscanf(argv[1], "%d ", &port) == EOF) {
		fprintf(stderr, "Error : invalid argument for port\n");
		return 1;
	}

	Serveur ser;
	if (demarrer_serveur(&ser, port) < 0) {
		fprintf(stderr, "Error : on server starting. Maybe port is invalid\n");
		return 1;
	}

	mainLoop = 1;
	bor_signal(SIGINT, interruptionHandler, SA_RESTART);

	int res = 0;
	int maxFd;
	fd_set setRead, setWrite;

	while(mainLoop) {
		res = faire_scrutation(&ser, &maxFd, &setRead, &setWrite);
		if(res < 0) {
			fprintf(stderr, "error on scrutation\n");
		}
	}

	fermer_serveur(&ser);
	printf("Server : closed\n");
	return (res < 0 ? 1 : 0);
}

