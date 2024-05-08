import java.io.*;
import java.net.*;
import java.util.List;

public class Nodo implements Runnable {
    private static final String SERVER_IP = "192.168.0.3";  // Dirección IP del servidor
    private static final int PORT = 12345;  // Puerto al que se conectarán los nodos

    public void run() {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Conectado al servidor " + SERVER_IP + ".");
            // Recepción de datos desde el servidor
            List<Persona> personas = (List<Persona>) in.readObject();

            // Procesamiento de los datos recibidos
            for (Persona p : personas) {
                if (p.ingresosMensuales > 30000) {
                    System.out.println(p.nombre + " tiene un riesgo bajo por ingresos altos.");
                } else if (p.edad < 40) {
                    System.out.println(p.nombre + " tiene un riesgo bajo por edad.");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudo conectar al servidor en " + SERVER_IP + ":" + PORT);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new Nodo()).start();
    }
}
