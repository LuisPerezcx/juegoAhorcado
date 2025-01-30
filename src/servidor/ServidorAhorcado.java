package servidor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ServidorAhorcado {
    public static ExecutorService pool = Executors.newFixedThreadPool(2);
    private static final int PUERTO = 5000;
    private static boolean ejecutando = false;
    private ServidorListener listener;
    private ServerSocket serverSocket; // Para poder cerrarlo después
    private List<ManejadorCliente> listaClientes = new ArrayList<>(); // Lista para manejar las conexiones de clientes
    private Thread hiloServidor; // Para manejar el hilo que ejecuta el servidor
    private volatile boolean cerrando = false; // Variable de control para cerrar el servidor de manera segura

    public ServidorAhorcado(ServidorListener listener){
        this.listener = listener;
    }

    public void iniciarServidor(String palabra) {

        if (ejecutando) {
            System.out.println("⚠️ El servidor ya está en ejecución.");
            return;
        }
        ejecutando = true;

        // Reiniciar el ThreadPoolExecutor si está apagado
        if (pool.isShutdown() || pool.isTerminated()) {
            pool = Executors.newFixedThreadPool(2);
        }


        hiloServidor = new Thread(() -> {
            enviarMensaje("🚀 Servidor iniciado en el puerto " + PUERTO);
            try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
                this.serverSocket = serverSocket;
                while (!cerrando && !pool.isShutdown()) {
                    try {
                        Socket cliente = serverSocket.accept();
                        enviarMensaje("🔹 Nuevo jugador conectado: " + cliente.getInetAddress());
                        ManejadorCliente manejadorCliente = new ManejadorCliente(cliente, palabra, listener);
                        listaClientes.add(manejadorCliente); // Guardamos el manejador para poder cerrar la conexión después
                        pool.execute(manejadorCliente); // Manejar el cliente en un hilo separado
                    }catch (SocketException e) {
                        if (cerrando) {
                            // Si el servidor está cerrando, ignoramos la excepción
                            break;
                        }
                        throw e; // Si no es por cierre del servidor, propagamos el error
                    }
                }
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    enviarMensaje("❌ Error en el servidor: " + e.getMessage());
                } else {
                    enviarMensaje("🔴 El servidor ha sido cerrado.");
                }
                e.printStackTrace();
            }
        });
        hiloServidor.start();
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
            // Establecer la variable de control para indicar que estamos cerrando el servidor
            cerrando = true;

            // Cerrar las conexiones de los clientes activos
            for (ManejadorCliente manejador : listaClientes) {
                manejador.cerrarConexion();
            }

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

            // Interrumpir el hilo que está esperando en el accept
            if (hiloServidor != null && hiloServidor.isAlive()) {
                hiloServidor.interrupt();
            }

            ejecutando = false;
        } catch (IOException | InterruptedException e) {
            enviarMensaje("❌ Error al cerrar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


