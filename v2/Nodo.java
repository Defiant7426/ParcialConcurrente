import java.io.*;
import java.net.*;

public class Nodo {
    public static void main(String[] args) {
        int puertoNodo = 30100;  // Puerto donde este nodo escucha
        String ipMaestro = "localhost";  // Cambiar por la IP del Maestro si es necesario

        try {
            // Registrar el nodo con el Maestro
            try (Socket registroSocket = new Socket(ipMaestro, 5000)) {
                PrintWriter out = new PrintWriter(registroSocket.getOutputStream(), true);

                // Enviar la IP y puerto de este nodo al Maestro
                String ipNodo = InetAddress.getLocalHost().getHostAddress();
                out.println(ipNodo);
                out.println(puertoNodo);
                System.out.println("Nodo registrado con IP: " + ipNodo + " y puerto: " + puertoNodo);
            }

            // Iniciar un servidor para recibir libros del Maestro
            ServerSocket serverSocket = new ServerSocket(puertoNodo);
            System.out.println("Nodo esperando trabajo en el puerto " + puertoNodo + "...");

            while (true) {
                Socket maestroSocket = serverSocket.accept();
                System.out.println("Nodo aceptó conexión desde el Maestro...");

                BufferedReader in = new BufferedReader(new InputStreamReader(maestroSocket.getInputStream()));
                StringBuilder datos = new StringBuilder();
                String libro;

                // Leer el libro enviado por el Maestro
                while ((libro = in.readLine()) != null) {
                    if (libro.equals("_FIN_")) {
                        break;  // Fin de la lectura del libro
                    }
                    System.out.println("Libro recibido en nodo: " + libro);
                    datos.append(libro).append("\n");
                }

                // Entrenar un modelo ficticio usando los datos recibidos
                String modeloEntrenado = "Modelo entrenado con: " + datos.toString().trim();
                System.out.println("Enviando modelo entrenado al Maestro...");

                PrintWriter out = new PrintWriter(maestroSocket.getOutputStream(), true);
                out.println(modeloEntrenado);  // Enviar el modelo entrenado

                System.out.println("Modelo entrenado enviado: " + modeloEntrenado);
                maestroSocket.close();  // Cerrar conexión con el Maestro
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
