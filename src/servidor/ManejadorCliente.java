package servidor;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String palabra;
    private Partida partida;

    public ManejadorCliente(Socket clienteSocket, String palabra) {
        this.clienteSocket = clienteSocket;
        this.partida = new Partida(palabra);
    }

    @Override
    public void run() {
        try {
            // Configurar flujo de entrada y salida con el cliente
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            salida = new PrintWriter(clienteSocket.getOutputStream(), true);

            salida.println("🎮 Bienvenido al Ahorcado. Adivina la palabra.");

            while (!partida.juegoTerminado()) {
                salida.println("Palabra: " + partida.getPalabraOculta());
                salida.println("Intentos restantes: " + partida.getIntentosRestantes());
                salida.println("Ingresa una letra:");

                String letra = entrada.readLine();
                if (letra != null) {
                    partida.intentarLetra(letra.charAt(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close(); // Cerrar la conexión cuando se termine
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}