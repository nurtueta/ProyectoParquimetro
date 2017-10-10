package parquimetros;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.JScrollPane;

public class VenInspector extends JFrame{

	private JFrame frame;

	private JButton btnIngresarPatente;
	private JButton btnIngresarParquimetro;
	private JButton btnPatente;
	private JButton btnParquimetro;
	private JButton btnEliminar;
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
	private String txtConsulta;
	private String patente;
	private JTextField tfNumero;
	private JScrollPane scrollPane;
	private JLabel lblNumero;
	
	private Fechas fecha;
	
	public VenInspector(String u) 
	{
		super();
		getContentPane().setBackground(new Color(204, 255, 153));
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

		setTitle("Parquimetro Landau-Urtueta-Vazquez");
		setIconImage(Toolkit.getDefaultToolkit().getImage(VenPrincipal.class.getResource("/imagenes/logoParquimetro.jpg")));

		setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//desconectarBD();
			}
		});
		getContentPane().setLayout(null);
		getContentPane().setLayout(null);
		
		crearTabla();
		
		btnIngresarPatente = new JButton("Ingresar Patente");
		btnIngresarPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPatente.setEnabled(true);
				tfPatente.setEnabled(true);
				btnIngresarParquimetro.setEnabled(true);
				btnIngresarPatente.setEnabled(false);
				conectarBD();		
			}
		});
		
		
		JButton btnAtras = new JButton("Menu Principal");
		btnAtras.setBounds(10, 12, 114, 25);
		getContentPane().add(btnAtras);
		
		btnAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(tabla !=null)
				desconectarBD();
				setVisible(false);
				String [] args = null;
				VenPrincipal.main(args);;
				
			}
		});
		
		
		btnIngresarPatente.setBounds(144, 12, 174, 25);
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
		btnIngresarParquimetro.setBounds(349, 12, 200, 25);
		getContentPane().add(btnIngresarParquimetro);
		
		btnPatente = new JButton("Ingresar");
		btnPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patente=tfPatente.getText();
				//compruebo que la patente exista
				try {
					Statement st = (Statement) tabla.getConnection().createStatement();
					ResultSet rs=st.executeQuery("SELECT patente FROM Automoviles WHERE patente='"+patente+"';");
					if(rs.first())
						LP.addElement(patente);
					else
						JOptionPane.showMessageDialog(null,
								"La patente "+patente+" no existe",
								"Ingreso invalido",
								JOptionPane.ERROR_MESSAGE);
						
				} catch (SQLException ex) {
					salidaError(ex);;
				}

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
//							JOptionPane.showMessageDialog(null,
//									"Esta fuera del horario de trabajo",
//									"Ingreso invalido",
//									JOptionPane.ERROR_MESSAGE);
					rs.close();
					
					//comprobar si se conecta el legajo en su determinado turno
					int Id=6;
					rs = st.executeQuery("SELECT id_asociado_con FROM Asociado_con WHERE legajo="+legajo+" AND"+
					" calle='"+calle+"' AND altura="+numero+" AND dia='"+dia+"' AND turno='"+turno+"';");
					if(rs.first()) {
						Id=Integer.parseInt(rs.getString(1));
						//ingresar acceso
						st.executeUpdate("INSERT INTO Accede VALUES("+legajo+","+parquimetro+",CURDATE(),CURTIME());");
						
						//crear multas
						for(int i=0;i<LP.size();i++) {
							patente=LP.getElementAt(i);
							rs=st.executeQuery("SELECT patente FROM estacionados WHERE patente='"+patente+"' AND calle='"+calle+"' AND "
								+"altura="+numero+";");
							if(!rs.first()) {
								//hacer multa
								st.executeUpdate("INSERT INTO Multa(fecha,hora,patente,id_asociado_con) VALUES (CURDATE(),"
										+ "CURTIME(),'"+patente+"',"+Id+");");
							}
							rs.close();
						}
						LP.removeAllElements();
						
						//mostrar multas
						txtConsulta="SELECT numero,fecha,hora,calle,altura,patente,legajo FROM Multa NATURAL JOIN Asociado_con "+
								"WHERE calle='"+calle+"' AND altura="+numero+" AND legajo="+legajo+
								" AND fecha=CURDATE();";
						refrescarTabla();
					
					}else
						JOptionPane.showMessageDialog(null,
								"No puede ingresar al parquimetro en este turno",
								"Ingreso invalido",
								JOptionPane.ERROR_MESSAGE);
				
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
		
		scrollPane = new JScrollPane(listaPatente);
		scrollPane.setBounds(578, 11, 167, 325);
		getContentPane().add(scrollPane);
		
		lblNumero = new JLabel("Numero:");
		lblNumero.setBounds(23, 123, 66, 15);
		getContentPane().add(lblNumero);
		
		tfNumero = new JTextField();
		tfNumero.setEnabled(false);
		tfNumero.setBounds(135, 117, 124, 19);
		getContentPane().add(tfNumero);
		tfNumero.setColumns(10);
		
		btnEliminar = new JButton("Eliminar Patente");
		btnEliminar.setBounds(621, 407, 124, 23);
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//elimino patente seleccionada
				if(!listaPatente.isSelectionEmpty())
					LP.remove(listaPatente.getSelectedIndex());
			}
		});
		getContentPane().add(btnEliminar);

	}

	private void crearTabla() {
		tabla = new DBTable();
		tabla.setBounds(35, 250, 500, 300);
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
