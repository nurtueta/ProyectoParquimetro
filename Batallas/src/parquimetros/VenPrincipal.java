package parquimetros;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextField;

public class VenPrincipal {

	private JFrame frame;
	private JTextField txtUsuario;
	private JPasswordField pwdBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VenPrincipal window = new VenPrincipal();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VenPrincipal() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(261, 109, 89, 23);
		frame.getContentPane().add(btnLogin);
		
		txtUsuario = new JTextField();
		txtUsuario.setText("Usuario");
		txtUsuario.setBounds(74, 84, 86, 20);
		frame.getContentPane().add(txtUsuario);
		txtUsuario.setColumns(10);
		
		pwdBox = new JPasswordField();
		pwdBox.setEchoChar('*');
		pwdBox.setToolTipText("Contrase\u00F1a");
		pwdBox.setBounds(74, 133, 86, 20);
		frame.getContentPane().add(pwdBox);
		pwdBox.setColumns(10);
	}
}
