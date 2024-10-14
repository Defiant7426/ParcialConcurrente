import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Maestro {
    private static List<NodoInfo> nodosConectados = Collections.synchronizedList(new ArrayList<>());
    private static final int PORT = 5000;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Maestro esperando conexiones en el puerto " + PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(new ConnectionHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConnectionHandler implements Runnable {
        private final Socket socket;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                // Verificar si es un nodo o un cliente
                String line1 = in.readLine();
                String line2 = in.readLine();

                if (line2 != null && line2.matches("\\d+")) {
                    // Es un nodo registrándose
                    String ip = line1;
                    int port = Integer.parseInt(line2);
                    nodosConectados.add(new NodoInfo(ip, port));
                    System.out.println("Nodo registrado: " + ip + ":" + port);
                } else {
                    // Es un cliente enviando un libro
                    String libro = line1;
                    System.out.println("Libro recibido: " + libro);
                    distribuirLibro(libro);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void distribuirLibro(String libro) {
            for (NodoInfo nodo : nodosConectados) {
                threadPool.execute(() -> enviarLibroANodo(libro, nodo));
            }
        }

        private void enviarLibroANodo(String libro, NodoInfo nodo) {
            try (Socket nodoSocket = new Socket(nodo.getIp(), nodo.getPort())) {
                PrintWriter out = new PrintWriter(nodoSocket.getOutputStream(), true);
                out.println(libro);
                out.println("_FIN_");

                BufferedReader in = new BufferedReader(new InputStreamReader(nodoSocket.getInputStream()));
                String modelo = in.readLine();
                System.out.println("Modelo recibido de " + nodo.getIp() + ": " + modelo);
            } catch (IOException e) {
                System.err.println("Error al conectar con nodo " + nodo.getIp() + ":" + nodo.getPort());
                e.printStackTrace();
            }
        }
    }

    private static class NodoInfo {
        private final String ip;
        private final int port;

        public NodoInfo(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }
    }
}
