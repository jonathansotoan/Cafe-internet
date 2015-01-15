//conexion y consultas de la base de datos en mysql
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//GUI
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;

//creacion de vector para ingresar como fila al modelo de la tabla
import java.util.Vector;

//organizacion de los elementos, color y eventos
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//obtencion de ip y nombr del equipo
import java.net.InetAddress;

/**
 * Clase con m&eacute;todo principal que crea una interfaz de usuario gr&aacute;fica para ejecutar comandos en equipos remotos que est&eacute;n ejecutando la aplicaci&oacute;n Estacion.
 * 
 * @author Jonathan Alexander Soto Montoya
 * @version 1.00 29/04/2013
 * @see AdministradorHiloEnviar
 * @see Estacion
 */
public class Administrador extends JFrame implements ActionListener {

	boolean controlEliminar;//usado para un pequeno script para mejorar el rendimiento del proceso de eliminacion
	DefaultTableModel dtm;//el modelo por defecto de la tabla, cualquier modificacion en este se vera reflejado en la tabla
	int fae, puerto = 5000;//fae: usada para la edicion de filas (la necesitaba poner global para poderla usar dentro de los escuchadores de eventos). puerto: se usa para saber a que puerto se hara la conexion
	JButton bEliminar, bAgregar, bSelecTodos, bSelecConectados, bSelecDesconectados, bEnviar, bDetallesE, bAgregarE, bAceptar, bQuitarE, bApagar, bSuspender, bReiniciar, bComprobar, bBloquear, bDesbloquear, bEjecutar;//botones (una E al final de la variable significa estudiante)
	JDialog ventana3;//es la ventana de agregar estudiante, esta global para poderla utilizar en el escuchador de eventos del boton bAceptar de la misma ventana
	JFrame ventana;//ventana principal del programa
	JTextField tfIp, tfNombre, tfSala, tfNombreE, tfIdE;
	ResultSet rs;//variable para guardar el resultado de las consultas mysql (de las sentencias o statement)
	Statement st;//ejecuta una sentencia de mysql

	/**
	 * M&eacute;todo constructor, inicializa la aplicaci&oacute;n llamando al m&eacute;todo parteGrafica ().
	 */
	public Administrador () {
		parteGrafica ();
	}
	
	/**
	 * M&eacute;todo principal, crea un objeto de tipo Administrador.
	 *
	 * @param args como <code><b>cadena</b></code> que es inutilizado en esta aplicaci&oacute;n.
	 */
	public static void main (String args[]) {
		new Administrador ();
	}
	
	/**
	 * Crea los objetos de javax.swing necesarios para la GUI de la aplicaci&oacute;n.
	 *
	 * @throws Exception
	 */
	public void parteGrafica () {
		ventana = new JFrame ("Aporemote");
		JMenuBar barra = new JMenuBar ();
		JPanel p1 = new JPanel (new GridLayout (5, 3, 10, 10));
		JScrollPane jsp;
		JTable tabla;

		dtm = new DefaultTableModel (
			new String[]{"", "IP del equipo", "Nombre del equipo", "Nombre del estudiante", "Sala en la que se encuentra", "Estado"}, 0) {
			
				Class types[] = new Class[] {
					java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
				};
				
				public Class getColumnClass (int ColumnIndex) {
					return types[ColumnIndex];
				}
				
				@Override
				public boolean isCellEditable (int fila, int columna) {
					if (columna == 0)
						return true;
					else
						return false;
				}
			};
		
		try {
			Connection con = DriverManager.getConnection ("jdbc:mysql://localhost:3306/cafeinternet","root","139595");
			st = con.createStatement ();
			rs = st.executeQuery ("select * from equipos");
			
			while (rs.next ()) {
				Vector temp = new Vector (4, 1);
				temp.add (false);
				temp.add (rs.getObject ("ip"));
				temp.add (rs.getObject ("nombre"));
				temp.add ("");
				temp.add (rs.getObject ("sala"));
				temp.add ("Desconectado");
				dtm.addRow (temp);
			}
		} catch (Exception e) {
			System.out.println ("Error: " + e.toString ());
		}
		
		tabla = new JTable (dtm) {
			public Component prepareRenderer (TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer (renderer, row, column);
				
				if (column == 5) {
					if (dtm.getValueAt (row, column).equals ("Desconectado"))
						c.setForeground (Color.red);
					else
						c.setForeground (Color.green);
				} else
					c.setForeground (Color.black);
				
				return c;
			}
		};
		tabla.getColumnModel ().getColumn (0).setPreferredWidth (20);
		tabla.getColumnModel ().getColumn (1).setPreferredWidth (100);
		tabla.getColumnModel ().getColumn (2).setPreferredWidth (150);
		tabla.getColumnModel ().getColumn (3).setPreferredWidth (150);
		tabla.getColumnModel ().getColumn (4).setPreferredWidth (170);
		tabla.getColumnModel ().getColumn (5).setPreferredWidth (100);
		jsp = new JScrollPane (tabla);
		JMenu config = new JMenu ("Configuraci\u00f3n");
		JMenuItem puerto = new JMenuItem ("Establecer puerto");
		JMenuItem ip = new JMenuItem ("Mostrar mi IP y nombre de equipo");
		bEliminar = new JButton ("Eliminar equipo");
		bAgregar = new JButton ("Agregar equipo");
		bSelecTodos = new JButton ("Seleccionar todos");
		bSelecConectados = new JButton ("Seleccionar conectados");
		bSelecDesconectados = new JButton ("Seleccionar desconectados");
		bDetallesE = new JButton ("Detalles del estudiante");
		bAgregarE = new JButton ("Agregar estudiante");
		bQuitarE = new JButton ("Quitar estudiante");
		bApagar = new JButton ("Apagar equipos");
		bSuspender = new JButton ("Suspender equipos");
		bReiniciar = new JButton ("Reiniciar equipos");
		bComprobar = new JButton ("Comprobar conexi\u00f3n");
		bBloquear = new JButton ("Bloquear equipos");
		bDesbloquear = new JButton ("Desbloquear equipos");
		bEjecutar = new JButton ("Ejecutar en equipos");
		
		puerto.addActionListener (this);
		ip.addActionListener (this);
		bEliminar.addActionListener (this);
		bAgregar.addActionListener (this);
		bSelecTodos.addActionListener (this);
		bSelecConectados.addActionListener (this);
		bSelecDesconectados.addActionListener (this);
		bDetallesE.addActionListener (this);
		bAgregarE.addActionListener (this);
		bQuitarE.addActionListener (this);
		bApagar.addActionListener (this);
		bSuspender.addActionListener (this);
		bReiniciar.addActionListener (this);
		bComprobar.addActionListener (this);
		bBloquear.addActionListener (this);
		bDesbloquear.addActionListener (this);
		bEjecutar.addActionListener (this);

		config.add (puerto);
		config.add (ip);
		barra.add (config);
		p1.add (bSelecTodos);
		p1.add (bSelecConectados);
		p1.add (bSelecDesconectados);
		p1.add (bAgregarE);
		p1.add (bQuitarE);
		p1.add (bDetallesE);
		p1.add (bBloquear);
		p1.add (bDesbloquear);
		p1.add (bSuspender);
		p1.add (bApagar);
		p1.add (bReiniciar);
		p1.add (bEjecutar);
		p1.add (bComprobar);
		p1.add (bAgregar);
		p1.add (bEliminar);
		ventana.setLayout (new BorderLayout (10, 10));
		ventana.add (barra, BorderLayout.NORTH);
		ventana.add (jsp, BorderLayout.CENTER);
		ventana.add (p1, BorderLayout.SOUTH);
		ventana.setSize (720, 500);
		ventana.setVisible (true);
		ventana.setDefaultCloseOperation (javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * Crea una ventana (JDialog) para que sea ingresado un nuevo usuario a la aplicaci&oacute;n y a la base de datos con la que se est&aacute; trabajando.
	 *
	 * @throws Exception
	 */
	public void ventanaAgregarNuevoEquipo () {
		JLabel lIp, lNombre, lSala;

		lIp = new JLabel ("Ingrese la IP", JLabel.CENTER);
		lNombre = new JLabel ("Ingrese el nombre", JLabel.CENTER);
		lSala = new JLabel ("Ingrese la sala", JLabel.CENTER);
		tfIp = new JTextField ();
		tfNombre = new JTextField ();
		tfSala = new JTextField ();
		bEnviar = new JButton ("Ingresar equipo");
		bEnviar.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				Vector temp = new Vector (4, 1);
				temp.add (false);
				temp.add (tfIp.getText ());
				temp.add (tfNombre.getText ());
				temp.add ("");
				temp.add (tfSala.getText ());
				temp.add ("Desconectado");
				try {
					st.executeUpdate ("INSERT INTO equipos VALUES('" + temp.elementAt (1)
						+ "', '" + temp.elementAt (2)
						+ "', '" + temp.elementAt (4) + "')");
				} catch (Exception e) {
					System.out.println ("Error: " + e.toString ());
				}
				dtm.addRow (temp);
			}
		});

		tfIp.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					bEnviar.doClick ();
				}//se puede quitar
			}
		}
		);
		tfNombre.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					bEnviar.doClick ();
				}
			}
		}
		);
		tfSala.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					bEnviar.doClick ();
				}
			}
		}
		);
		
		JDialog ventana2 = new JDialog (ventana, "Ingresar equipo nuevo");
		ventana2.setVisible (true);
		ventana2.setLayout (new GridLayout (4, 2, 10, 10));
		ventana2.setSize (300, 150);
		ventana2.add (lIp);
		ventana2.add (tfIp);
		ventana2.add (lNombre);
		ventana2.add (tfNombre);
		ventana2.add (lSala);
		ventana2.add (tfSala);
		ventana2.add (bEnviar);
	}
	
	/**
	 * Crea una ventana (JDialog) para que sea vinculado un estudiante a un equipo activo. Si el estudiante ya ha estado en alg&uacute;n equipo alguna vez, estar&aacute; en la base de datos y se podr&aacute; agregar &uacute;nicamente con poner el n&uacute;mero de identificaci&oacute;n o el nombre completo; Si es la primera vez que el estudiante va a usar un computador, se ingresan sus datos a la base de datos para que la pr&oacute;xima vez que se vaya a vincular a un equipo sea mucho m&aacute;s f&aacute;cil.
	 *
	 * @param filaAeditar como <code><b>entero</b></code> que define la fila o equipo a la que se va a vincular el estudiante. 
	 *
	 * @throws Exception
	 */
	public void ventanaAgregarEstudiante (int filaAeditar) {
		fae = filaAeditar;
		ventana3 = new JDialog (ventana, "Agregar estudiante");
		ventana3.setVisible (true);
		ventana3.setLayout (new GridLayout (3, 2, 10, 10));
		ventana3.setSize (300, 150);
		
		tfNombreE = new JTextField ();
		tfIdE = new JTextField ();
		
		JButton bComprobar = new JButton ("Comprobar");
		bAceptar = new JButton ("Aceptar");
		bComprobar.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				try {
					if (!tfIdE.getText ().equals ("")) {
						rs = st.executeQuery ("SELECT * FROM estudiantes WHERE id='" + tfIdE.getText () + "'");
						rs.next ();
						if (rs.isFirst ()) {
							tfNombreE.setText (rs.getObject ("nombre").toString ());
						} else
							JOptionPane.showMessageDialog (null, "No existe un estudiante con ese n\u00famero de identificaci\u00f3n");
					} else if (!tfNombreE.getText ().equals ("")) {
						rs = st.executeQuery ("SELECT * FROM estudiantes WHERE nombre='" + tfNombreE.getText () + "'");
						rs.next ();
						if (rs.isFirst ()) {
							tfIdE.setText (rs.getObject ("id").toString ());
						} else
							JOptionPane.showMessageDialog (null, "No existe un estudiante con ese nombre");
					} else
						JOptionPane.showMessageDialog (null, "Ingrese uno de los datos para verificar si ya existe el estudiante");
					
					bAceptar.doClick ();
				} catch (Exception e) {
					System.out.println ("Error: " + e.toString ());
				}
			}
		});		
		bAceptar.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				try {
					st.executeUpdate ("INSERT INTO estudiantes VALUES ('" + tfNombreE.getText () + "'," + tfIdE.getText () + ")");
				} catch (Exception e) {
					System.out.println ("Error: " + e.toString ());
				}
				ventana3.setVisible (false);
				dtm.setValueAt (tfNombreE.getText (), fae, 3);
			}
		});
		
		ventana3.add (new JLabel ("Nombre"));
		ventana3.add (tfNombreE);
		ventana3.add (new JLabel ("Identificaci\u00f3n"));
		ventana3.add (tfIdE);
		ventana3.add (bComprobar);
		ventana3.add (bAceptar);
	}
	
	/**
	 * Obtiene la primera fila que est&eacute; marcada empezando de arriba hacia abajo, este m&eacute;todo es utilizado en esta clase en los escuchadores de los botones: bAgregarE (para vincular un estudiante a un equipo), bDetallesE (para ver los detalles de un estudiante que est&eacute; vinculado a un equipo) y bQuitarE (para desvincular a un estudiante de un equipo) para que se haga la operaci&oacute;n correspondiente &uacute;nicamente al primer equipo seleccionado.
	 */
	public int obtenerPrimeraFilaMarcada () {
		int j;
		boolean seleccionado = true;

		for (j = 0; j < dtm.getRowCount () && seleccionado; j++) {
			if ((boolean)dtm.getValueAt (j, 0))
				seleccionado = false;
		}

		if (seleccionado)
			return -1;
		else
			return j - 1;
	}

	/**
	 * Escuchador de eventos de los elementos de la ventana principal de la aplicaci&oacute;n.
	 *
	 * @param ae como <code><b>ActionEvent</b></code> que identifica cu&aacute;l elemento dispar&oacute; el evento.
	 *
	 * @throws Exception
	 */
	public void actionPerformed (ActionEvent ae) {
		try {
			if (ae.getSource () == bEliminar) {//si se presiona el boton de eliminar
					controlEliminar = true;
					for (short j = 0; j < dtm.getRowCount () && controlEliminar; j++) {//cada vez que encuentra uno seleccionado lo elimina y hace un tipo de recursion con doClick () y vuelve a buscar seleccionados
						if ((boolean)dtm.getValueAt (j, 0)) {
							st.executeUpdate ("DELETE from equipos where ip='" + dtm.getValueAt (j, 1) + "'");
							dtm.removeRow (j);
							bEliminar.doClick ();
							controlEliminar = false;
						}
					}
			} else if (ae.getSource () == bAgregar) {
				ventanaAgregarNuevoEquipo ();
			} else if (ae.getSource () == bSelecTodos) {
				boolean seleccionadas = true;//para identificar si se quiere seleccionar o deseleccionar
				for (short j = 0; j < dtm.getRowCount () && seleccionadas; j++) {
					if (!(boolean)dtm.getValueAt (j, 0))
						seleccionadas = false;
				}

				for (short j = 0; j < dtm.getRowCount (); j++) {
					dtm.setValueAt (!seleccionadas, j, 0);
				}
				
				if (seleccionadas)
					bSelecTodos.setText ("Seleccionar todos");
				else
					bSelecTodos.setText ("Deseleccionar todos");
			} else if (ae.getSource () == bSelecConectados) {
				boolean seleccionadas = true;
				for (short j = 0; j < dtm.getRowCount () && seleccionadas; j++) {
					if (!(boolean)dtm.getValueAt (j, 0) && dtm.getValueAt (j, 5).equals ("Conectado"))
						seleccionadas = false;
				}

				for (short j = 0; j < dtm.getRowCount (); j++) {
					if (dtm.getValueAt (j, 5).equals ("Conectado"))
						dtm.setValueAt (!seleccionadas, j, 0);
				}
				
				if (seleccionadas)
					bSelecConectados.setText ("Seleccionar conectados");
				else
					bSelecConectados.setText ("Deseleccionar conectados");
			} else if (ae.getSource () == bSelecDesconectados) {
				boolean seleccionadas = true;
				for (short j = 0; j < dtm.getRowCount () && seleccionadas; j++) {
					if (!(boolean)dtm.getValueAt (j, 0) && dtm.getValueAt (j, 5).equals ("Desconectado"))
						seleccionadas = false;
				}

				for (short j = 0; j < dtm.getRowCount (); j++) {
					if (dtm.getValueAt (j, 5).equals ("Desconectado"))
						dtm.setValueAt (!seleccionadas, j, 0);
				}
				
				if (seleccionadas)
					bSelecDesconectados.setText ("Seleccionar desconectados");
				else
					bSelecDesconectados.setText ("Deseleccionar desconectados");
			} else if (ae.getSource () == bAgregarE) {
				int temp = obtenerPrimeraFilaMarcada ();
				if (temp != -1)
					ventanaAgregarEstudiante (temp);
				else
					JOptionPane.showMessageDialog (null, "Debe seleccionar el equipo al que quiere agregarle el estudiante");
			} else if (ae.getSource () == bDetallesE) {
				int pf = obtenerPrimeraFilaMarcada ();
				if (pf != -1) {
					if (!((String)dtm.getValueAt (pf, 3)).equals ("")) {
						JDialog info = new JDialog (ventana, "Detalles del estudiante");
						info.setLayout (new GridLayout (2, 2));
						info.setVisible (true);
						info.setSize (200, 200);
						
						rs = st.executeQuery ("select * from estudiantes where nombre='" + dtm.getValueAt (obtenerPrimeraFilaMarcada (), 3) + "'");
						rs.next ();
						
						info.add (new JLabel ("Nombre: "));
						info.add (new JLabel (rs.getObject ("nombre").toString ()));
						info.add (new JLabel ("Identificaci\u00f3n: "));
						info.add (new JLabel (rs.getObject ("id").toString ()));
					} else
						JOptionPane.showMessageDialog (null, "El equipo seleccionado no tiene un estudiante activo");
				} else
					JOptionPane.showMessageDialog (null, "Debe seleccionar un equipo");
			} else if (ae.getSource () == bQuitarE) {
				int pf = obtenerPrimeraFilaMarcada ();
				
				if (pf != -1)
					dtm.setValueAt ("", pf, 3);
				else
					JOptionPane.showMessageDialog (null, "Debe seleccionar un equipo");
			} else if (ae.getSource () == bApagar) {
				enviarMensajeAseleccionados ("shutdown -s -t 0");
			} else if (ae.getSource () == bReiniciar) {
				enviarMensajeAseleccionados ("shutdown -r -t 0");
			} else if (ae.getSource () == bSuspender) {
				enviarMensajeAseleccionados ("rundll32.exe PowrProf.dll,SetSuspendState");
			} else if (ae.getSource () == bComprobar) {
				for (short j = 0; j < dtm.getRowCount (); j++)
					enviarMensajeAseleccionados ("are you on?");
			} else if (ae.getSource () == bBloquear) {
				enviarMensajeAseleccionados ("bloquear");
			} else if (ae.getSource () == bDesbloquear) {
				enviarMensajeAseleccionados ("desbloquear");
			} else if (ae.getSource () == bEjecutar) {
				enviarMensajeAseleccionados (JOptionPane.showInputDialog (null, "Ingrese el comando a ejecutar en los equipos seleccionados", "Ejecutar remotamente", JOptionPane.PLAIN_MESSAGE));
			} else if (ae.getActionCommand ().equals ("Establecer puerto")) {
				puerto = Integer.parseInt (JOptionPane.showInputDialog (null, "El puerto actual es " + puerto + ", introduzca el nuevo valor", "Establecer puerto", JOptionPane.PLAIN_MESSAGE));
				JOptionPane.showMessageDialog (null, "El puerto ha sido cambiado a " + puerto);
			} else if (ae.getActionCommand ().equals ("Mostrar mi IP y nombre de equipo")) {
				JOptionPane.showMessageDialog (null, "Mi IP actual es " + InetAddress.getLocalHost ().getHostAddress () + "\ny mi nombre de equipo es " + InetAddress.getLocalHost ().getHostName ());
			}
		} catch (Exception e) {
			System.out.println ("Error: " + e.toString ());
		}
	}
	
	/**
	 * Crea un objeto de tipo AdministradorHiloEnviar (encargado de hacer llegar el mensaje al equipo cliente) con la ip del equipo al que se le env&iacute;a el mensaje y el puerto global.
	 *
	 * @param msj como <code><b>cadena</b></code> que contine el mensaje que se le va a enviar a otro equipo que est&eacute; ejecutando la aplicaci&oacute;n Estacion.
	 */
	public void enviarMensajeAseleccionados (String msj) {
		boolean ejecutado = false;

		for (short j = 0; j < dtm.getRowCount (); j++) {
			if ((boolean)dtm.getValueAt (j, 0)) {
				new AdministradorHiloEnviar (msj, dtm.getValueAt (j, 1).toString (), puerto, dtm, j).start ();
				ejecutado = true;
			}
		}
		
		if (!ejecutado)
			JOptionPane.showMessageDialog (null, "Por favor seleccione almenos un equipo para realizar esta operaci\u00f3n");
	}
}
