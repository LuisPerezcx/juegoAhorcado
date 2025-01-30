package servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorAhorcado {
    public static ExecutorService pool = Executors.newFixedThreadPool(2);
    private static final int PUERTO = 5000;
    private static boolean ejecutando = false;

    public void iniciarServidor(String palabra) {

        if(ejecutando) {
            System.out.println("⚠️ El servidor ya está en ejecución.");
            return;
        }
        ejecutando = true;

        new Thread(() -> {
            System.out.println("🚀 Servidor iniciado en el puerto " + PUERTO);
            try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
                while (true) {
                    Socket cliente = serverSocket.accept();
                    System.out.println("🔹 Nuevo jugador conectado: " + cliente.getInetAddress());
                    pool.execute(new ManejadorCliente(cliente,palabra)); // Manejar el cliente en un hilo separado
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}


