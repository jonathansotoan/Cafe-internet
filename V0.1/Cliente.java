/*Importaciones*/

//javax.swing (entorno grafico)
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

//java.awt (organizacion y fuente)
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;

//java.awt.event (Escuchadores de eventos)
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import java.net.InetAddress;
import java.util.StringTokenizer;
/*Fin importaciones*/

public class Cliente extends JFrame implements ActionListener {
	int puerto = 5000;
	JButton bEnviar;
	JTextField mensajes;
	JTextArea areaTexto;
	JLabel titulo, conectado;
	JComboBox barraDesplegable;
	String ipS;

	//cuando se crea un objeto de esta clase se hace la interfaz grafica
	public Cliente () {
		parteGrafica ();
	}

	//crea un nuevo objeto de esta clase
  	public static void main (String[] args) {
  		new Cliente ();
  	}

	//hace la interfaz
  	public void parteGrafica () {
  		JFrame ventana;
  		JMenuBar barra;
  		JMenu config;
  		JMenuItem puerto, ip;
  		JPanel barraInferior, barraInferior2, barraInferior3, p1;
  		JScrollPane jsp;
  	
  		ventana = new JFrame ("Aporemote");
  		ventana.setDefaultCloseOperation (javax.swing.WindowConstants.EXIT_ON_CLOSE);
  		ventana.setSize (500, 500);
  		ventana.setLayout (new BorderLayout (20, 20));
  		titulo = new JLabel ("Administrador", JLabel.CENTER);
  		titulo.setFont (new Font ("Arial", Font.BOLD, 50));
  		barraInferior = new JPanel (new GridLayout (2, 1, 10, 10));
  		barraInferior2 = new JPanel (new BorderLayout (30, 10));
  		barraInferior3 = new JPanel (new BorderLayout (10, 10));
  		p1 = new JPanel (new BorderLayout (10, 10));
  		areaTexto = new JTextArea ("<Sistema>Bienvenido a Aporemote!\n");
  		areaTexto.setEditable (false);
  		jsp = new JScrollPane (areaTexto);
  		mensajes = new JTextField ();
  		bEnviar = new JButton ("Enviar");
  		conectado = new JLabel ("Conectado");
  		bEnviar.addActionListener (this);
		mensajes.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					bEnviar.doClick ();
				}
			}
		}
		);
  		barraDesplegable = new JComboBox ();
		
		try {
			Connection con = DriverManager.getConnection ("jdbc:mysql://localhost:3306/cafeinternet","root","139595");
			Statement st = con.createStatement ();
			ResultSet rs = st.executeQuery ("select * from equipos");
			rs.next ();
			ipS = rs.getObject ("ip").toString ();
			do {
				barraDesplegable.addItem (rs.getObject ("nombre") + " " + rs.getObject ("ip"));
			} while (rs.next ());
			barraDesplegable.addItem ("Otro...");
  		} catch (Exception e) {
			System.out.println ("Error: " + e.toString ());
		}
		
  		//menu
  		barra = new JMenuBar ();
  		config = new JMenu ("Configuraci\u00f3n");
  		puerto = new JMenuItem ("Establecer puerto");
  		ip = new JMenuItem ("Ver mi IP y nombre de equipo");
  		puerto.addActionListener (this);
  		ip.addActionListener (this);
		barraDesplegable.addActionListener (this);
  		config.add (puerto);
  		config.add (ip);
  		barra.add (config);

  		barraInferior2.add (mensajes, BorderLayout.CENTER);
  		barraInferior2.add (bEnviar, BorderLayout.EAST);
  		barraInferior3.add (barraDesplegable, BorderLayout.CENTER);
  		barraInferior3.add (conectado, BorderLayout.EAST);
  		barraInferior.add (barraInferior2);
  		barraInferior.add (barraInferior3);
  		p1.add (barra, BorderLayout.NORTH);
  		p1.add (titulo, BorderLayout.CENTER);
		ventana.add (p1, BorderLayout.NORTH);
		ventana.add (jsp, BorderLayout.CENTER);
		ventana.add (barraInferior, BorderLayout.SOUTH);
		ventana.setVisible (true);
  	}
  	
  	//escuchador de eventos
  	public void actionPerformed (ActionEvent ae) {
		try {
	  		//si se presiona el boton de enviar
	  		if (ae.getSource () == bEnviar) {
	  				//Se crea un hilo para enviar mensajes al servidor
		  			new ClienteHilo (mensajes.getText (), ipS, puerto, areaTexto, conectado).start ();
		  			mensajes.setText ("");
	  		} else if (ae.getSource () == barraDesplegable) {
				if (barraDesplegable.getSelectedItem ().toString ().equals ("Otro..."))
					ipS = JOptionPane.showInputDialog ("Ingrese la IP para conectarse");
				else {
					StringTokenizer st = new StringTokenizer (barraDesplegable.getSelectedItem ().toString ());
					st.nextToken ();
					ipS = st.nextToken ();
				}

				JOptionPane.showMessageDialog (null, "IP para conectarse cambiada a " + ipS);
			} else if (ae.getActionCommand ().equals ("Establecer puerto")) {
	  			puerto = Integer.parseInt (JOptionPane.showInputDialog (null, "El puerto actual es " + puerto + ", introduzca el nuevo valor", "Establecer puerto", JOptionPane.PLAIN_MESSAGE));
				JOptionPane.showMessageDialog (null, "El puerto ha sido cambiado a " + puerto);
	  		} else if (ae.getActionCommand ().equals ("Ver mi IP y nombre de equipo"))
	  			JOptionPane.showMessageDialog (null, "Mi IP actual es " + InetAddress.getLocalHost ().getHostAddress () + "\ny mi nombre de equipo es " + InetAddress.getLocalHost ().getHostName ());
  		} catch (Exception e){
	  		areaTexto.append ("<Error>" + e.getMessage() + "\n");
	  	}
  	}
}
