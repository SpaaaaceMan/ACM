package algorithms;

import graphModel.Edge;
import graphModel.Graph;

import java.util.ArrayList;
import java.util.PriorityQueue;


public class StrategyPrimEvolved extends AbstractAlgo {

	PriorityQueue<Edge> edgesNotInACMStack;
	
	public Graph findACM(Graph graph) {
		setMainGraph(graph);
		createACM();
		
		for (Edge e : edgesNotInACM)
			if (isAGoodEdge(e))
				edgesNotInACMStack.add(e);
		
		int nbVertices = mainGraph.getVertices().size();
		
		while (acm.getVertices().size() < nbVertices){
			acm.addEdge(edgesNotInACMStack.peek());
			finalWeight += edgesNotInACMStack.poll().getValue();
			edgesNotInACMStack.clear();
			for (Edge e : edgesNotInACM)
				if (isAGoodEdge(e))
					edgesNotInACMStack.add(e);
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

	public String getNameAlgo() {
		return "Prim Evolved";
	}

	public void initACM() {
		acm.addVertice(mainGraph.getVertices().get(0));		
		edgesNotInACMStack = new PriorityQueue<Edge>();
		for (Edge e : edgesNotInACM)
			if (isAGoodEdge(e))
				edgesNotInACMStack.add(e);	
	}
}
