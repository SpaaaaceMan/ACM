package GraphModel;

import java.util.ArrayList;

public class Graph {

	private String name;
	private ArrayList<Vertice> vertices;
	private ArrayList<Edge> edges;
	private boolean isWeighted;

	public Graph(String name, boolean isWeighted) {
		super();
		this.name = name;
		this.vertices = new ArrayList<Vertice>();
		this.edges = new ArrayList<Edge>();
		this.isWeighted = isWeighted;
	}

	public void addVertice(Vertice v) {
		if (vertices.contains(v)) return;
		vertices.add(v);
	}

	public void addEdge(Edge e) {
		if (edges.contains(e)) return;
		if (!vertices.contains(e.getVertice1())) {
			vertices.add(e.getVertice1());
		}
		if (!vertices.contains(e.getVertice2())) {
			vertices.add(e.getVertice2());
		}
		edges.add(e);
	}

	public void addEdge(Vertice v1, Vertice v2) {
		Edge e = new Edge("a" + edges.size(), v1, v2);
		addEdge(e);
	}

	public void addEdge(Vertice v1, Vertice v2, int value) {
		Edge e = new Edge("a" + edges.size(), v1, v2, value);
		addEdge(e);
	}

	public Vertice getVertice(String name) {
		for (Vertice v : vertices) {
			if (v.getName() == name) {
				return v;
			}
		}
		return null;
	}

	public Edge getEdge(String name) {
		for (Edge e : edges) {
			if (e.getName() == name) {
				return e ;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Vertice> getVertices() {
		return vertices;
	}

	public void setVertices(ArrayList<Vertice> vertices) {
		this.vertices = vertices;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}

	@Override
	public String toString() {
		return name + " : " + vertices.size() + " vertices & " + edges.size() 
				+ " edges.\n" + vertices + edges;
	}

	public boolean isWeighted() {
		return isWeighted;
	}

	public void setWeighted(boolean isWeighted) {
		this.isWeighted = isWeighted;
	}

}
