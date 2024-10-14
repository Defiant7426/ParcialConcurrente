import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Maestro {
    private static List<String> modelosEntrenados = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Maestro esperando conexión...");

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Conexión de cliente recibida.");

                BufferedReader in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

                // Leer libro enviado por el cliente
                String libro;
                while ((libro = in.readLine()) != null) {
                    System.out.println("Libro recibido: " + libro);

                    // Enviar libro a los nodos para que procesen
                    entrenarEnNodos(libro);
                }

                clienteSocket.close();  // Cerrar conexión con el Cliente
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void entrenarEnNodos(String libro) {
        try {
            // Mensaje de depuración antes de la conexión
            System.out.println("Intentando conectarse al nodo...");

            // Cambia "localhost" por la IP de la máquina donde está el nodo si es necesario
            Socket nodoSocket = new Socket("10.0.2.15", 30100);
            System.out.println("Conexión al nodo exitosa.");

            PrintWriter out = new PrintWriter(nodoSocket.getOutputStream(), true);
            out.println(libro);  // Enviar libro al nodo
            out.println("_FIN_");  // Fin de la lectura del libro

            BufferedReader in = new BufferedReader(new InputStreamReader(nodoSocket.getInputStream()));
            String modelo = in.readLine();
            modelosEntrenados.add(modelo);
            System.out.println("Modelo recibido desde el nodo: " + modelo);

            modelosEntrenados.add(modelo);
            nodoSocket.close();  // Cerrar conexión con el nodo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

