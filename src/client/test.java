package client;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
/*
 * (C) Copyright 2003-2016, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.security.auth.callback.ChoiceCallback;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.vertexcover.EdgeBasedTwoApproxVCImpl;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;

import GraphModel.Edge;
import GraphModel.Graph;
import GraphModel.Vertice;
import algorithms.IStrategyACM;
import algorithms.StrategyKruskal;
import algorithms.StrategyPrim;

/**
 * A demo applet that shows how to use JGraph to visualize JGraphT graphs.
 *
 * @author Barak Naveh
 * @since Aug 3, 2003
 */
public class test extends JApplet
{
    private static final long serialVersionUID = 3256444702936019250L;
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
    
    private JGraph jgraph;
    private ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge> g;
    private JGraphModelAdapter<String, DefaultWeightedEdge> jgAdapter;
    
    private ArrayList<String> verticesForView = new ArrayList<String>();
    private MyFrame frame;
    
    public test(final Graph graph) {
    	convertMyGraph(graph);

        frame = new MyFrame(graph.getName());
        JPanel panelNorth = new JPanel();
        JPanel panelWest = new JPanel();
        panelNorth.setLayout(new FlowLayout());
        panelWest.setLayout(new BoxLayout(panelWest, BoxLayout.PAGE_AXIS));
        
        JButton newGraph = new JButton("Generate new graph");
        JButton getACM = new JButton("Get ACM");
        JButton getUltra = new JButton("Get Ultrametric");

        //ACM
        JPanel algos = new JPanel();
        algos.setLayout(new BoxLayout(algos, BoxLayout.PAGE_AXIS));
        algos.setBorder(BorderFactory.createTitledBorder("Algoritms"));
        ButtonGroup AlgoGroup = new ButtonGroup();
        final JRadioButton checkBox = new JRadioButton("Prim", true);
        final JRadioButton checkBox2 = new JRadioButton("Kruskhal");
        final JRadioButton checkBox3 = new JRadioButton("Prim Improved");
        AlgoGroup.add(checkBox);
        AlgoGroup.add(checkBox2);
        AlgoGroup.add(checkBox3);
        algos.add(checkBox);
        algos.add(checkBox2);
        algos.add(checkBox3);
        algos.add(getACM);
        
        //Ultrametric
        JPanel ultrametric = new JPanel();
        ultrametric.setLayout(new BoxLayout(ultrametric, BoxLayout.PAGE_AXIS));
        ultrametric.setBorder(BorderFactory.createTitledBorder("Ultrametric"));
        final JTextField firstVertex = new JTextField();
        firstVertex.setToolTipText("Start vertex");
        firstVertex.setMaximumSize(new Dimension(200, 30));
        final JTextField secondVertex = new JTextField();
        secondVertex.setToolTipText("Arrival vertex");
        secondVertex.setMaximumSize(new Dimension(200, 30));
        final JLabel result = new JLabel("Max Weight : ?");
        ultrametric.add(firstVertex);
        ultrametric.add(secondVertex);
        ultrametric.add(getUltra);
        ultrametric.add(result);

        //New graph
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(5, 2, 100, 1));
        spinner.setMaximumSize(new Dimension(100, 30));
        panelWest.add(spinner);
        panelWest.add(newGraph);
        panelWest.add(algos);
        panelWest.add(ultrametric);
        
        //effets boutons
        getACM.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IStrategyACM strat = null;
				if (checkBox.isSelected()) {
					System.out.println("Prim");
					strat = new StrategyPrim();
				}
				else if (checkBox2.isSelected()) {
					System.out.println("kruskal");
					strat = new StrategyKruskal();
				}
				else if (checkBox3.isSelected()) {
					System.out.println("improved");
				}
				test acm = new test(strat.findACM(graph));
			}
		});
        
        newGraph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				test random = new test((int) spinner.getValue());
			}
		});
        
        getUltra.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(verticesForView.toString());
				String v1 = firstVertex.getText();
				String v2 = secondVertex.getText();
				if (verticesForView.contains(v1) && verticesForView.contains(v2)) {
					Vertice start = graph.getVertice(v1);
					Vertice arrival = graph.getVertice(v2);
					graph.computeUltrametric(start, arrival);
					//result.setText("Max Weight : " + graph.computeUltrametric(start, arrival));
					System.out.println("yessss");
				}
				else
					result.setText("Max Weight : Bad vertex's name");
			}
		});

        //frame.add(panelNorth, BorderLayout.NORTH);
        frame.add(panelWest, BorderLayout.WEST);
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.pack();
    }
    
    public test(int nbVertices) {
    	Graph graph = generateRandomCompleteGraph(nbVertices);
    	test t = new test(graph);
    }
    
    private Graph generateRandomCompleteGraph(int nbVertices) {
    	Graph graph = new Graph("Random Graph", true);
    	
    	// create a JGraphT graph
    	g = new ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
                                
        for (int i = 1; i <= nbVertices; i++) {
        	String vertex = "v" + i;
        	graph.addVertice(new Vertice(vertex));
		}

    	CompleteGraphGenerator<String, DefaultWeightedEdge> generator = new CompleteGraphGenerator<String, DefaultWeightedEdge>(nbVertices);
    	generator.generateGraph(g, new ClassBasedVertexFactory<String>(String.class), null);
    	g.removeVertex("");
    	
    	for (int i = 0; i < graph.getVertices().size(); i++) {
			for (int j = 0; j < graph.getVertices().size(); j++) {
				if (graph.getVertices().get(i) != graph.getVertices().get(j)) {
					graph.addEdge(graph.getVertices().get(i), graph.getVertices().get(j));
				}
				System.out.println(graph.getVertices().get(j).getEdges().toString());
			}
		}
		return graph;
    }
    
    private boolean areVerticesSuperposed(String v1, String v2) {
    	Rectangle2D v1Bounds = getCellBounds(v1);
    	Rectangle2D v2Bounds = getCellBounds(v2);
    	double x1 = v1Bounds.getX();
    	double x2 = v2Bounds.getX();
    	double y1 = v1Bounds.getY();
    	double y2 = v2Bounds.getY();
    	
    	//si au même niveau horizontal
    	if (x2 > x1 && x2 < x1 + v1Bounds.getWidth()) {
			//si au même niveau vertical
    		if (y2 > y1 && y2 < y1 + v1Bounds.getHeight()) {
				return true;
			}
		}
    	return false;
    }
    
    public JGraph convertMyGraph(Graph myGraph) {
    	// create a JGraphT graph
    	g = new ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    	
        // create a visualization using JGraph, via an adapter
        jgAdapter = new JGraphModelAdapter<String, DefaultWeightedEdge>(g);

        jgraph = new JGraph(jgAdapter);

        //adjustDisplaySettings(jgraph);
        getContentPane().add(jgraph);
        //resize(DEFAULT_SIZE);}
                        
        for (Edge e : myGraph.getEdges()) {
        	String v1 = e.getVertice1().getName();
        	String v2 = e.getVertice2().getName();
        	
        	placeVertex(v1);
        	placeVertex(v2);
        	
        	if(!verticesForView.contains(v1))
        		verticesForView.add(v1);
        	if(!verticesForView.contains(v2))
        		verticesForView.add(v2);
        	
        	DefaultWeightedEdge edge = new DefaultWeightedEdge();
        	g.addEdge(v1, v2, edge);
        	g.setEdgeWeight(edge, e.getValue());
        }
        jgraph.setEdgeLabelsMovable(false);
        return jgraph;
    }
    
    private double getRandomX(Rectangle2D bounds) {
    	Random rand = new Random();
    	double cellWidth = bounds.getWidth();
        return rand.nextInt((int) ((800 - cellWidth)));
    }
    
    private double getRandomY(Rectangle2D bounds) {
    	Random rand = new Random();
    	double cellHeight = bounds.getHeight();
        return rand.nextInt((int) ((600 - cellHeight)));
    }
    
    private void placeVertex(String vertex) {
    	g.addVertex(vertex);
        Rectangle2D bounds = getCellBounds(vertex);
    	positionVertexAt(vertex, getRandomX(bounds), getRandomY(bounds));
        
        for (String v : verticesForView) {
        	while (areVerticesSuperposed(v, vertex)) {
            	positionVertexAt(vertex, getRandomX(bounds), getRandomY(bounds));
    		}
		}  
    }
    
    private Rectangle2D getCellBounds(String vertex) {
    	DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        return GraphConstants.getBounds(attr);
    }

    private void adjustDisplaySettings(JGraph jg)
    {
        jg.setPreferredSize(DEFAULT_SIZE);

        Color c = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter("bgcolor");
        } catch (Exception e) {
        }

        if (colorStr != null) {
            c = Color.decode(colorStr);
        }

        jg.setBackground(c);
    }

    @SuppressWarnings("unchecked") // FIXME hb 28-nov-05: See FIXME below
    private void positionVertexAt(Object vertex, double x, double y)
    {
        DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        Rectangle2D bounds = GraphConstants.getBounds(attr);

        Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight());

        GraphConstants.setBounds(attr, newBounds);

        // TODO: Clean up generics once JGraph goes generic
        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, attr);
        jgAdapter.edit(cellAttr, null, null, null);
    }

    /**
     * a listenable directed multigraph that allows loops and parallel edges.
     */
    private static class ListenableDirectedMultigraph<V, E> extends DefaultListenableGraph<V, E>
        implements DirectedGraph<V, E>
    {
        private static final long serialVersionUID = 1L;

        ListenableDirectedMultigraph(Class<E> edgeClass)
        {
            super(((DirectedGraph<V, E>) new DirectedMultigraph<Object, E>(edgeClass)));
        }
    }
}

// End JGraphAdapterDemo.java