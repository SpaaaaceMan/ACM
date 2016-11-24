package algorithms;

import graphModel.Graph;

public interface IStrategyACM {

	public Graph findACM(Graph graph);

	public String getNameAlgo();
	
	public void initACM();

}
