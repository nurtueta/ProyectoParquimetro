package parquimetros;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class VenInspector extends JFrame{

	private JButton btnIngresarPatente;
	private JButton btnIngresarParquimetro;
	private JButton btnPatente;
	private JButton btnParquimetro;
	private JButton btnEliminar;
	private JButton btnReiniciar;
	private JButton btnAtras;
	
	private JComboBox<String> boxUbicacion;
	private JComboBox<Integer> boxNumero;
	private JComboBox<Integer> boxParquimetro;
	
	private JTextField tfPatente;
	
	private JLabel lblPatente;
	private JLabel lblCalle;
	private JLabel lblParquimetro;
	private JLabel lblNumero;
	
	private JList<String> listaPatente;
	private DefaultListModel<String> LP;
	
	private DBTable tabla;
	
	private String legajo;
	private String txtConsulta;
	private String patente;
	private String calle;
	private Integer parquimetro;
	private Integer numero;
	
	private int Id;
	
	private Vector<String> ubicaciones;
	private Vector<Integer> numeros;
	private Vector<Integer> parquimetros;
	
	private JScrollPane scrollPane;
	
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
		
		setBounds(100, 100, 800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle("Parquimetro Landau-Urtueta-Vazquez");
		setIconImage(Toolkit.getDefaultToolkit().getImage(VenPrincipal.class.getResource("/imagenes/logoParquimetro.jpg")));

		setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				desconectarBD();
			}
		});
		getContentPane().setLayout(null);
		
		crearTabla();
		crearTextos();
		crearOtros();
		crearBotones();
		cargarOyentes();
		conectarBD();

	}
	
	private void crearOtros() {
		LP = new DefaultListModel<String>();
		listaPatente = new JList<String>(LP);
		
		scrollPane = new JScrollPane(listaPatente);
		scrollPane.setBounds(578, 11, 167, 325);
		getContentPane().add(scrollPane);
		
		ubicaciones=new Vector<String>();
		boxUbicacion = new JComboBox<String>(ubicaciones);
		boxUbicacion.setEnabled(false);
		boxUbicacion.setBounds(135, 86, 124, 24);
		getContentPane().add(boxUbicacion);
		
		numeros=new Vector<Integer>();
		boxNumero = new JComboBox<Integer>(numeros);
		boxNumero.setEnabled(false);
		boxNumero.setBounds(135, 118, 124, 24);
		getContentPane().add(boxNumero);
		
		parquimetros=new Vector<Integer>();
		boxParquimetro = new JComboBox<Integer>(parquimetros);
		boxParquimetro.setEnabled(false);
		boxParquimetro.setBounds(135, 150, 124, 25);
		getContentPane().add(boxParquimetro);
		
	}

	private void crearTextos() {
		tfPatente = new JTextField();
		tfPatente.setEnabled(false);
		tfPatente.setBounds(135, 55, 124, 19);
		getContentPane().add(tfPatente);
		tfPatente.setColumns(10);
		
		lblPatente = new JLabel("Patente:");
		lblPatente.setBounds(23, 54, 66, 15);
		getContentPane().add(lblPatente);
		
		lblCalle = new JLabel("Calle:");
		lblCalle.setBounds(23, 91, 103, 15);
		getContentPane().add(lblCalle);
		
		lblParquimetro = new JLabel("Parquimetro:");
		lblParquimetro.setBounds(23, 150, 103, 15);
		getContentPane().add(lblParquimetro);
		
		lblNumero = new JLabel("Numero:");
		lblNumero.setBounds(23, 123, 66, 15);
		getContentPane().add(lblNumero);
	}
	
	private void crearBotones() {
		btnIngresarPatente = new JButton("Ingresar Patente");
		btnIngresarPatente.setBounds(144, 12, 174, 25);
		getContentPane().add(btnIngresarPatente);
		
		btnAtras = new JButton("Menu Principal");
		btnAtras.setBounds(10, 12, 114, 25);
		getContentPane().add(btnAtras);
		
		btnIngresarParquimetro = new JButton("Ingresar Parquimetro");
		btnIngresarParquimetro.setEnabled(false);
		btnIngresarParquimetro.setBounds(349, 12, 200, 25);
		getContentPane().add(btnIngresarParquimetro);
		
		btnPatente = new JButton("Ingresar");
		btnPatente.setEnabled(false);
		btnPatente.setBounds(324, 49, 114, 25);
		getContentPane().add(btnPatente);
		
		btnParquimetro = new JButton("Ingresar");
		btnParquimetro.setEnabled(false);
		btnParquimetro.setBounds(324, 102, 114, 25);
		getContentPane().add(btnParquimetro);
		
		btnReiniciar = new JButton("Reiniciar");
		btnReiniciar.setBounds(324, 140, 114, 25);
		getContentPane().add(btnReiniciar);
		
		btnEliminar = new JButton("Eliminar Patente");
		btnEliminar.setBounds(621, 407, 124, 23);
		getContentPane().add(btnEliminar);
	}
	
	private void crearTabla() {
		tabla = new DBTable();
		tabla.setBounds(35, 250, 500, 300);
		getContentPane().add(tabla);           
		tabla.setEditable(false);       
	}

	private void cargarOyentes() {
		
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
		
		btnIngresarPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//conecto a la BD y habilito las operaciones siguientes
				btnPatente.setEnabled(true);
				tfPatente.setEnabled(true);
				btnParquimetro.setEnabled(false);
				btnIngresarParquimetro.setEnabled(true);
				btnIngresarPatente.setEnabled(false);
				boxParquimetro.setSelectedItem(null);
				boxNumero.setSelectedItem(null);
				boxUbicacion.setSelectedItem(null);		
			}
		});

		btnAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				desconectarBD();
				setVisible(false);
				String [] args = null;
				VenPrincipal.main(args);;	
			}
		});
		
		btnIngresarParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnPatente.setEnabled(false);
				tfPatente.setEnabled(false);
				btnIngresarParquimetro.setEnabled(false);
				btnIngresarPatente.setEnabled(true);
				btnParquimetro.setEnabled(true);
				boxUbicacion.removeAllItems();
				boxUbicacion.setEnabled(true);
				boxNumero.setEnabled(true);
				boxParquimetro.setEnabled(true);
				//cargo datos de ubicaciones
				try {
					Statement st = (Statement) tabla.getConnection().createStatement();
					ResultSet rs=st.executeQuery("SELECT calle FROM Parquimetros GROUP BY calle;");
					while(rs.next()){
						boxUbicacion.addItem(rs.getString(1));
					}
				} catch (SQLException ex) {
					salidaError(ex);;
				}
			}
		});
		
		btnPatente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				patente=tfPatente.getText();
				//compruebo que la patente exista
				try {
					Statement st = (Statement) tabla.getConnection().createStatement();
					ResultSet rs=st.executeQuery("SELECT patente FROM Automoviles WHERE patente='"+patente+"';");
					//si existe la ingreso a la lista, sino muestro mensaje
					if(rs.first()) {
						if(!LP.contains(patente))
							LP.addElement(patente);
						else
							JOptionPane.showMessageDialog(null,
									"La patente "+patente+" ya esta ingresada",
									"Ingreso invalido",
									JOptionPane.ERROR_MESSAGE);
					}else
						JOptionPane.showMessageDialog(null,
								"La patente "+patente+" no existe",
								"Ingreso invalido",
								JOptionPane.ERROR_MESSAGE);
						
				} catch (SQLException ex) {
					salidaError(ex);
				}
				tfPatente.setText("");
			}
		});
		
		btnParquimetro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				//obtener ubicacion y numero de parquimetro
				if(boxUbicacion.getSelectedItem()==null || boxParquimetro.getSelectedItem()==null ||
							boxNumero.getSelectedItem()==null)
					JOptionPane.showMessageDialog(null,
							"Faltan ingresar datos",
							"Ingreso invalido",
							JOptionPane.ERROR_MESSAGE);
				else {	
					Statement st = null;
					ResultSet rs = null;
					try { 
						String dia;
						String turno;
						calle=(String) boxUbicacion.getSelectedItem();
						parquimetro= (Integer) boxParquimetro.getSelectedItem();
						numero= (Integer) boxNumero.getSelectedItem();
						
						st = (Statement) tabla.getConnection().createStatement();
						
						//obtengo dia de la semana en que se conecta
						dia=obtenerDiaSemana(st,rs);
						
						//obtengo turno de ingreso
						turno=obtenerTurno(st,rs);
						
						
						//compruebo que este en el horario de trabajo
						if(turno.equals("T") || turno.equals("M")) {
						
							//comprobar si se conecta el legajo en su determinado turno
							rs = st.executeQuery("SELECT id_asociado_con FROM Asociado_con WHERE legajo="+legajo+" AND"+
							" calle='"+calle+"' AND altura="+numero+" AND dia='"+dia+"' AND turno='"+turno+"';");
							if(rs.first()) {
								//se conecto en su determinado turno
								Id=Integer.parseInt(rs.getString(1));
								rs.close();
								
								//obtengo id_parq 
								rs = st.executeQuery("SELECT id_parq FROM Parquimetros WHERE numero="+parquimetro+" AND"+
										" calle='"+calle+"' AND altura="+numero+" ;");
								//siempre van a ser correctos, porque no le doy eleccion incorrecta al usuario
								if(rs.first()) {
									//datos de parquimetros correctos
									parquimetro=Integer.parseInt(rs.getString(1));
									rs.close();
									
									//ingresar acceso
									st.executeUpdate("INSERT INTO Accede VALUES("+legajo+","+parquimetro+",CURDATE(),CURTIME());");
									
									generarYMostrarMultas(st,rs);
									
									//seteo todo para volver a empezar
									btnIngresarPatente.setEnabled(true);
									btnParquimetro.setEnabled(false);
							
								}
							}else {
								JOptionPane.showMessageDialog(null,
										"No esta habilitado en este turno para conectarse al parquimetro",
										"Ingreso invalido",
										JOptionPane.ERROR_MESSAGE);
							}
						}else {
							JOptionPane.showMessageDialog(null,
									"Esta fuera del horario de trabajo",
									"Ingreso invalido",
									JOptionPane.ERROR_MESSAGE);
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
		
		btnReiniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//vuelvo a empezar
				btnIngresarPatente.setEnabled(true);
				btnIngresarParquimetro.setEnabled(false);
				btnParquimetro.setEnabled(false);
				btnPatente.setEnabled(false);
				tfPatente.setEnabled(false);
				boxParquimetro.removeAllItems();
				boxNumero.removeAllItems();
				boxUbicacion.removeAllItems();
				boxUbicacion.setEnabled(false);
				boxNumero.setEnabled(false);
				boxParquimetro.setEnabled(false);
				LP.removeAllElements();
				tabla.clearSelection();
			}
		});
		
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//elimino patente seleccionada
				if(!listaPatente.isSelectionEmpty())
					LP.remove(listaPatente.getSelectedIndex());
			}
		});
	}
	
	private String obtenerDiaSemana(Statement st,ResultSet rs)throws SQLException{
		//obtengo dia de la semana en que se conecta
		String dia=null;
		
		rs=st.executeQuery("SELECT DAYOFWEEK(CURDATE());");
		rs.first();
		
		String fecha=rs.getString(1);
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
		return dia;
	}

	private String obtenerTurno(Statement st,ResultSet rs)throws SQLException{
		String turno;
		int hora;
		
		rs=st.executeQuery("SELECT CURTIME();");
		rs.first();
		
		hora=Integer.parseInt(rs.getString(1).substring(0,2));
		rs.close();
		
		if((hora<=8) && hora<14)
			turno="M";
		else
			if(hora>=14 && hora<19)
				turno="T";
			else 
//				turno="x";
				turno="T";

		return turno;
	}
	
	private void generarYMostrarMultas(Statement st,ResultSet rs) throws SQLException{
		
		//crear multas
		for(int i=0;i<LP.size();i++) {
			patente=LP.getElementAt(i);
			//si la patente no esta en estacionados entonces hago una multa
			rs=st.executeQuery("SELECT patente FROM estacionados WHERE patente='"+patente+"' AND calle='"+calle+"' AND "
				+"altura="+numero+";");
			if(!rs.first()) {
				//hacer multa
				st.executeUpdate("INSERT INTO Multa(fecha,hora,patente,id_asociado_con) VALUES (CURDATE(),"
						+ "CURTIME(),'"+patente+"',"+Id+");");
			}
			rs.close();
		}
		
		//mostrar multas
		txtConsulta="SELECT numero,fecha,hora,calle,altura,patente,legajo FROM Multa NATURAL JOIN Asociado_con "+
				"WHERE calle='"+calle+"' AND altura="+numero+" AND legajo="+legajo+
				" AND fecha=CURDATE();";
		refrescarTabla();
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
