import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;

/**
 * Clase que env&iacute;a un mensaje al equipo que tenga la aplicaci&oacute;n Estacion activa, siempre y cuando se especifique la IP y el puerto de dicho equipo.
 * 
 * @author Jonathan Alexander Soto Montoya
 * @version 1.00 29/04/2013
 * @see Administrador
 */
public class AdministradorHiloEnviar extends Thread {
	public Socket yo = null;
	String mensaje, ip;
	int port;
	short fila;
	DefaultTableModel dtm;
	
	/**
	 * @param mensaje como <code><b>cadena</b></code> que define el mensaje a enviar.
	 * @param ip como <code><b>cadena</b></code> que define la IP del usuario al que se le enviar&aacute; el mensaje.
	 * @param port como <b><code>entero</code></b> que define el puerto por el que se conectar&aacute; al usuario (debe estar abierto en el equipo que recibir&aacute; el mensaje).
	 * @param dtm como <code><b>DefaultTableModel</b></code> que define modelo de tabla al que se actualizar&aacute; el estado de conexi&oacute;n (en la columna n&uacute;mero 6).
	 * @param fila como <code><b>short</b></code> que define la fila en la que se actualizar&aacute; el estado de la conexi&oacute;n.
	 */
	public AdministradorHiloEnviar (String mensaje, String ip, int port, DefaultTableModel dtm, short fila) {
		this.mensaje = mensaje;
		this.port = port;
		this.ip = ip;
		this.dtm = dtm;
		this.fila = fila;
	}
	
	public void run () {
		try {
			while (yo == null) {
				yo = new Socket (ip, port);
				DataOutputStream salida = new DataOutputStream (yo.getOutputStream ());
	  			salida.writeUTF (mensaje);
				dtm.setValueAt ("Conectado", fila, 5);

				yo.close();
  			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage ());
			dtm.setValueAt ("Desconectado", fila, 5);
		}
	}
}