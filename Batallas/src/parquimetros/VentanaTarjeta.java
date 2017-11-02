package parquimetros;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class VentanaTarjeta extends JFrame{

	public VentanaTarjeta() 
	{
		super();
		getContentPane().setBackground(new Color(204, 255, 153));
		initGUI();
	}

	/**
	 * Create the application.
	 */
	public void initGUI() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

}
