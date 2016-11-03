package algorithms;

import java.util.ArrayList;

import GraphModel.Edge;
import GraphModel.Graph;

public class StrategyKruskal extends AbstractAlgo {

	public Graph findACM(Graph graph) {
		setMainGraph(graph);
		createACM();
		for (Edge e : edgesNotInACM) {
			
		}
		return null;
	}

	public void initACM() {
		sort();
		Edge first = edgesNotInACM.get(0);
		acm.addEdge(first);
		edgesNotInACM.remove(first);
	}
	
	public boolean isACycle(ArrayList<Edge> edges) {
		for (Edge e : edges) {
			if (!acm.getVertices().contains(e.getVertice1()) || !acm.getVertices().contains(e.getVertice2())) {
				return false;
			}
		}
		return true;
	}

	public void sort() {
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
