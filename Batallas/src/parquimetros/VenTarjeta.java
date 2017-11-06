package parquimetros;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import quick.dbtable.DBTable;
import javax.swing.JLabel;
import javax.swing.JButton;

public class VenTarjeta extends JFrame{

	
	private DBTable tabla;
	
	private JComboBox boxUbicacion;
	private JComboBox boxNumero;
	private JComboBox boxParquimetro;
	private JComboBox boxTarjetas;
	
	private JList<String> listaPatente;
	private DefaultListModel<String> LP;
	
	private Vector<String> ubicaciones;
	private Vector<Integer> numeros;
	private Vector<Integer> parquimetros;
	private Vector<Integer> tarjetas;
	
	private JScrollPane scrollPane;
	
	private String txtConsulta;
	
	public VenTarjeta() 
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
		setVisible(true);
		getContentPane().setLayout(null);
		
		crearTabla();
		crearOtros();
	}
	
	private void crearOtros() {
		LP = new DefaultListModel<String>();
		listaPatente = new JList<String>(LP);
		
		scrollPane = new JScrollPane(listaPatente);
		scrollPane.setBounds(578, 11, 167, 325);
		getContentPane().add(scrollPane);
		
		ubicaciones=new Vector<String>();
		boxUbicacion = new JComboBox(ubicaciones);
		boxUbicacion.setEnabled(false);
		boxUbicacion.setBounds(135, 47, 124, 24);
		getContentPane().add(boxUbicacion);
		
		numeros=new Vector<Integer>();
		boxNumero = new JComboBox(numeros);
		boxNumero.setEnabled(false);
		boxNumero.setBounds(135, 79, 124, 24);
		getContentPane().add(boxNumero);
		
		parquimetros=new Vector<Integer>();
		boxParquimetro = new JComboBox(parquimetros);
		boxParquimetro.setEnabled(false);
		boxParquimetro.setBounds(135, 111, 124, 25);
		getContentPane().add(boxParquimetro);
		
		tarjetas=new Vector<Integer>();
		boxTarjetas = new JComboBox(tarjetas);
		boxTarjetas.setEnabled(false);
		boxTarjetas.setBounds(135, 111, 124, 25);
		getContentPane().add(boxTarjetas);
		
		JLabel lblCalle = new JLabel("Calle");
		lblCalle.setBounds(31, 47, 66, 15);
		getContentPane().add(lblCalle);
		
		JLabel lblNumero = new JLabel("Numero");
		lblNumero.setBounds(31, 84, 66, 15);
		getContentPane().add(lblNumero);
		
		JLabel lblParquimetro = new JLabel("Parquimetro");
		lblParquimetro.setBounds(31, 116, 102, 15);
		getContentPane().add(lblParquimetro);
		
		JLabel lblSeleccionarUbicacion = new JLabel("Seleccionar ubicacion");
		lblSeleccionarUbicacion.setBounds(135, 13, 191, 15);
		getContentPane().add(lblSeleccionarUbicacion);
		
		JButton btnIngresarTarjeta = new JButton("Ingresar Tarjeta");
		btnIngresarTarjeta.setBounds(112, 149, 191, 25);
		getContentPane().add(btnIngresarTarjeta);
		
	}
	

	private void crearTabla() {
		tabla = new DBTable();
		tabla.setBounds(35, 250, 500, 300);
		//getContentPane().add(tabla);           
		//tabla.setEditable(false);       
	}
	
	private void conectarBD(){
		try{
			String driver ="com.mysql.jdbc.Driver";
			String servidor = "localhost:3306";
			String baseDatos = "parquimetros";
			String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos;

			//establece una conexión con la  B.D. "parquimetros"  usando directamante una tabla DBTable    
			tabla.connectDatabase(driver, uriConexion, "inspector", "inspector");
		}
		catch (SQLException ex){
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		catch (ClassNotFoundException e){			
			e.printStackTrace();
		}
	}

	private void desconectarBD(){
		try{
			tabla.close();            
		}
		catch (SQLException ex){
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}      
	}

	private void refrescarTabla(){
		try{    
			// seteamos la consulta a partir de la cual se obtendrán los datos para llenar la tabla
			tabla.setSelectSql(this.txtConsulta);

			// obtenemos el modelo de la tabla a partir de la consulta para 
			// modificar la forma en que se muestran de algunas columnas  
			tabla.createColumnModelFromQuery();    	    
			for (int i = 0; i < tabla.getColumnCount(); i++){ 
				// para que muestre correctamente los valores de tipo TIME (hora)  		   		  
				if	 (tabla.getColumn(i).getType()==Types.TIME) {    		 
					tabla.getColumn(i).setType(Types.CHAR);  
				}
				// cambiar el formato en que se muestran los valores de tipo DATE
				if	 (tabla.getColumn(i).getType()==Types.DATE){
					tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
				}
			}  
			// actualizamos el contenido de la tabla.   	     	  
			tabla.refresh();
			// No es necesario establecer  una conexión, crear una sentencia y recuperar el 
			// resultado en un resultSet, esto lo hace automáticamente la tabla (DBTable) a 
			// patir  de  la conexión y la consulta seteadas con connectDatabase() y setSelectSql() respectivamente.
		}
		catch (SQLException ex){
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
