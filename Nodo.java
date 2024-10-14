import java.io.*;
import java.net.*;

public class Nodo {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(30100);
            System.out.println("Nodo esperando trabajo...");

            while (true) {
                Socket maestroSocket = serverSocket.accept();
                System.out.println("Nodo aceptó conexión desde el Maestro...");

                BufferedReader in = new BufferedReader(new InputStreamReader(maestroSocket.getInputStream()));

                String libro;
                StringBuilder datos = new StringBuilder();
                while ((libro = in.readLine()) != null) {
                    if (libro.equals("_FIN_")) {
                        break;  // Fin de la lectura del libro
                    }
                    System.out.println("Libro recibido en nodo: " + libro);
                    datos.append(libro).append("\n");
                }

                String modeloEntrenado = "Modelo entrenado con: " + datos.toString().trim();
                System.out.println("Enviando modelo entrenado al Maestro...");

                PrintWriter out = new PrintWriter(maestroSocket.getOutputStream(), true);
                out.println(modeloEntrenado);

                System.out.println("Modelo entrenado enviado: " + modeloEntrenado);

                maestroSocket.close();  // Cerrar conexión con el Maestro
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

