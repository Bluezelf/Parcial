import java.io.*;
import java.util.*;

public class Productor {
    public static void main(String[] args) {
        List<Persona> personas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data.csv"))) {
            String line;
            br.readLine(); // Saltar la cabecera del CSV
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;  // Ignorar líneas vacías

                String[] values = line.split(";");
                if (values.length < 3) {
                    System.err.println("Línea incompleta o mal formada: " + line);
                    continue;  // Saltar líneas mal formadas
                }

                try {
                    String nombre = values[0].trim();
                    int edad = Integer.parseInt(values[1].trim());
                    int ingresosMensuales = Integer.parseInt(values[2].trim());
                    personas.add(new Persona(nombre, edad, ingresosMensuales));
                } catch (NumberFormatException e) {
                    System.err.println("Error al parsear números en la línea: " + line);
                    // Manejar o registrar el error de parsing
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Persona[] personasArray = personas.toArray(new Persona[0]);
        Nodo nodo1 = new Nodo(Arrays.copyOfRange(personasArray, 0, 10), "1");
        Nodo nodo2 = new Nodo(Arrays.copyOfRange(personasArray, 10, 20), "2");

        Thread t1 = new Thread(nodo1);
        Thread t2 = new Thread(nodo2);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
