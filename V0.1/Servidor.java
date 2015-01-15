import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Vector;

import java.net.InetAddress;

public class Servidor extends JFrame implements ActionListener {
	static Vector puertosAbiertos = new Vector (5, 5);
	static JTextArea areaTexto;

	public Servidor () {
		parteGrafica ();
	}

  	public static void main (String[] args) {
		puertosAbiertos.addElement (5000);
  		new Servidor ();
  	
  		try {
	  		new ServidorHilo (areaTexto, Integer.parseInt (puertosAbiertos.elementAt (0).toString ())).start ();
			String comando[] = {"rundll32.exe", "user32.dll,LockWorkStation"};
			Runtime.getRuntime ().exec (comando);
  		} catch (Exception e) {
  			areaTexto.append ("<Error>" + e.getMessage() + "\n");
  		}
  	}

  	public void parteGrafica () {
		JLabel titulo;
		JFrame ventana;
  		JMenuBar barra;
  		JMenu config;
  		JMenuItem puerto, ip;
		JPanel p1;
		JScrollPane jsp;
  	
  		ventana = new JFrame ("Aporemote");
  		ventana.setDefaultCloseOperation (javax.swing.WindowConstants.EXIT_ON_CLOSE);
  		ventana.setSize (500, 500);
  		ventana.setLayout (new BorderLayout (20, 20));
  		titulo = new JLabel ("Estaci\u00f3n", JLabel.CENTER);
  		titulo.setFont (new Font ("Arial", Font.BOLD, 50));
  		p1 = new JPanel (new BorderLayout (10, 10));
  		areaTexto = new JTextArea ("<Sistema>Bienvenido a Aporemote!\n");
  		areaTexto.setEditable (false);
  		jsp = new JScrollPane (areaTexto);

		//menu
  		barra = new JMenuBar ();
  		config = new JMenu ("Configuraci\u00f3n");
  		puerto = new JMenuItem ("Crear nuevo puerto");
  		ip = new JMenuItem ("Ver mi IP y nombre de equipo");
  		puerto.addActionListener (this);
  		ip.addActionListener (this);
  		config.add (puerto);
  		config.add (ip);
  		barra.add (config);
  		
  		p1.add (barra, BorderLayout.NORTH);
  		p1.add (titulo, BorderLayout.CENTER);
		ventana.add (p1, BorderLayout.NORTH);
		ventana.add (jsp, BorderLayout.CENTER);
		ventana.setVisible (true);
  	}
  	
  	public void actionPerformed (ActionEvent ae) {
  		try {
	  		if (ae.getActionCommand ().equals ("Crear nuevo puerto")) {
	  			puertosAbiertos.addElement (Integer.parseInt (JOptionPane.showInputDialog (null, "Los puertos abiertos actualmente son"
	  				+ todosLosPuertos () + "\n\nintroduzca el nuevo valor", "Abrir nuevo puerto", JOptionPane.PLAIN_MESSAGE)));
	  			new ServidorHilo (areaTexto, Integer.parseInt (puertosAbiertos.elementAt (puertosAbiertos.size () - 1).toString ())).start ();
	  		} else if (ae.getActionCommand ().equals ("Ver mi IP y nombre de equipo"))
	  			JOptionPane.showMessageDialog (null, "Mi IP actual es " + InetAddress.getLocalHost ().getHostAddress () + "\ny mi nombre de equipo es " + InetAddress.getLocalHost ().getHostName ());
		 } catch (Exception e) {
		 	areaTexto.append ("<Error>" + e.getMessage () + "\n");
		 }
  	}
  	
  	public String todosLosPuertos () {
  		String sumatoria = "";
  		
  		for (short j = 0; j < puertosAbiertos.size (); j++) {
  			sumatoria += "\n" + (j + 1) + ". " + puertosAbiertos.elementAt (j);
  		}
  		
  		return sumatoria;
  	}
}
