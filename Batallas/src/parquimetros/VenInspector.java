package parquimetros;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import quick.dbtable.DBTable;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Types;
import java.awt.event.ActionEvent;
import javax.swing.JTable;

public class VenInspector extends JFrame{

	private JFrame frame;

	private JButton btnIngresarPatente;
	private JButton btnIngresarParquimetro;
	private JButton btnPatente;
	private JButton btnParquimetro;
	private JTextField tfPatente;
	private JTextField tfUbicacion;
	private JTextField tfParquimetro;
	private JLabel lblPatente;
	private JLabel lblUbicacion;
	private JLabel lblParquimetro;
	private JTable tableMuestra;
	
	private DBTable tabla;
	private String usuario;
	private String clave;
	private String txtConsulta;
	
	
	public static void main(String[] args) {

		VenInspector inst = new VenInspector();
		inst.setLocationRelativeTo(null);
		inst.setVisible(true);
	}

	public VenInspector(String usuario) 
	{
		super();
		this.usuario = usuario;
		initGUI();
	}
	
	public VenInspector() {
		
	}
	/**
	 * Create the application.
	 */
	public void initGUI() {
		getContentPane().setLayout(null);
		
		btnIngresarPatente = new JButton("Ingresar Patente");
		btnIngresarPatente.setBounds(107, 12, 174, 25);
		getContentPane().add(btnIngresarPatente);
		
		btnIngresarParquimetro = new JButton("Ingresar Parquimetro");
		btnIngresarParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnIngresarParquimetro.setBounds(324, 12, 200, 25);
		getContentPane().add(btnIngresarParquimetro);
		
		btnPatente = new JButton("Ingresar");
		btnPatente.setEnabled(false);
		btnPatente.setBounds(324, 49, 114, 25);
		getContentPane().add(btnPatente);
		
		btnParquimetro = new JButton("Ingresar");
		btnParquimetro.setEnabled(false);
		btnParquimetro.setBounds(324, 86, 114, 25);
		getContentPane().add(btnParquimetro);
		
		tfPatente = new JTextField();
		tfPatente.setEnabled(false);
		tfPatente.setBounds(135, 55, 124, 19);
		getContentPane().add(tfPatente);
		tfPatente.setColumns(10);
		
		tfUbicacion = new JTextField();
		tfUbicacion.setEnabled(false);
		tfUbicacion.setBounds(135, 86, 124, 19);
		getContentPane().add(tfUbicacion);
		tfUbicacion.setColumns(10);
		
		tfParquimetro = new JTextField();
		tfParquimetro.setEnabled(false);
		tfParquimetro.setBounds(135, 111, 124, 19);
		getContentPane().add(tfParquimetro);
		tfParquimetro.setColumns(10);
		
		lblPatente = new JLabel("Patente:");
		lblPatente.setBounds(23, 54, 66, 15);
		getContentPane().add(lblPatente);
		
		lblUbicacion = new JLabel("Ubicacion:");
		lblUbicacion.setBounds(23, 88, 103, 15);
		getContentPane().add(lblUbicacion);
		
		lblParquimetro = new JLabel("Parquimetro:");
		lblParquimetro.setBounds(23, 113, 103, 15);
		getContentPane().add(lblParquimetro);
		
		tableMuestra = new JTable();
		tableMuestra.setBounds(33, 146, 571, 161);
		getContentPane().add(tableMuestra);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
}
