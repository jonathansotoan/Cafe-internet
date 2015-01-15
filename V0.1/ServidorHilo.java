import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JTextArea;
import java.util.StringTokenizer;

public class ServidorHilo extends Thread {
	boolean modoConsola = false;
	JTextArea areaT;
	public int port;

	public ServidorHilo (JTextArea areaT, int port) {
		this.areaT = areaT;
		this.port = port;
	}

	public void run () {
		DataInputStream entrada;
		ServerSocket yo = null;
		Socket cliente = null;
		try {
			while (true) {
				yo = new ServerSocket(port);
		  		System.out.println("Socket escuchando en puerto " + port);
				cliente = yo.accept();
				entrada = new DataInputStream (cliente.getInputStream());
		  		System.out.println("Ya se conecto el cliente");
				
				if (entrada != null) {
					String msjRecibido = entrada.readUTF ();
					if (modoConsola) {
						if (msjRecibido.equals ("salir")) {
							modoConsola = false;
							areaT.append ("<Sistema>Modo consola desactivado\n");
						} else
							Runtime.getRuntime ().exec (convertirAvector (msjRecibido));
					} else {
						if (msjRecibido.equals ("apagar")) {
							areaT.append ("<Sistema>Apagando...\n");
							Runtime.getRuntime ().exec (convertirAvector ("shutdown -s -t 0"));
						} else if (msjRecibido.equals ("reiniciar")) {
							areaT.append ("<Sistema>Reiniciando...\n");
							Runtime.getRuntime ().exec (convertirAvector ("shutdown -r -t 0"));
						} else if (msjRecibido.equals ("bloquear")) {
							areaT.append ("<Sistema>Bloqueando...\n");
							Runtime.getRuntime ().exec (convertirAvector ("rundll32.exe user32.dll,LockWorkStation"));
						} else if (msjRecibido.equals ("suspender")) {
							areaT.append ("<Sistema>Suspendiendo...\n");
							Runtime.getRuntime ().exec ("rundll32.exe PowrProf.dll,SetSuspendState");
						} else if (msjRecibido.equals ("ejecutar")) {
							modoConsola = true;
							areaT.append ("<Sistema>Modo consola activado\n");
						} else
							areaT.append ("<Cliente: " + port + ">" + msjRecibido + "\n");
					}
				}
				cliente.close();
				yo.close();
			}
		} catch (Exception e) {
			areaT.append ("<Error>" + e.toString () + "\n");
		}
	}
	
	public static String[] convertirAvector (String cad) {
		StringTokenizer st = new StringTokenizer (cad);
		String cmd[] = new String[st.countTokens ()];
		for (short j = 0; j < cmd.length; j++)
			cmd[j] = st.nextToken ();
		
		return cmd;
	}
}
