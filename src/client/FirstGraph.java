package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;

public class FirstGraph<E> extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JGraph myGraph;
	private JScrollPane scrollPane;
	private JGraphModelAdapter m_jgAdapter;

	public FirstGraph() {
		//myGraph = new JGraph();
		// create a JGraphT graph
		ListenableGraph g = new ListenableUndirectedGraph(DefaultEdge.class);
		
		this.setSize(600, 600);

		// create a visualization using JGraph, via an adapter
		m_jgAdapter = new JGraphModelAdapter(g);

		myGraph = new JGraph( m_jgAdapter );
		myGraph.setLayout(new GridLayout());

		// add some sample data (graph manipulated via JGraphT)
		g.addVertex( "v1" );
		g.addVertex( "v2" );
		g.addVertex( "v3" );
		g.addVertex( "v4" );

		g.addEdge( "v1", "v2" );
		g.addEdge( "v2", "v3" );
		g.addEdge( "v3", "v1" );
		g.addEdge( "v4", "v3" );
		
		myGraph.setGridEnabled(true);
		myGraph.setGridColor(Color.blue);
		myGraph.setGridVisible(true);
		myGraph.setHighlightColor(Color.green);
		myGraph.setMoveIntoGroups(false);
		
		//TO DO positionner les sommets.
		// position vertices nicely within JGraph component
		/*positionVertexAt( "v1", 130, 40 );
		positionVertexAt( "v2", 60, 200 );
		positionVertexAt( "v3", 310, 230 );
		positionVertexAt( "v4", 380, 70 );*/

		scrollPane = new JScrollPane(myGraph);

		this.setTitle("First JGraph");
		WindowListener winl = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("Sayonara...");
				System.exit(0);
			}
		};
		this.addWindowListener(winl);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		//this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void positionVertexAt(Object vertex, int x, int y) {
		DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
		Map              attr = cell.getAttributes();
		Rectangle        b    = (Rectangle) GraphConstants.getBounds(attr);

		GraphConstants.setBounds(attr, new Rectangle(x, y, b.width, b.height));

		Map<DefaultGraphCell, Map> cellAttr = new HashMap<DefaultGraphCell, Map>();
		cellAttr.put(cell, attr);
		m_jgAdapter.edit(cellAttr, null, null, null);
	}

	public void actionPerformed(ActionEvent e) {
	}

	public static void main(String argvs[]) {
		FirstGraph thisUI = new FirstGraph();
		System.out.println("My First JGraph is successfully constructed.");
	}
}