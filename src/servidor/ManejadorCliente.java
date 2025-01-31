package servidor;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private Partida partida;
    private ServidorListener listener;

    public ManejadorCliente(Socket clienteSocket, String palabra, ServidorListener listener) {
        this.clienteSocket = clienteSocket;
        this.partida = new Partida(palabra);
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            // Configurar flujo de entrada y salida con el cliente
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            salida = new PrintWriter(clienteSocket.getOutputStream(), true);

            enviarMensaje("🎮 Bienvenido al Ahorcado. Adivina la palabra.");


            while (!partida.juegoTerminado()) {
                enviarMensaje("Palabra: " + partida.getPalabraOculta());
                enviarMensaje("Intentos restantes: " + partida.getIntentosRestantes());
                enviarMensaje("Ingresa una letra:");

                String letra = entrada.readLine();
                if (letra != null) {
                    enviarMensaje("Letra: "+ letra);
                    partida.intentarLetra(letra.charAt(0));
                } else {
                    System.out.println("El cliente cerró la conexión");

                    JOptionPane.showMessageDialog(
                            null,
                            "El cliente ha cerrado la conexión. El servidor se cerrará.",
                            "Conexión cerrada",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    System.exit(0); // Cierra el programa
                }
            }
            if(partida.juegoTerminado()){
                if (partida.getPalabraOculta().equals(partida.getPalabra())) {
                    enviarMensaje("🎉 ¡Felicidades! Has adivinado la palabra: " + partida.getPalabra());
                } else {
                    enviarMensaje("💥 ¡Lo siento! Has perdido. La palabra era: " + partida.getPalabra());
                }
                enviarMensaje("Juego terminado");
            }
        } catch (IOException e) {
            enviarMensaje("❌ Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void enviarMensaje(String mensaje) {
        salida.println(mensaje); // Enviar al cliente
        if (listener != null) {
            listener.onMensajeRecibido(mensaje); // Notificar al servidor
        }
    }

    public void cerrarConexion() {
        try {
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close(); // Cerrar el socket del cliente
                System.out.println("Conexión cerrada para el cliente.");
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión del cliente: " + e.getMessage());
        }
    }

}