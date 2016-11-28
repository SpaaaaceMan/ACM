package GraphModel;

import java.util.ArrayList;
import java.util.Stack;

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

	public int computeUltrametric(Vertice start, Vertice arrival) {
		// All already visited AbstractLink
		ArrayList<Edge> visitedLinks = new ArrayList<Edge>();
		// The current path
		Stack<Edge> path = new Stack<Edge>();
		Vertice currentVertice = start;

		// We search the path from origin to dest
		while (currentVertice != arrival) {
			boolean mustBack = true;

			// Find the first valid AbstractLink
			for (Edge edge : currentVertice.getEdges()) {
				// If this link is not the path neither the already visited links then go through it
				// and we don't need to go back
				if (!visitedLinks.contains(edge) && !path.contains(edge)) {
					currentVertice = (edge.getVertice1() == currentVertice ? edge.getVertice2() : edge.getVertice1());
					path.add(edge);
					mustBack = false;
					break;      
				}     
			}

			// We go back if we havn't find a valid link to go through
			if (mustBack) {
				Edge lastEdge  = path.pop();
				currentVertice = (lastEdge.getVertice1() == currentVertice ? lastEdge.getVertice2() : lastEdge.getVertice1());
				visitedLinks.add(lastEdge);
			}
		}

		// Then we simply get the max weight of the found path.
		int ultrametric = path.pop().getValue();
		System.out.println(ultrametric);
		for (Edge edge : path) {
			if (edge.getValue() > ultrametric) ultrametric = edge.getValue();
		}
		return ultrametric;
	}

}
