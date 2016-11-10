package client;

import algorithms.IStrategyACM;
import algorithms.StrategyKruskal;
import algorithms.StrategyPrim;
import GraphModel.Graph;
import GraphModel.Vertice;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Graph g = new Graph("mon graph", true);

		Vertice v1 = new Vertice("S1");
		Vertice v2 = new Vertice("S2");
		Vertice v3 = new Vertice("S3");
		Vertice v4 = new Vertice("S4");
		Vertice v5 = new Vertice("S5");
		Vertice v6 = new Vertice("S6");
		Vertice v7 = new Vertice("S7");
		Vertice v8 = new Vertice("S8");
		Vertice v9 = new Vertice("S9");

		g.addVertice(v1);
		g.addVertice(v2);
		g.addVertice(v3);
		g.addVertice(v4);
		g.addVertice(v5);
		g.addVertice(v6);
		g.addVertice(v7);
		g.addVertice(v8);
		g.addVertice(v9);

		g.addEdge(v1, v2, 8);
		g.addEdge(v2, v3, 7);
		g.addEdge(v3, v4, 9);
		g.addEdge(v4, v5, 10);
		g.addEdge(v5, v6, 2);
		g.addEdge(v6, v8, 1);
		g.addEdge(v8, v9, 8);
		g.addEdge(v9, v1, 4);
		g.addEdge(v1, v8, 11);
		g.addEdge(v8, v7, 7);
		g.addEdge(v7, v6, 6);
		g.addEdge(v7, v2, 2);
		g.addEdge(v2, v5, 4);

		//IStrategyACM strat = new StrategyPrim();
		IStrategyACM strat = new StrategyKruskal();
		Graph acm = strat.findACM(g);
		
		System.out.println(g.toString());
		System.out.println(acm.toString());
	}

}
