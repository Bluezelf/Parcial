import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Trabajador {
    private static int limiteEdad;
    private static int limiteIngreso;
    private static int limitePrestamo;
    private static int limiteCuotas;
    private static int inicio;
    private static int fin;


    public static void main (String[] args) throws IOException {
        List<Integer> returnValues = new ArrayList<>(Collections.nCopies(32,0));

        Socket socket = new Socket("localhost",12345);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String masterInput;

        try{
            masterInput = reader.readLine();
            String[] masterInputs = masterInput.split(",");
            limiteEdad = Integer.parseInt(masterInputs[0]);
            limiteIngreso = Integer.parseInt(masterInputs[1]);
            limitePrestamo = Integer.parseInt(masterInputs[2]);
            limiteCuotas = Integer.parseInt(masterInputs[3]);
            inicio = Integer.parseInt(masterInputs[4]);
            fin = Integer.parseInt(masterInputs[5]);

        }catch (IOException e){
            System.out.println("Error al recibir datos del cliente!");
            throw new RuntimeException(e);
        }

        List<String> records = readCSV("D:\\\\UNI\\\\2024-1\\\\Cursos\\\\Programacion Concurrente y Distribuida\\\\Parcial\\\\dataset.csv",inicio,fin);

        for(int i = 0; i < records.size(); i++){
            int index = getIndex(records.get(i));
            if (prediction(records.get(i))){
                returnValues.set(index, returnValues.get(index)+1);
            }
            else{
                returnValues.set(index, returnValues.get(index) - 1);
            }
        }

        String returnString = returnValues.stream().map(Object::toString)
                .collect(Collectors.joining(","));

        writer.write(returnString);
        writer.flush();

        socket.close();
    }

    public static List<String> readCSV(String route, int inicio, int fin){
        List<String> records = new ArrayList<>();
        int head = 0;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(route));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            reader.readLine(); // Saltar cabecera
            String line;
            while(head < inicio){
                reader.readLine();
                head++;
            }
            for(head = inicio; head < fin; head++){
                line = reader.readLine();
                records.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return records;
    }

    public static boolean prediction(String row){
        String[] values = row.split(",");
        int prediction = Integer.parseInt(values[values.length - 1]);
        return prediction == 1;
    }

    public static int getIndex(String row){
        int index = 0;
        String[] values = row.split(",");

        if (Integer.parseInt(values[0]) > limiteEdad){
            index += 16;
        }

        if (Integer.parseInt(values[1]) == 1){
            index += 8;
        }

        if (Integer.parseInt(values[2]) > limiteIngreso){
            index += 4;
        }

        if (Integer.parseInt(values[3]) > limitePrestamo){
            index += 2;
        }

        if (Integer.parseInt(values[4]) > limiteCuotas){
            index += 1;
        }
        return index;
    }

}
