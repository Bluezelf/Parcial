import java.io.*;
import java.net.*;
import java.util.*;

public class Productor {
    private static final int PORT = 12345; // Puerto del servidor

    public static void main(String[] args) throws IOException {
        List<Persona> personas = leerPersonasDeCSV("data.csv");

        // Asegurarse de que hay suficientes personas para distribuir entre los nodos
        if (personas.size() < 20) {
            System.err.println("No hay suficientes datos para distribuir entre los nodos.");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en el puerto " + PORT);

            // Aceptar conexiones de dos nodos
            try (Socket nodo1Socket = serverSocket.accept();
                 ObjectOutputStream nodo1Out = new ObjectOutputStream(nodo1Socket.getOutputStream());
                 Socket nodo2Socket = serverSocket.accept();
                 ObjectOutputStream nodo2Out = new ObjectOutputStream(nodo2Socket.getOutputStream())) {

                System.out.println("Nodos conectados.");

                // Envío de datos a los nodos
                nodo1Out.writeObject(new ArrayList<>(personas.subList(0, 10)));
                nodo2Out.writeObject(new ArrayList<>(personas.subList(10, 20)));
            }
        }
    }

    private static List<Persona> leerPersonasDeCSV(String archivo) {
        List<Persona> personas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            br.readLine(); // Saltar la cabecera
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length < 3) continue;
                personas.add(new Persona(values[0], Integer.parseInt(values[1].trim()), Integer.parseInt(values[2].trim())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return personas;
    }
}
