package parquimetros;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Types;
import java.sql.PreparedStatement;
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
import javax.swing.text.BadLocationException;
import javax.swing.event.ListSelectionEvent;
import java.awt.Color; 

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

	private String usuario;
	private String clave;
	public VenConsultas() 
	{
		super();
		getContentPane().setBackground(new Color(255, 153, 102));
		usuario = "admin";
		clave = "admin";
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

			this.setTitle("Parquimetro Landau-Urtueta-Vazquez");
			setIconImage(Toolkit.getDefaultToolkit().getImage(VenPrincipal.class.getResource("/imagenes/logoParquimetro.jpg")));

			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					desconectarBD();
				}
			});
			getContentPane().setLayout(null);
			{
				pnlConsulta = new JPanel();
				pnlConsulta.setBackground(new Color(255, 153, 102));
				pnlConsulta.setBounds(0, 0, 784, 186);
				getContentPane().add(pnlConsulta);
				pnlConsulta.setLayout(null);
				{
					scrConsulta = new JScrollPane();
					scrConsulta.setBounds(60, 5, 566, 176);
					pnlConsulta.add(scrConsulta);
					{
						txtConsulta = new JTextArea();
						scrConsulta.setColumnHeaderView(txtConsulta);
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
					btnEjecutar.setBounds(647, 11, 101, 29);
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
					botonBorrar.setBounds(647, 51, 101, 29);
					pnlConsulta.add(botonBorrar);
					botonBorrar.setText("Borrar");

					JButton btnNewButton_1 = new JButton("Atras");
					btnNewButton_1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {

							desconectarBD();
							setVisible(false);
							String [] args = null;
							VenPrincipal.main(args);;

						}
					});
					btnNewButton_1.setBounds(0, 0, 60, 60);
					pnlConsulta.add(btnNewButton_1);





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
				tabla.setBackground(new Color(255, 153, 102));
				tabla.getTable().setBackground(new Color(204, 255, 204));
				tabla.setBounds(0, 186, 392, 375);

				// Agrega la tabla al frame (no necesita JScrollPane como Jtable)
				getContentPane().add(tabla);           

				// setea la tabla para sólo lectura (no se puede editar su contenido)  
				tabla.setEditable(false);       



			}

			JPanel panel = new JPanel();
			panel.setBounds(392, 186, 392, 375);
			getContentPane().add(panel);
			panel.setLayout(null);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 0, 194, 375);
			panel.add(scrollPane);

			DLM = new DefaultListModel<String>();
			list = new JList<String>(DLM);               
			list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					mostrarColumnas();
				}				
			});			
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			scrollPane.setViewportView(list);

			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(197, 0, 194, 375);
			panel.add(scrollPane_1);

			DLM_1 = new DefaultListModel<String>();
			list_1 = new JList<String>(DLM_1);
			scrollPane_1.setViewportView(list_1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		conectarBD();
		mostrarTablas();

	}

	private void mostrarTablas() {
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

	public void mostrarColumnas() {
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

			//establece una conexión con la  B.D. "parquimetros"  usando directamante una tabla DBTable    
			tabla.connectDatabase(driver, uriConexion, usuario, clave);
			btnEjecutar.setEnabled(true);
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

			String comando = new String(txtConsulta.getText());
			if(txtConsulta.getText(0, 6).toLowerCase().equals("select")){// Si el comando no modifica la base de datos, ingresa aqui.							 
				// seteamos la consulta a partir de la cual se obtendrán los datos para llenar la tabla
				tabla.setSelectSql(this.txtConsulta.getText().trim());		
			}
			else { //modifica la base de datos				
				PreparedStatement pstmt = tabla.getConnection().prepareStatement(comando);
				pstmt.execute();			
				String selec = list.getSelectedValue();
				mostrarTablas();
				list.setSelectedIndex(DLM.indexOf(selec));
			}
			if(tabla.getSelectSql() != null) {
				tabla.createColumnModelFromQuery();    	    
				for (int i = 0; i < tabla.getColumnCount(); i++)
				{	   		  
					if	 (tabla.getColumn(i).getType()==Types.TIME)  
					{    		 
						tabla.getColumn(i).setType(Types.CHAR);  
					}
					if	 (tabla.getColumn(i).getType()==Types.DATE)
					{
						tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
					}
				}				
				tabla.refresh();
			}
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

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
