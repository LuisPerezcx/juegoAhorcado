package servidor;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private Partida partida;

    public ManejadorCliente(Socket socket, String palabra) {
        this.socket = socket;
        this.partida = new Partida(palabra);
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

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

            salida.println("✅ Juego terminado. La palabra era: " + partida.getPalabra());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}