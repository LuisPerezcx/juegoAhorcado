package servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorAhorcado {
    private static final int PUERTO = 5000;
    private static ExecutorService pool = Executors.newFixedThreadPool(15);
    private static boolean ejecutando = false;
    private static String palabraSecreta;


    public static void iniciarServidor(String palabra) {
        if(ejecutando) {
            System.out.println("⚠️ El servidor ya está en ejecución.");
            return;
        }
        ejecutando = true;
        palabraSecreta = palabra;

        new Thread(() -> {
            System.out.println("🚀 Servidor iniciado en el puerto " + PUERTO);
            try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
                while (true) {
                    Socket cliente = serverSocket.accept();
                    System.out.println("🔹 Nuevo jugador conectado: " + cliente.getInetAddress());
                    PrintWriter salidaCliente = new PrintWriter(cliente.getOutputStream(), true);
                    pool.execute(new ManejadorCliente(cliente, palabraSecreta, salidaCliente));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void detenerServidor() {
        ejecutando = false;
        pool.shutdown();  // Detenemos el pool de hilos
        System.out.println("🛑 Servidor detenido.");
    }
    public static void main(String[] args) {
        // Iniciar el servidor con una palabra secreta
        iniciarServidor("JAVA");
    }
}
