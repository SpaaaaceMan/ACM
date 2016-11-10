package GraphModel;

public class Edge {

	private String name;
	private int value;

	private Vertice vertice1;
	private Vertice vertice2;

	public Edge(String name, Vertice vertice1, Vertice vertice2, int value) {
		this.name = name;
		this.value = value;
		this.vertice1 = vertice1;
		this.vertice2 = vertice2;
		vertice1.getEdges().add(this);
		vertice2.getEdges().add(this);
	}

	public Edge(String name, Vertice vertice1, Vertice vertice2) {
		super();
		this.name = name;
		this.value = 1;
		this.vertice1 = vertice1;
		this.vertice2 = vertice2;
		vertice1.getEdges().add(this);
		vertice2.getEdges().add(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Vertice getVertice1() {
		return vertice1;
	}

	public void setVertice1(Vertice vertice1) {
		this.vertice1 = vertice1;
	}

	public Vertice getVertice2() {
		return vertice2;
	}

	public void setVertice2(Vertice vertice2) {
		this.vertice2 = vertice2;
	}

	@Override
	public String toString() {
		return "\nEdge " + name + ", value = " + value + ", vertice1 = "
				+ vertice1 + ", vertice2 = " + vertice2;
	}
}
