package GraphModel;

import java.util.ArrayList;

public class Vertice {
	
	private String name;
	private ArrayList<Edge> edges;
	
	public Vertice(String name)	{
		this.name = name;
		this.edges = new ArrayList<Edge>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Vertice [name=" + name + "]";
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
}
