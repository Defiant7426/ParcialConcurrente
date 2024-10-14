import java.io.*;
import java.net.*;

public class ClienteJava {
    public static void main(String[] args) {
        String libro = "Este es el contenido del libro que quiero enviar al Maestro.";

        try {
            // Conectar con el Maestro en el puerto 5000
            Socket socket = new Socket("localhost", 5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Enviar el libro al Maestro
            out.println(libro);

            // Cerrar el socket después de enviar los datos
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
