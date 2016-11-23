package algorithms;

import java.util.ArrayList;

import GraphModel.Edge;
import GraphModel.Graph;

public abstract class AbstractAlgo implements IStrategyACM {

	protected Graph mainGraph;
	protected Graph acm;
	protected ArrayList<Edge> edgesNotInACM;
	protected int finalWeight;

	public void createACM() {
		System.out.println("Arbre couvrant minimal obtenu à l'aide de l'algorithme de " + getNameAlgo());
		edgesNotInACM = mainGraph.getEdges();
		acm = new Graph(getMainGraph().getName() + "ACM(" + getNameAlgo() + ")", mainGraph.isWeighted());
		finalWeight = 0;
		initACM();
	}
	
	public int getMin(ArrayList<Edge> edgesFound) {
		int values[] = new int [edgesFound.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = edgesFound.get(i).getValue();
		}
		int min = values[0];
		if (values.length > 1) {
			for (int i = 1; i < values.length; i++) {
				if (values[i] < min) {
					min = values[i];
				}
			}
		}
		return min;
	}

	public Graph getMainGraph() {
		return mainGraph;
	}

	public void setMainGraph(Graph mainGraph) {
		this.mainGraph = mainGraph;
	}

}
