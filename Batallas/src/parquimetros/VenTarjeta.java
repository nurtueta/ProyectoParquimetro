package parquimetros;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
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

import com.mysql.jdbc.Statement;

import quick.dbtable.DBTable;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class VenTarjeta extends JFrame{
	
	private DBTable tabla;
	
	private JComboBox boxUbicacion;
	private JComboBox boxNumero;
	private JComboBox boxParquimetro;
	private JComboBox boxTarjetas;
	
	private Vector<String> ubicaciones;
	private Vector<Integer> numeros;
	private Vector<Integer> parquimetros;
	private Vector<Integer> tarjetas;
	
	private JLabel lblCalle;
	private JLabel lblNumero;
	private JLabel lblParquimetro;
	private JLabel lblTarjeta;
	
	private JButton btnIngresar;
	
	private String txtConsulta;
	private JTextField textField;
	
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
		conectarBD();
		crearBox();
		crearLabel();
		crearBoton();
		crearOyente();
		
		try {
			Statement st = (Statement) tabla.getConnection().createStatement();
			ResultSet rs=st.executeQuery("SELECT calle FROM Parquimetros GROUP BY calle;");
			while(rs.next()){
				boxUbicacion.addItem(rs.getString(1));
			}
			rs.close();
			
			rs=st.executeQuery("SELECT id_tarjeta FROM Tarjetas ORDER BY id_tarjeta ;");
			while(rs.next()) {
				boxTarjetas.addItem(rs.getInt(1));
			}
			rs.close();
			
		} catch (SQLException ex) {
			salidaError(ex);;
		}
		
	}
	
	private void crearBox() {
		
		ubicaciones=new Vector<String>();
		boxUbicacion = new JComboBox(ubicaciones);
		boxUbicacion.setBounds(135, 30, 124, 24);
		getContentPane().add(boxUbicacion);
		
		numeros=new Vector<Integer>();
		boxNumero = new JComboBox(numeros);
		boxNumero.setBounds(135, 60, 124, 24);
		getContentPane().add(boxNumero);
		
		parquimetros=new Vector<Integer>();
		boxParquimetro = new JComboBox(parquimetros);
		boxParquimetro.setBounds(135, 90, 124, 25);
		getContentPane().add(boxParquimetro);
		
		tarjetas=new Vector<Integer>();
		boxTarjetas = new JComboBox(tarjetas);
		boxTarjetas.setBounds(135, 120, 124, 25);
		getContentPane().add(boxTarjetas);
	}
	
	private void crearLabel() {
		lblCalle = new JLabel("Calle");
		lblCalle.setBounds(30, 35, 66, 15);
		getContentPane().add(lblCalle);
		
		lblNumero = new JLabel("Numero");
		lblNumero.setBounds(30, 65, 66, 15);
		getContentPane().add(lblNumero);
		
		lblParquimetro = new JLabel("Parquimetro");
		lblParquimetro.setBounds(30, 95, 102, 15);
		getContentPane().add(lblParquimetro);
		
		lblTarjeta = new JLabel("Tarjeta");
		lblTarjeta.setBounds(30, 125, 66, 15);
		getContentPane().add(lblTarjeta);
	}
	
	private void crearBoton() {
		btnIngresar = new JButton("Ingresar");
		btnIngresar.setBounds(115, 157, 179, 25);
		getContentPane().add(btnIngresar);	
	}
	
	private void crearOyente() {
		boxUbicacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boxParquimetro.removeAllItems();
				boxNumero.removeAllItems();
				try {
					Statement st = (Statement) tabla.getConnection().createStatement();
					ResultSet rs=st.executeQuery("SELECT altura FROM Parquimetros WHERE calle='"+
							boxUbicacion.getSelectedItem()+"' GROUP BY altura ;");
					while(rs.next()){
						boxNumero.addItem(Integer.parseInt(rs.getString(1)));
					}
					boxNumero.setSelectedItem(null);
				} catch (SQLException ex) {
					salidaError(ex);
				}
			}
		});
		
		boxNumero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boxParquimetro.removeAllItems();
				try {
					Statement st = (Statement) tabla.getConnection().createStatement();
					ResultSet rs=st.executeQuery("SELECT numero FROM Parquimetros WHERE calle='"+
							boxUbicacion.getSelectedItem()+"' AND altura="+boxNumero.getSelectedItem()+";");
					while(rs.next()){
						boxParquimetro.addItem(Integer.parseInt(rs.getString(1)));
					}
					boxParquimetro.setSelectedItem(null);
				} catch (SQLException ex) {
					salidaError(ex);
				}
			}
		});
		
		btnIngresar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(boxParquimetro.getSelectedItem()==null || boxNumero.getSelectedItem()==null)
					JOptionPane.showMessageDialog(null,
							"Faltan ingresar datos",
							"Ingreso invalido",
							JOptionPane.ERROR_MESSAGE);
				else {	
					String calle=(String) boxUbicacion.getSelectedItem();
					int parquimetro= (Integer) boxParquimetro.getSelectedItem();
					int numero= (Integer) boxNumero.getSelectedItem();
					int id_tarjeta=(Integer) boxTarjetas.getSelectedItem();
					int id_parquimetro;
					Statement st = null;
					ResultSet rs = null;
					try {
						
						//obtengo id_parq 
						st = (Statement) tabla.getConnection().createStatement();
						rs = st.executeQuery("SELECT id_parq FROM Parquimetros WHERE numero="+parquimetro+" AND"+
								" calle='"+calle+"' AND altura="+numero+" ;");
						if(rs.first()) {
							id_parquimetro=Integer.parseInt(rs.getString(1));
							rs.close();
							//ejecuto el procedimiento
							txtConsulta="CALL conectar("+id_tarjeta+","+id_parquimetro+");";
							refrescarTabla();

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
			}
		});
	}

	private void crearTabla() {
		tabla = new DBTable();
		tabla.setBounds(30, 194, 400, 50);
		getContentPane().add(tabla);           
		tabla.setEditable(false);       
	}
	
	private void conectarBD(){
		try{
			String driver ="com.mysql.jdbc.Driver";
			String servidor = "localhost:3306";
			String baseDatos = "parquimetros";
			String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos;

			//establece una conexión con la  B.D. "parquimetros"  usando directamante una tabla DBTable    
			tabla.connectDatabase(driver, uriConexion, "parquimetro", "parq");
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
