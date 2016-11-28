package client;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MyFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493343746358969695L;

	public MyFrame(String title) {
		setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
	}
}
