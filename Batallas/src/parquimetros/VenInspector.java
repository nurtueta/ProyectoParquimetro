package parquimetros;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
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
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VenInspector window = new VenInspector();
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
	public VenInspector() {
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
}
