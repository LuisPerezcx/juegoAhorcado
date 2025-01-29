package servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorAhorcado {
    private static final int PUERTO = 12345;
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("🚀 Servidor iniciado en el puerto " + PUERTO);

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("🔹 Nuevo jugador conectado: " + cliente.getInetAddress());
                pool.execute(new ManejadorCliente(cliente));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
