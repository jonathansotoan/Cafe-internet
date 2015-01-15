import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Bloquea la pantalla sin posibilidad de cerrarla.
 *
 * @author Jonathan Alexander Soto Montoya
 * @version 1.00 29/04/2013
 * @see EstacionHiloRecibir
 */
public class VentanaBloqueo extends JFrame {
	private boolean cerrar = false;
	private JFrame ventana;
	
	/**
	 * Constructor que inicializa la GUI de esta ventana de bloqueo.
	 */
	public VentanaBloqueo () {
		ventana = new JFrame ("Equipo bloqueado");
		ventana.setExtendedState (JFrame.MAXIMIZED_BOTH);
		ventana.add (new JLabel (new ImageIcon ("jaexjuegos.com.png")));
		ventana.setUndecorated (true);
		ventana.setAlwaysOnTop (true);
		ventana.setDefaultCloseOperation (javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		ventana.setVisible (true);
		ventana.addKeyListener (new KeyListener () {//para ahorrar recursos del pc en el que se ejecuta (solo empieza a refrescarse cuando alguien intenta cerrar la ventana por primera vez)
		
			@Override
			public void keyPressed (KeyEvent ke) {
				empezar ();
			}
			
			@Override
			public void keyReleased (KeyEvent ke) {}
			
			@Override
			public void keyTyped (KeyEvent ke) {}
		});
	}

	/**
	 * Empieza a refrescar la ventana de la aplicaci&oacute;n para que no haya tiempo de presionar las teclas
	 */
	public void empezar () {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor ();
		ses.scheduleAtFixedRate (new Runnable () {
			@Override
			public void run () {
				if (cerrar)
					ventana.setVisible (false);
				else {
					ventana.setExtendedState (JFrame.MAXIMIZED_BOTH);
					ventana.toFront ();
				}
			}
		}, 500, 50, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Cierra la ventana (solo se puede hacer desde la aplicaci&oacute;n Administrador).
	 *
	 * @param valor como <code><b>boolean</b></code> para definir si se debe cerrar la siguiente vez que se refresque la ventana.
	 */
	public void cerrar (boolean valor) {
		cerrar = valor;
	}
}