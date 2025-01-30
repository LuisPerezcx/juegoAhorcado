package cliente;

import servidor.ManejadorServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static servidor.ServidorAhorcado.pool;

public class ClienteAhorcado {
    private static final int PUERTO = 5000;
    private Socket socket;
    BufferedReader entradaServidor;
    private PrintWriter salidaCliente;

    public ClienteAhorcado(String ipServidor){
        try {
            // Conectar al servidor usando su IP y puerto
            socket = new Socket(ipServidor, PUERTO);
            System.out.println("Conectado al servidor en " + ipServidor + ":" + PUERTO);

            // Crear flujo de entrada y salida para la comunicación
            entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salidaCliente = new PrintWriter(socket.getOutputStream(), true);

            // Crear un hilo para leer mensajes del servidor
            new Thread(() -> {
                try {
                    String mensajeServidor;
                    while ((mensajeServidor = entradaServidor.readLine()) != null) {
                        pool.execute(new ManejadorServidor(mensajeServidor));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
