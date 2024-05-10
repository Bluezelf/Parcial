import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Productor {
    private static final int PORT = 12345;

    private static List<Integer> SCORES;
    private static final int limiteEdad = 45;
    private static final int limiteIngreso = 5000;
    private static final int limitePrestamo = 24000;
    private static final int limite_cuotas = 24;

    public static void main(String[] args) throws IOException{
        int sizeCSV = sizeCSV("D:\\\\UNI\\\\2024-1\\\\Cursos\\\\Programacion Concurrente y Distribuida\\\\Parcial\\\\dataset.csv");
        //D:\UNI\2024-1\Cursos\Programacion Concurrente y Distribuida\Parcial

        List<Socket> clients = new ArrayList<>();
        List<ClientHandler> handlers = new ArrayList<>();
        SCORES = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(32,0)));

        ServerSocket server = new ServerSocket(PORT);
        Scanner sc = new Scanner(System.in);
        boolean pendingConnections = true;
        int num_workers = 0;

        while(pendingConnections){
            System.out.println("Esperando conexion...");
            Socket client = server.accept();
            num_workers++;
            clients.add(client);
            System.out.println("Se ha conectado un trabajador");
            System.out.println("Listo para trabajar? (Y/N)");
            String answer = sc.nextLine();
            if (answer.equals("Y") || answer.equals("y")){
                System.out.println("Empezando trabajos...");
                pendingConnections = false;
            }
        }

        long inicio = System.currentTimeMillis();

        for(int i = 0; i < num_workers; i++){
            handlers.add(new ClientHandler(clients.get(i), sizeCSV, i, num_workers));
            handlers.get(i).start();
        }

        for(int i = 0; i < num_workers; i++){
            try {
                handlers.get(i).join();
            } catch (InterruptedException e) {
                System.out.println("Algo paso al juntar los hilos AUXILIO QUIERO DORMIR");
                throw new RuntimeException(e);
            }
        }

        long fin = System.currentTimeMillis();

        for (int d: SCORES){
            System.out.print(d + " ");
        }

        System.out.println("\nTiempo transcurrido para entrenamiento: " + (fin-inicio) + "ms");
        server.close();

        System.out.println("\nAnalizando las posibilidades de prestamo de una persona que");
        System.out.printf("\tEs mayor de %d años?: ", limiteEdad);
        int edad = sc.nextInt();
        System.out.print("\tEs hombre? : ");
        int sexo = sc.nextInt();
        System.out.printf("\tGana más de %d soles mensualmente?: ", limiteIngreso);
        int ingreso = sc.nextInt();
        System.out.printf("\tPide más de %d soles como prestamo?: ", limitePrestamo);
        int prestamo = sc.nextInt();
        System.out.printf("\tEn más de %d cuotas?: ", limite_cuotas);
        int cuotas = sc.nextInt();

        int index = (int) (Math.pow(edad,4) + Math.pow(sexo,4) + Math.pow(ingreso,2) + Math.pow(prestamo,1) + cuotas);
        System.out.println("Obtendra prestamo? " + (SCORES.get(index) > 0));
    }

    public static int sizeCSV(String route){
        Scanner scanner;
        try {
            scanner = new Scanner(new File(route));
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado! Verificar la ruta!");
            throw new RuntimeException(e);
        }
        scanner.nextLine();
        int CSV_size = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            CSV_size++;
        }
        return CSV_size;
    }

    public static class ClientHandler extends Thread{
        Socket client;
        int work_load;
        int work_total;
        int worker_id;
        int worker_total;
        BufferedWriter writer;
        BufferedReader reader;

        ClientHandler(Socket client, int sizeCSV, int worker_id, int num_workers){
            this.work_total = sizeCSV;
            this.work_load = sizeCSV / num_workers;
            this.worker_id = worker_id;
            this.worker_total = num_workers;
            try {
                this.client = client;
                this.writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            String sendToClient;
            String receivedFromClient;
            int inicio = worker_id * work_load;
            int fin;
            if (worker_id != (worker_total - 1)){
                fin = (worker_id + 1) * work_load;
            }
            else{
                fin = work_total;
            }

            sendToClient = String.format("%d,%d,%d,%d,%d,%d\n", limiteEdad,limiteIngreso,limitePrestamo,limite_cuotas,inicio, fin);
            try {
                writer.write(sendToClient);
                writer.flush();
            } catch (IOException e) {
                System.out.println("Error al enviar datos al cliente!");
                throw new RuntimeException(e);
            }

            try {
                receivedFromClient = reader.readLine();
                String[] valores = receivedFromClient.split(",");

                for(int j = 0; j < valores.length; j++){
                    int toSum = SCORES.get(j);
                    SCORES.set(j, toSum + Integer.parseInt(valores[j]));
                }
            } catch (IOException e){
                System.out.println("Error al recibir datos del cliente!");
                throw new RuntimeException(e);
            }
        }
    }
}
