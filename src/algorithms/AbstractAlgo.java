package algorithms;

import GraphModel.Graph;

public abstract class AbstractAlgo implements IStrategyACM{

	protected Graph mainGraph;
	protected Graph acm;

	public void createACM() {
		acm = new Graph(getMainGraph().getName() + "ACM(" + getNameAlgo() + ")", mainGraph.isWeighted());
		acm.addVertice(mainGraph.getVertices().get(0));
	}

	public int getMin(int values[]) {
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
