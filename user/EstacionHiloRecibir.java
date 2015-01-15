import java.net.ServerSocket;
import java.net.Socket;

import java.io.DataInputStream;

import java.util.StringTokenizer;

/**
 * Clase que extiende de Thread para abrir un puerto que escuche constantemente los mensajes de entrada.
 *
 * @author Jonathan Alexander Soto Montoya
 * @version 1.00 29/04/2013
 * @see Estacion
 * @see VentanaBloqueo
 */
public class EstacionHiloRecibir extends Thread {
	boolean bloqueado = true;
	int puerto;
	VentanaBloqueo vb;
	
	/**
	 * Constructor que inicializa el puerto y bloquea la pantalla.
	 *
	 * @param puerto como <code><b>entero</b></code> para definir el puerto que se abrir&aacute;.
	 */
	public EstacionHiloRecibir (int puerto) {
		this.puerto = puerto;
		vb = new VentanaBloqueo ();
	}
	
	/**
	 * Se ejecuta siempre y est&aacute; a la espera de recibir los mensajes.
	 *
	 * @throws Exception
	 */
	public void run () {
		DataInputStream entrada;
		ServerSocket yo;
		Socket cliente;
		try {
			while (true) {
				yo = new ServerSocket (puerto);
		  		System.out.println ("Socket escuchando en puerto " + puerto);
				cliente = yo.accept ();
				entrada = new DataInputStream (cliente.getInputStream());
		  		System.out.println ("Ya se conecto el cliente");
				
				if (entrada != null) {
					String msj = entrada.readUTF ();
					if (msj.equals ("bloquear")) {
						if (!bloqueado) {
							vb = new VentanaBloqueo ();
							bloqueado = true;
						}
					} else if (msj.equals ("desbloquear")) {
						if (bloqueado) {
							vb.cerrar (true);
							bloqueado = false;
						}
					} else if (!msj.equals ("are you on?"))
						Runtime.getRuntime ().exec (convertirAvector (msj));
				}

				cliente.close();
				yo.close();
			}
		} catch (Exception e) {
			vb.cerrar (true);
			System.out.println ("Error: " + e.toString ());
		}
	}
	
	/**
	 * Convierte una cadena de texto a un vector de cadenas (separa las palabras y las mete en un vector)
	 *
	 * @param cad como <code><b>cadena</b></code> para definir la cadena que se convertir&aacute; a vector.
	 * @return <code>vector</code> de cadenas con las palabras que ten&iacute;a el par&aacute;metro de entrada <code>cad</code>.
	 */
	public static String[] convertirAvector (String cad) {
		StringTokenizer st = new StringTokenizer (cad);
		String cmd[] = new String[st.countTokens ()];
		for (short j = 0; j < cmd.length; j++)
			cmd[j] = st.nextToken ();
		
		return cmd;
	}
}