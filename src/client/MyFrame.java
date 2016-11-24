package client;

import javax.swing.JFrame;

public class MyFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493343746358969695L;

	public MyFrame() {
		setTitle("Mon affichage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
	}
}
