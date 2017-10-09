package parquimetros;

import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import quick.dbtable.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VenPrincipal {

	private JFrame frame;
	private JTextField userBox;
	private JPasswordField pwdBox;
	protected Connection conexionBD = null;
	


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
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				
				try {
					if(conexionBD!=null)
					conexionBD.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "hubo error al cerrar todo");
				}
			}
		});
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				String usuario = userBox.getText();
				String password =new String(pwdBox.getPassword());
	            String baseDatos = "parquimetros";					// NO ESTOY SEGURO SI ES DATOS O PARQUIMETROS

	            
	            try {
				
	            	String uriConexion = "jdbc:mysql://" + "localhost:3306" + "/" + baseDatos;

	            	/*
	            	 * Si el usuario ingresado es ADMIN
	            	 */
					if(usuario.equals("admin") && password.equals("admin")) {
							
						VenConsultas ventanaAdmin = new VenConsultas();
						ventanaAdmin.setVisible(true);
						frame.setVisible(false);

					}
					/*
					 * Si el usuario ingresado es el legajo de un inspector valido
					 */
					else {
							
							conexionBD = DriverManager.getConnection(uriConexion, "inspector", "inspector");
							Statement stmt = conexionBD.createStatement();
					        String queryString = "SELECT * FROM Inspectores where legajo = "+usuario+" AND password = md5('"+password+"')";

					        ResultSet results = stmt.executeQuery(queryString);
					        if (results.first()) {
					        	conexionBD.close();
					           	VenInspector ventanaAdmin = new VenInspector(results.getString(1));
					           	ventanaAdmin.setVisible(true);
					           	frame.setVisible(false);

					        }
					        else {
					           	   JOptionPane.showMessageDialog(null, "Please Check Username and Password ");
					        }
					        results.close();
					}
							
				}catch (SQLException e) {

			        JOptionPane.showMessageDialog(null, "An Error ocurred trying to connect to the DATABASE");
			    } 
		    } 
		});
		btnLogin.setBounds(261, 109, 89, 23);
		frame.getContentPane().add(btnLogin);
		
		userBox = new JTextField();
		userBox.setText("Usuario");
		userBox.setBounds(74, 84, 86, 20);
		frame.getContentPane().add(userBox);
		userBox.setColumns(10);
		
		pwdBox = new JPasswordField("contrasena");
		pwdBox.setEchoChar('*');
		pwdBox.setToolTipText("Contrase\u00F1a");
		pwdBox.setBounds(74, 133, 86, 20);
		frame.getContentPane().add(pwdBox);
		pwdBox.setColumns(10);
	}
	
	
	private void conectarBD()
	   {
	      if (this.conexionBD == null)
	      {
	        
	    	  try
	         {
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	         }
	         catch (Exception ex)
	         {
	            System.out.println(ex.getMessage());
	         }
	        
	         try
	         {
	            String servidor = "localhost:3306";
	            String baseDatos = "parquimetros";
	            String usuario = userBox.getText();
	            String clave = pwdBox.getPassword().toString();
	            String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos;
	   
	            this.conexionBD = DriverManager.getConnection(uriConexion, usuario, clave);
	         }
	         catch (SQLException ex)
	         {
	        	
	            JOptionPane.showMessageDialog(null,
	                                          "Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(),
	                                          "Error",
	                                          JOptionPane.ERROR_MESSAGE);
	         }
	      }
	   }
}
