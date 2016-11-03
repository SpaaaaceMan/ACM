package algorithms;

import java.util.ArrayList;

import GraphModel.Edge;
import GraphModel.Graph;

public class StrategyPrim extends AbstractAlgo {

	public Graph findACM(Graph graph) {
		setMainGraph(graph);
		createACM();
		int nbVertices = mainGraph.getVertices().size();
		while (acm.getVertices().size() < nbVertices) {
			acm.addEdge(findMinimalEdge());
		}
		System.out.println("weight = " + finalWeight);
		return acm;
	}

	public boolean isAGoodEdge(Edge e) {
		if (!acm.getVertices().contains(e.getVertice1()) 
				&& !acm.getVertices().contains(e.getVertice2()))
			return false;
		else if (acm.getVertices().contains(e.getVertice1()) 
				&& acm.getVertices().contains(e.getVertice2()))
			return false;
		return true;
	}

	public Edge findMinimalEdge() {
		//liste des arêtes liées à au moins un sommet de l'ACM actuel mais pas 
		//déjà dedans
		ArrayList<Edge> edgesFound = new ArrayList<Edge>();

		//pour chaque sommet de l'ACM
		for (Edge e : edgesNotInACM) {
			if (isAGoodEdge(e))
				edgesFound.add(e);
		}	
		Edge res = null;
		int min = getMin(edgesFound);
		finalWeight += min;
		for (Edge e : edgesFound) {
			if (e.getValue() == min) {
				res = e;
				edgesNotInACM.remove(res);
				return res;
			}
		}		
		return res;
	}

	public String getNameAlgo() {
		return "Prim";
	}

	public void initACM() {
		acm.addVertice(mainGraph.getVertices().get(0));		
	}
}
