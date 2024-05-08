import java.io.*;
import java.net.*;
import java.util.List;

public class Nodo implements Runnable {
    private final String host;
    private final int port;

    public Nodo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port);
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Conectado al servidor.");
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
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new Nodo("localhost", 12345)).start();
    }
}
