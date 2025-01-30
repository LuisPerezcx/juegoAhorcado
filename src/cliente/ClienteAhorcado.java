package cliente;
import java.io.*;
import java.net.*;

public class ClienteAhorcado {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PUERTO);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {

            // Recibir mensaje de bienvenida
            System.out.println(entrada.readLine());

            while (true) {
                System.out.print("Introduce una letra: ");
                String letra = teclado.readLine();
                salida.println(letra);

                // Recibir respuesta del servidor
                String respuesta = entrada.readLine();
                System.out.println(respuesta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
