package parquimetros;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.sql.Types;
import java.sql.Array;
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
import javax.swing.AbstractListModel;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; 


@SuppressWarnings("serial")
public class VenConsultas extends javax.swing.JFrame 
{
   private JPanel pnlConsulta;
   private JTextArea txtConsulta;
   private JButton botonBorrar;
   private JButton btnEjecutar;
   private DBTable tabla;    
   private JScrollPane scrConsulta;
   private JList list;
   private JList list_1;
   private DefaultListModel DLM;
   private DefaultListModel DLM_1;
   private JButton btnNewButton;
   
   private String usuario;
   private String clave;
  

   
   
   public VenConsultas() 
   {
      this(null,null);
   }
   
   public VenConsultas(String usuario, String clave) {
	   super();
	   this.usuario = usuario;
	   this.clave = clave;
	   initGUI();
   }
   
   private void initGUI() 
   {
      try {
         setPreferredSize(new Dimension(800, 600));
         this.setBounds(0, 0, 800, 600);
         setVisible(true);
         this.setTitle("Consultas (Utilizando DBTable)");
         this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
         this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent evt) {
               thisComponentHidden(evt);
            }
            public void componentShown(ComponentEvent evt) {
               thisComponentShown(evt);
            }
         });
         getContentPane().setLayout(null);
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
                      
           // setea la tabla para sólo lectura (no se puede editar su contenido)  
           tabla.setEditable(false);       
           
           
           
         }
         
         JPanel panel = new JPanel();
         panel.setBounds(392, 186, 392, 375);
         getContentPane().add(panel);
         panel.setLayout(null);
         
         JScrollPane scrollPane = new JScrollPane();
         scrollPane.setBounds(0, 45, 194, 330);
         panel.add(scrollPane);
         
         DLM = new DefaultListModel();
         list = new JList(DLM);               
         list.addMouseListener(new MouseAdapter() {
          	public void mouseClicked(MouseEvent arg0) {         		
          		DLM_1.clear();
          		if(DLM.getSize()> 0) {         			
          			String selected = (String) list.getSelectedValue();
          			selected = "'"+selected+"'";
          			
 					try {
 						Statement st = (Statement) tabla.getConnection().createStatement();
 						ResultSet rs = st.executeQuery("SELECT COLUMN_NAME FROM "
 	         					+ "INFORMATION_SCHEMA.COLUMNS "
 	         					+ "WHERE TABLE_SCHEMA='parquimetros' "
 	         					+ "AND TABLE_NAME="+selected );
 						boolean sig = rs.first();
 						while(sig) {
 							DLM_1.addElement(rs.getString(1));
 							sig = rs.next();
 						}
 					} catch (SQLException e) {
 						e.printStackTrace();
 					}         			        			
          		}  
          	}
         });
         list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         
         
         scrollPane.setViewportView(list);
         {
          	btnNewButton = new JButton("Refrescar");
          	btnNewButton.addActionListener(new ActionListener() {
          		public void actionPerformed(ActionEvent arg0) {
          			DLM.clear();        			        			
          			try {
 						Statement st = (Statement) tabla.getConnection().createStatement();
 						ResultSet rs = st.executeQuery("SELECT table_name FROM "
 								+ "information_schema.tables where "
 								+ "table_schema='parquimetros'");
 						boolean sig = rs.first();
 						while(sig) {
 							DLM.addElement(rs.getString(1));
 							sig = rs.next();												
 						}      			
          			} catch (SQLException e) {
 						e.printStackTrace();
 					}
         		}
          	});
          	btnNewButton.setBounds(139, 11, 111, 23);
          	panel.add(btnNewButton);
         }
         
         JScrollPane scrollPane_1 = new JScrollPane();
         scrollPane_1.setBounds(197, 45, 194, 330);
         panel.add(scrollPane_1);
         
         DLM_1 = new DefaultListModel();
         list_1 = new JList(DLM_1);
         scrollPane_1.setViewportView(list_1);
         
         
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   
   private void thisComponentShown(ComponentEvent evt) 
   {
      this.conectarBD();
   }
   
   private void thisComponentHidden(ComponentEvent evt) 
   {
      this.desconectarBD();
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
            String baseDatos = "batallas";
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
