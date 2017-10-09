package parquimetros;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import com.mysql.jdbc.Statement;

import quick.dbtable.*;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent; 

@SuppressWarnings("serial")
public class VenConsultas extends javax.swing.JFrame 
{
	private JPanel pnlConsulta;
	private JTextArea txtConsulta;
	private JButton botonBorrar;
	private JButton btnEjecutar;
	private DBTable tabla;    
	private JScrollPane scrConsulta;
	private JList<String> list;
	private JList<String> list_1;
	private DefaultListModel<String> DLM;
	private DefaultListModel<String> DLM_1;
	private JButton btnRefrescar;

	private String usuario;
	private String clave;
	private JButton btnNewButton;
	
	
	public static void main(String[] args) {

		VenConsultas inst = new VenConsultas();
		inst.setLocationRelativeTo(null);
		inst.setVisible(true);

	}


	public VenConsultas() 
	{
		super();
		initGUI();
	}

	private void initGUI() 
	{
		try {
			setPreferredSize(new Dimension(800, 600));
			this.setBounds(0, 0, 800, 600);
			setVisible(true);
			this.setTitle("Consultas");
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					desconectarBD();
				}
			});
			getContentPane().setLayout(null);

			btnNewButton = new JButton("Reconectar");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					desconectarBD();
					conectarBD();
				}
			});
			{
				btnRefrescar = new JButton();
				btnRefrescar.setEnabled(false);
				btnRefrescar.setBounds(591, 186, 101, 29);
				getContentPane().add(btnRefrescar);
				btnRefrescar.setText("Refrescar");
				btnRefrescar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DLM.clear();     
						Statement st = null;
						ResultSet rs = null;
						try {         				
							st = (Statement) tabla.getConnection().createStatement();
							rs = st.executeQuery("SELECT table_name FROM "
									+ "information_schema.tables where "
									+ "table_schema='parquimetros'");
							boolean sig = rs.first();
							while(sig) {
								DLM.addElement(rs.getString(1));
								sig = rs.next();												
							}      			
						} catch (SQLException ex) {
							salidaError(ex);
						} catch (NullPointerException ex2){
							JOptionPane.showMessageDialog(null,
									"Error",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
						finally {					
							if(st != null) {
								try {
									st.close();
								} catch (SQLException ex) {
									salidaError(ex);
								}
							}
							if(rs != null) {
								try {
									rs.close();
								} catch (SQLException ex) {
									salidaError(ex);
								}
							}
						}
					}
				});
			}
			btnNewButton.setBounds(480, 186, 101, 29);
			getContentPane().add(btnNewButton);
			{
				pnlConsulta = new JPanel();
				pnlConsulta.setBounds(0, 0, 784, 186);
				getContentPane().add(pnlConsulta);
				{
					scrConsulta = new JScrollPane();
					pnlConsulta.add(scrConsulta);
					{
						txtConsulta = new JTextArea();
						scrConsulta.setViewportView(txtConsulta);
						txtConsulta.setTabSize(3);
						txtConsulta.setColumns(80);
						txtConsulta.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
						txtConsulta.setText("Ingrese aqui su consulta");
						txtConsulta.setFont(new java.awt.Font("Monospaced",0,12));
						txtConsulta.setRows(10);
					}
				}
				{
					btnEjecutar = new JButton();
					btnEjecutar.setEnabled(false);
					pnlConsulta.add(btnEjecutar);
					btnEjecutar.setText("Ejecutar");
					btnEjecutar.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							btnEjecutarActionPerformed(evt);
						}
					});
				}
				{
					botonBorrar = new JButton();
					pnlConsulta.add(botonBorrar);
					botonBorrar.setText("Borrar");            
					botonBorrar.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							txtConsulta.setText("");            			
						}
					});
				}	
			}
			{
				// crea la tabla  
				tabla = new DBTable();
				tabla.setBounds(0, 186, 392, 375);

				// Agrega la tabla al frame (no necesita JScrollPane como Jtable)
				getContentPane().add(tabla);           

				// setea la tabla para s�lo lectura (no se puede editar su contenido)  
				tabla.setEditable(false);       



			}

			JPanel panel = new JPanel();
			panel.setBounds(392, 231, 392, 330);
			getContentPane().add(panel);
			panel.setLayout(null);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 0, 194, 330);
			panel.add(scrollPane);

			DLM = new DefaultListModel<String>();
			list = new JList<String>(DLM);               
			list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					DLM_1.clear();
					if(DLM.getSize()> 0) {         			
						String selected = (String) list.getSelectedValue();
						selected = "'"+selected+"'";
						Statement st = null;
						ResultSet rs = null;
						try {
							st = (Statement) tabla.getConnection().createStatement();
							rs = st.executeQuery("SELECT COLUMN_NAME FROM "
									+ "INFORMATION_SCHEMA.COLUMNS "
									+ "WHERE TABLE_SCHEMA='parquimetros' "
									+ "AND TABLE_NAME="+selected );
							boolean sig = rs.first();
							while(sig) {
								DLM_1.addElement(rs.getString(1));
								sig = rs.next();
							}
						} catch (SQLException ex) {
							salidaError(ex);
						}  finally {
							if(st != null) {
								try {
									st.close();
								} catch (SQLException ex) {
									salidaError(ex);
								}
							}
							if(rs != null) {
								try {
									rs.close();
								} catch (SQLException ex) {
									salidaError(ex);
								}
							}
						}         			        			
					}  
				}				
			});			
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			scrollPane.setViewportView(list);

			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(197, 0, 194, 330);
			panel.add(scrollPane_1);

			DLM_1 = new DefaultListModel<String>();
			list_1 = new JList<String>(DLM_1);
			scrollPane_1.setViewportView(list_1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		conectarBD();

	}

	private void salidaError(SQLException ex) {
		JOptionPane.showMessageDialog(null,
				ex.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
		System.out.println("SQLException: " + ex.getMessage());
		System.out.println("SQLState: " + ex.getSQLState());
		System.out.println("VendorError: " + ex.getErrorCode());
	}

	private void btnEjecutarActionPerformed(ActionEvent evt) 
	{
		this.refrescarTabla();      
	}

	private void conectarBD()
	{
		try
		{
			String driver ="com.mysql.jdbc.Driver";
			String servidor = "localhost:3306";
			String baseDatos = "parquimetros";
			String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos;

			//establece una conexi�n con la  B.D. "parquimetros"  usando directamante una tabla DBTable    
			tabla.connectDatabase(driver, uriConexion, usuario, clave);
			btnEjecutar.setEnabled(true);
			btnRefrescar.setEnabled(true);
		}
		catch (SQLException ex)
		{
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			btnEjecutar.setEnabled(false);
			btnRefrescar.setEnabled(false);
		}
		catch (ClassNotFoundException e)
		{			
			e.printStackTrace();
		}

	}


	private void desconectarBD()
	{
		try
		{
			tabla.close();            
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}      
	}

	private void refrescarTabla()
	{
		try
		{    
			// seteamos la consulta a partir de la cual se obtendr�n los datos para llenar la tabla
			tabla.setSelectSql(this.txtConsulta.getText().trim());

			// obtenemos el modelo de la tabla a partir de la consulta para 
			// modificar la forma en que se muestran de algunas columnas  
			tabla.createColumnModelFromQuery();    	    
			for (int i = 0; i < tabla.getColumnCount(); i++)
			{ // para que muestre correctamente los valores de tipo TIME (hora)  		   		  
				if	 (tabla.getColumn(i).getType()==Types.TIME)  
				{    		 
					tabla.getColumn(i).setType(Types.CHAR);  
				}
				// cambiar el formato en que se muestran los valores de tipo DATE
				if	 (tabla.getColumn(i).getType()==Types.DATE)
				{
					tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
				}
			}  
			// actualizamos el contenido de la tabla.   	     	  
			tabla.refresh();
			// No es necesario establecer  una conexi�n, crear una sentencia y recuperar el 
			// resultado en un resultSet, esto lo hace autom�ticamente la tabla (DBTable) a 
			// patir  de  la conexi�n y la consulta seteadas con connectDatabase() y setSelectSql() respectivamente.



		}
		catch (SQLException ex)
		{
			// en caso de error, se muestra la causa en la consola
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
					ex.getMessage() + "\n", 
					"Error al ejecutar la consulta.",
					JOptionPane.ERROR_MESSAGE);

		}

	}



}
