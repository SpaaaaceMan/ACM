package algorithms;

import java.util.ArrayList;

import graphModel.Edge;
import graphModel.Graph;
import graphModel.Vertice;

public class StrategyKruskal extends AbstractAlgo {
	
	private ArrayList<Graph> subGraphs = new ArrayList<Graph>();
	
	public Graph findACM(Graph graph) {
		setMainGraph(graph);
		createACM();
		
		int lastIndex = mainGraph.getVertices().size() - 1;
		
		//parcours des arêtes triées par poids
		for (Edge e : edgesNotInACM) {
			
			//lorsque le nombre d'arête de l'acm = N - 1 c'est fini
			if (acm.getEdges().size() == lastIndex) break;
			
			Graph subGraph1 = null;
			Graph subGraph2 = null;
			
			for (Graph g : subGraphs) {
				for (Vertice v : g.getVertices()) {
					//si le sous-graphe possède l'un des sommets de l'arête minimale courante
					if (v == e.getVertice1() || v == e.getVertice2()) {
						if (subGraph2 == null) {
							subGraph2 = g;
						}
						else {
							subGraph1 = g;
						}
						break;
					}
				}
			}
			System.out.println("sousgraphe1\n" + subGraph1);
			System.out.println("sousgraphe2\n" + subGraph2);
			if (!willCreateACycle(subGraph1, e)) {
				//acm.addEdge(e);
				mergeSubGraphs(subGraph1, subGraph2, e);
				acm = subGraph1;
			}
		}
		return acm;
	}

	public void initACM() {
		sortEdgesByWeight();
		for (Vertice v : mainGraph.getVertices()) {
			String name = "subGraph(" + v.getName() + ")";
			Graph subGraph = new Graph(name , mainGraph.isWeighted());
			subGraph.addVertice(v);
			subGraphs.add(subGraph);
		}
	}
	
	private void mergeSubGraphs(Graph subGraph1, Graph subGraph2, Edge e) {
		for (Vertice vertice : subGraph2.getVertices()) {
			subGraph1.addVertice(vertice);
		}
		for (Edge edge : subGraph2.getEdges()) {
			subGraph1.addEdge(edge);
		}
		subGraph1.addEdge(e);
		subGraphs.remove(subGraph2);
	}

	/**
	 * @brief check if add edges to the acm will create a cycle
	 * @param edges list of edges to check
	 * @return TRUE if an edge induces the creation of a cycle, FALSE otherwise
	 */
	public boolean willCreateACycle(Graph g, Edge e) {
		if (g == null) {
			return true;
		}
		if (!g.getVertices().contains(e.getVertice1()) || !g.getVertices().contains(e.getVertice2())) {
			return false;
		}
		return true;
	}

	private void sortEdgesByWeight() {
		if (edgesNotInACM == null || edgesNotInACM.size() == 0) {
			return;
		}
		quickSort(0, edgesNotInACM.size() - 1);
	}

	private void quickSort(int lowerIndex, int higherIndex) {

		int i = lowerIndex;
		int j = higherIndex;

		// calculate pivot number, I am taking pivot as middle index number
		int pivot = edgesNotInACM.get(lowerIndex+(higherIndex-lowerIndex)/2).getValue();

		// Divide into two arrays
		while (i <= j) {
			/**
			 * In each iteration, we will identify a number from left side which 
			 * is greater then the pivot value, and also we will identify a number 
			 * from right side which is less then the pivot value. Once the search 
			 * is done, then we exchange both numbers.
			 */
			while (edgesNotInACM.get(i).getValue() < pivot) {
				i++;
			}
			while (edgesNotInACM.get(j).getValue() > pivot) {
				j--;
			}
			if (i <= j) {
				exchangeNumbers(i, j);
				//move index to next position on both sides
				i++;
				j--;
			}
		}
		// call quickSort() method recursively
		if (lowerIndex < j)
			quickSort(lowerIndex, j);
		if (i < higherIndex)
			quickSort(i, higherIndex);
	}

	private void exchangeNumbers(int i, int j) {
		Edge temp = edgesNotInACM.get(i);
		edgesNotInACM.set(i, edgesNotInACM.get(j));
		edgesNotInACM.set(j, temp);
	}

	public String getNameAlgo() {
		return "kruskal";
	}
}
