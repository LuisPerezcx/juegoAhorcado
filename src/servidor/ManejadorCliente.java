package servidor;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private Partida partida;
    private PrintWriter salidaHost;
    private PrintWriter salidaCliente;

    public ManejadorCliente(Socket socket, String palabraSecreta, PrintWriter salidaHost, PrintWriter salidaCliente) {
        this.socket = socket;
        this.partida = new Partida(palabraSecreta); // Crear una nueva partida por jugador
        this.salidaHost = salidaHost;
        this.salidaCliente = salidaCliente;
    }


    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            // Enviar bienvenida al cliente
            salidaCliente.println("🎮 Bienvenido al Ahorcado. Adivina la palabra.");
            salidaHost.println("🎮 Un jugador se ha unido. Esperando adivinanzas...");

            while (!partida.juegoTerminado()) {
                // Enviar palabra oculta y intentos restantes a ambos jugadores
                String palabraOculta = partida.getPalabraOculta();
                int intentosRestantes = partida.getIntentosRestantes();

                // Enviar al cliente
                salidaCliente.println("Palabra: " + palabraOculta);
                salidaCliente.println("Intentos restantes: " + intentosRestantes);
                salidaCliente.println("Ingresa una letra:");

                // Enviar al host
                salidaHost.println("Palabra: " + palabraOculta);
                salidaHost.println("Intentos restantes: " + intentosRestantes);

                // Leer letra del cliente
                String letra = entrada.readLine();
                if (letra != null) {
                    partida.intentarLetra(letra.charAt(0));
                    salidaHost.println("El jugador intentó la letra: " + letra.charAt(0));
                }

                // Enviar actualizaciones después de cada intento
                enviarActualizaciones(palabraOculta, intentosRestantes);
            }

            // Cuando el juego termine
            salidaCliente.println("✅ Juego terminado. La palabra era: " + partida.getPalabra());
            salidaHost.println("✅ Juego terminado. La palabra era: " + partida.getPalabra());

            socket.close(); // Cerrar la conexión con el cliente
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void enviarActualizaciones(String palabraOculta, int intentosRestantes) {
        // Enviar al cliente
        salidaCliente.println("Palabra: " + palabraOculta);
        salidaCliente.println("Intentos restantes: " + intentosRestantes);

        // Enviar al host
        salidaHost.println("Palabra: " + palabraOculta);
        salidaHost.println("Intentos restantes: " + intentosRestantes);
    }
}
