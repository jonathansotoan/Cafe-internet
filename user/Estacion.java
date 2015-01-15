import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Vector;

import java.net.InetAddress;

/**
 * Clase para recibir mensajes de la clase Administrador.
 *
 * @author Jonathan Alexander Soto Montoya
 * @version 1.00 29/04/2013
 * @see EstacionHiloRecibir
 * @see VentanaBloqueo
 * @see Administrador
 */
public class Estacion {
	JFrame ventana;
	static Vector puertosAbiertos;

	/**
	 * Inicializa la aplicaci&oacute;n llamando el m&eacute;todo parteGrafica().
	 */
	public Estacion () {
		parteGrafica ();
	}

	/**
	 * Instancia el vector que tendr&aacute; los puertos abiertos y abre el primero con el n&uacute;mero 5000; e inicializa el la clase.
	 *
	 * @param args como <code><b>cadena</b></code> que en esta aplicaci&oacute;n no se utiliza.
	 */
	public static void main (String args[]) {
		puertosAbiertos = new Vector (5, 5);
		abrirPuerto (5000);
		new Estacion ();
	}
	
	/**
	 * Crea todos los elementos, escuchadores y los adhiere a una ventana (JFrame) para crear la GUI principal.
	 *
	 * @throws Exception
	 */
	public void parteGrafica () {
		JButton bCrearPuerto = new JButton ("Crear nuevo puerto");
		JButton bMiIp = new JButton ("Ver mi IP y nombre de equipo");
		
		bCrearPuerto.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				try {
					abrirPuerto (Integer.parseInt (JOptionPane.showInputDialog (null, "Los puertos abiertos actualmente son"
						+ todosLosPuertos () + "\n\nintroduzca el nuevo valor", "Abrir nuevo puerto", JOptionPane.PLAIN_MESSAGE)));
				} catch (Exception e) {
					System.out.println ("Error: " + e.toString ());
				}
			}
		});

		bMiIp.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
				try {
					JOptionPane.showMessageDialog (null, "Mi IP actual es " + InetAddress.getLocalHost ().getHostAddress ()
						+ "\ny mi nombre de equipo es " + InetAddress.getLocalHost ().getHostName ());
				} catch (Exception e) {
					System.out.println ("Error: " + e.toString ());
				}
			}
		});
		
		ventana = new JFrame ("Aporemote");
		ventana.setLayout (new GridLayout (1, 2, 10, 10));

		ventana.add (bCrearPuerto);
		ventana.add (bMiIp);

		ventana.pack ();
  		ventana.setDefaultCloseOperation (javax.swing.WindowConstants.EXIT_ON_CLOSE);
		ventana.setVisible (true);
	}
  	
	/**
	 * Devuelve una cadena con todos los puertos que est&aacute;n actualmente abiertos.
	 *
	 * @return <code>cadena</code> o <code>String</code> con los puertos (separados por l&iacute;neas) que actualmente est&aacute;n abiertos.
	 */
  	public String todosLosPuertos () {
  		String sumatoria = "";
  		
  		for (short j = 0; j < puertosAbiertos.size (); j++) {
  			sumatoria += "\n" + (j + 1) + ". " + puertosAbiertos.elementAt (j);
  		}
  		
  		return sumatoria;
  	}
	
	/**
	 * Abre un nuevo puerto.
	 *
	 * @param port como <code><b>entero</b></code> para definir el n&uacute;mero del nuevo puerto a abrir.
	 */
	public static void abrirPuerto (int port) {
		puertosAbiertos.addElement (port);
		new EstacionHiloRecibir (port).start ();
	}
}