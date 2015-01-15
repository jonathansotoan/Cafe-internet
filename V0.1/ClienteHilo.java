import java.io.DataOutputStream;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.net.Socket;
import java.awt.Color;

public class ClienteHilo extends Thread {
	public Socket yo = null;
	String mensaje, ip;
	int port;
	JTextArea areaTexto;
	JLabel estado;
	
	public ClienteHilo (String mensaje, String ip, int port, JTextArea areaTexto, JLabel estado) {
		this.mensaje = mensaje;
		this.areaTexto = areaTexto;
		this.estado = estado;
		this.port = port;
		this.ip = ip;
	}
	
	public void run () {
		try {
			while (yo == null) {
   			yo = new Socket (ip, port);
				DataOutputStream salida = new DataOutputStream (yo.getOutputStream ());
	  			salida.writeUTF (mensaje);
   			yo.close();
   			estado.setForeground (Color.green);
  				areaTexto.append ("<Yo>" + mensaje + "\n");
  			}
		} catch (Exception e) {
			areaTexto.append ("<Error> " + e.getMessage () + "\n");
			estado.setForeground (Color.red);
		}
	}
}
