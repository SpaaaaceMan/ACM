package algorithms;

import java.util.ArrayList;

import GraphModel.Edge;
import GraphModel.Graph;
import GraphModel.Vertice;

public class StrategyPrim extends AbstractAlgo {

	public Graph findACM(Graph graph) {
		setMainGraph(graph);
		createACM();
		int nbVertices = mainGraph.getVertices().size();
		while (acm.getVertices().size() < nbVertices) {
			acm.addEdge(findMinimalEdge());
		}
		return acm;
	}
	
	public Edge findMinimalEdge() {
		ArrayList<Edge> edgesFound = new ArrayList<Edge>();
		
		//pour chaque arête du graphe d'origine
		for (int i = 0; i < mainGraph.getEdges().size(); i++) {
			Edge currEdge = mainGraph.getEdges().get(i);
			for (int j = 0; j < acm.getVertices().size(); j++) {
				Vertice currVer = acm.getVertices().get(j);
				for (int k = 0; k < currVer.getEdges().size(); k++) {
					Edge cmpEdge = currVer.getEdges().get(k);
					if (cmpEdge == currEdge) {
						edgesFound.add(currEdge);
					}
				}
			}
		}	
		int values[] = new int [edgesFound.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = edgesFound.get(i).getValue();
		}
		int min = getMin(values);
		for (int i = 0; i < edgesFound.size(); i++) {
			if (edgesFound.get(i).getValue() == min) {
				return edgesFound.get(i);
			}
		}		
		return null;
	}
	
	public String getNameAlgo() {
		return "Prim";
	}

}
