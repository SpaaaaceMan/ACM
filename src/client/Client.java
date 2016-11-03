package client;

import algorithms.IStrategyACM;
import algorithms.StrategyPrim;
import GraphModel.Graph;
import GraphModel.Vertice;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Graph g = new Graph("mon graph", false);
		
		Vertice v1 = new Vertice("S1");
		Vertice v2 = new Vertice("S2");
		Vertice v3 = new Vertice("S3");
		Vertice v4 = new Vertice("S4");
		Vertice v5 = new Vertice("S5");
		Vertice v6 = new Vertice("S6");

		g.addVertice(v1);
		g.addVertice(v2);
		g.addVertice(v3);
		g.addVertice(v4);
		g.addVertice(v5);
		g.addVertice(v6);

		g.addEdge(v1, v2);
		g.addEdge(v1, v3);
		g.addEdge(v3, v5);
		g.addEdge(v5, v6);
		g.addEdge(v3, v6);
		
		IStrategyACM strat = new StrategyPrim();
		Graph acm = strat.findACM(g);

		System.out.println(g.toString());
		System.out.println(acm.toString());
	}

}
