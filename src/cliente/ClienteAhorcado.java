package cliente;

import java.io.*;
import java.net.*;

public class ClienteAhorcado {
    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PUERTO);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {

            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println(mensaje);

                if (mensaje.startsWith("Ingresa una letra:")) {
                    String letra = teclado.readLine();
                    salida.println(letra);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
