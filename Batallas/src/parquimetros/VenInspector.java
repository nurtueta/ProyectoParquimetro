package parquimetros;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mysql.jdbc.Statement;

import quick.dbtable.DBTable;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.JList;

public class VenInspector extends JFrame{

	private JFrame frame;

	private JButton btnIngresarPatente;
	private JButton btnIngresarParquimetro;
	private JButton btnPatente;
	private JButton btnParquimetro;
	private JTextField tfPatente;
	private JTextField tfCalle;
	private JTextField tfParquimetro;
	private JLabel lblPatente;
	private JLabel lblCalle;
	private JLabel lblParquimetro;
	private JList<String> listaPatente;
	private DefaultListModel<String> LP;
	
	private DBTable tabla;
	private String legajo;
	private String clave;
	private String txtConsulta;
	private String patente;
	private JTextField tfNumero;
	
	private Fechas fecha;
	
	public VenInspector(String u) 
	{
		super();
		this.legajo = u;
		initGUI();
	}
	/**
	 * Create the application.
	 */

	public void initGUI() {
		
		fecha=new Fechas();
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//desconectarBD();
			}
		});

		getContentPane().setLayout(null);
		
		btnIngresarPatente = new JButton("Ingresar Patente");
		btnIngresarPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPatente.setEnabled(true);
				tfPatente.setEnabled(true);
				btnIngresarParquimetro.setEnabled(true);
				btnIngresarPatente.setEnabled(false);
				crearTabla();
				conectarBD();
							
			}
		});
		
		
		btnIngresarPatente.setBounds(107, 12, 174, 25);
		getContentPane().add(btnIngresarPatente);
		
		btnIngresarParquimetro = new JButton("Ingresar Parquimetro");
		btnIngresarParquimetro.setEnabled(false);
		btnIngresarParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnPatente.setEnabled(false);
				tfPatente.setEnabled(false);
				btnParquimetro.setEnabled(true);
				tfParquimetro.setEnabled(true);
				tfCalle.setEnabled(true);
				tfNumero.setEnabled(true);
				btnIngresarParquimetro.setEnabled(false);
			}
		});
		btnIngresarParquimetro.setBounds(324, 12, 200, 25);
		getContentPane().add(btnIngresarParquimetro);
		
		btnPatente = new JButton("Ingresar");
		btnPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patente=tfPatente.getText();
				LP.addElement(patente);
				//permitir eliminar patentes
			}
		});
		btnPatente.setEnabled(false);
		btnPatente.setBounds(324, 49, 114, 25);
		getContentPane().add(btnPatente);
		
		btnParquimetro = new JButton("Ingresar");
		btnParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnIngresarPatente.setEnabled(true);
				btnParquimetro.setEnabled(false);
				tfCalle.setEnabled(false);
				tfNumero.setEnabled(false);
				tfParquimetro.setEnabled(false);
				//obtener ubicacion y parquimetro
				String calle=tfCalle.getText();
				String parquimetro=tfParquimetro.getText();
				String numero=tfNumero.getText();
				//obtener hora y turno de coneccion			
				//comprobar si se conecta el legajo en su determinado turno

				Statement st = null;
				ResultSet rs = null;
				try {         				
					st = (Statement) tabla.getConnection().createStatement();
					
					//obtengo dia de la semana en que se conecta
					String dia=null;
					rs=st.executeQuery("SELECT CURDATE();");
					rs.first();
					String fecha=Fechas.convertirDateAStringDB(rs.getDate(1));
					rs.close();
					rs=st.executeQuery("SELECT DAYOFWEEK('"+fecha+"');");
					rs.first();
					fecha=rs.getString(1);
					rs.close();
					switch (fecha) {
					case "1" : dia="Do";
								break;
					case "2" : dia="Lu";
								break;
					case "3" : dia="Ma";
								break;
					case "4" : dia="Mi";
								break;
					case "5" : dia="Ju";
								break;
					case "6" : dia="Vi";
								break;
					case "7" : dia="Sa";
								break;
					}
					
					//obtengo turno de ingreso
					String turno=null;
					rs=st.executeQuery("SELECT CURTIME();");
					rs.first();
					int hora=Integer.parseInt(rs.getString(1).substring(0,2));
					if((hora<=8) && hora<14)
						turno="M";
					else
						if(hora>=14 && hora<19)
							turno="T";
						else
							turno="T";//aca va mensaje diciendo que no se puede conectar en su turno
					rs.close();
					
					System.out.print(turno+dia+calle+legajo+numero);
					
					rs = st.executeQuery("SELECT * FROM Asociado_con WHERE legajo="+legajo+" AND"+
					"calle="+calle+" AND altura="+numero+" AND dia="+dia+" AND turno="+turno+";");
					if(rs.first()) {
						System.out.println(rs.getString(1));
					}else
						System.out.print("no se puede ingresar al turno");
					
					refrescarTabla();      			
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
				//crear multas
				//mostrar multas
				
				//vaciar tabla patentes
			}
		});
		btnParquimetro.setEnabled(false);
		btnParquimetro.setBounds(324, 102, 114, 25);
		getContentPane().add(btnParquimetro);
		
		tfPatente = new JTextField();
		tfPatente.setEnabled(false);
		tfPatente.setBounds(135, 55, 124, 19);
		getContentPane().add(tfPatente);
		tfPatente.setColumns(10);
		
		tfCalle = new JTextField();
		tfCalle.setEnabled(false);
		tfCalle.setBounds(135, 86, 124, 19);
		getContentPane().add(tfCalle);
		tfCalle.setColumns(10);
		
		tfParquimetro = new JTextField();
		tfParquimetro.setEnabled(false);
		tfParquimetro.setBounds(135, 148, 124, 19);
		getContentPane().add(tfParquimetro);
		tfParquimetro.setColumns(10);
		
		lblPatente = new JLabel("Patente:");
		lblPatente.setBounds(23, 54, 66, 15);
		getContentPane().add(lblPatente);
		
		lblCalle = new JLabel("Calle:");
		lblCalle.setBounds(23, 91, 103, 15);
		getContentPane().add(lblCalle);
		
		lblParquimetro = new JLabel("Parquimetro:");
		lblParquimetro.setBounds(23, 150, 103, 15);
		getContentPane().add(lblParquimetro);
		
		LP = new DefaultListModel<String>();
		listaPatente = new JList<String>(LP);
		listaPatente.setBounds(630, 12, 158, 365);
		getContentPane().add(listaPatente);
		
		JLabel lblNumero = new JLabel("Numero:");
		lblNumero.setBounds(23, 123, 66, 15);
		getContentPane().add(lblNumero);
		
		tfNumero = new JTextField();
		tfNumero.setEnabled(false);
		tfNumero.setBounds(135, 117, 124, 19);
		getContentPane().add(tfNumero);
		tfNumero.setColumns(10);

	}

	private void crearTabla() {
		// crea la tabla  
		tabla = new DBTable();
		tabla.setBounds(35, 150, 392, 375);

		// Agrega la tabla al frame (no necesita JScrollPane como Jtable)
		getContentPane().add(tabla);           

		// setea la tabla para sólo lectura (no se puede editar su contenido)  
		tabla.setEditable(false);       

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
			tabla.connectDatabase(driver, uriConexion, "inspector", "inspector");
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
			// seteamos la consulta a partir de la cual se obtendrán los datos para llenar la tabla
			tabla.setSelectSql(this.txtConsulta);

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
			// No es necesario establecer  una conexión, crear una sentencia y recuperar el 
			// resultado en un resultSet, esto lo hace automáticamente la tabla (DBTable) a 
			// patir  de  la conexión y la consulta seteadas con connectDatabase() y setSelectSql() respectivamente.



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
	
	private void salidaError(SQLException ex) {
		JOptionPane.showMessageDialog(null,
				ex.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
		System.out.println("SQLException: " + ex.getMessage());
		System.out.println("SQLState: " + ex.getSQLState());
		System.out.println("VendorError: " + ex.getErrorCode());
	}
}
