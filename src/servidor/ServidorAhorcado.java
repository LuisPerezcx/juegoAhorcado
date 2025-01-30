package servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorAhorcado {
    public static ExecutorService pool = Executors.newFixedThreadPool(2);
    private static final int PUERTO = 5000;
    private static boolean ejecutando = false;
    private ServidorListener listener;
    private ServerSocket serverSocket; // Para poder cerrarlo después

    public ServidorAhorcado(ServidorListener listener){
        this.listener = listener;
    }

    public void iniciarServidor(String palabra) {

        if(ejecutando) {
            System.out.println("⚠️ El servidor ya está en ejecución.");
            return;
        }
        ejecutando = true;

        new Thread(() -> {
            enviarMensaje("🚀 Servidor iniciado en el puerto " + PUERTO);
            try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
                while (true) {
                    Socket cliente = serverSocket.accept();
                    enviarMensaje("🔹 Nuevo jugador conectado: " + cliente.getInetAddress());
                    pool.execute(new ManejadorCliente(cliente,palabra,listener)); // Manejar el cliente en un hilo separado
                }
            } catch (IOException e) {
                enviarMensaje("❌ Error en el servidor: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void enviarMensaje(String mensaje) {
        if (listener != null) {
            listener.onMensajeRecibido(mensaje);
        }
    }


    public void cerrarServidor() {
        if (!ejecutando) {
            System.out.println("⚠️ El servidor no está en ejecución.");
            return;
        }

        try {
            // Cerrar el ServerSocket para que no acepte más conexiones
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                enviarMensaje("🔴 El servidor ha sido cerrado.");
            }

            // Detener el pool de hilos y liberar recursos
            if (!pool.isShutdown()) {
                pool.shutdown();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
                enviarMensaje("🔴 Los recursos del servidor han sido liberados.");
            }

            ejecutando = false;
        } catch (IOException | InterruptedException e) {
            enviarMensaje("❌ Error al cerrar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


