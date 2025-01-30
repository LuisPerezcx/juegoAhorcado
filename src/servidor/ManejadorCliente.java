package servidor;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private Socket cliente;
    private String palabraSecreta;
    private PrintWriter salidaCliente;

    public ManejadorCliente(Socket cliente, String palabraSecreta, PrintWriter salidaCliente) {
        this.cliente = cliente;
        this.palabraSecreta = palabraSecreta;
        this.salidaCliente = salidaCliente;
    }


    @Override
    public void run() {
        try {
            BufferedReader entradaCliente = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            salidaCliente.println("¡Bienvenido al juego del ahorcado!");

            while (true) {
                // Leer la letra del cliente
                String letra = entradaCliente.readLine();
                if (letra == null || letra.length() != 1) {
                    break;
                }

                // Lógica del juego (verificar la letra)
                if (palabraSecreta.contains(letra)) {
                    salidaCliente.println("Correcto! La letra " + letra + " está en la palabra.");
                } else {
                    salidaCliente.println("Incorrecto! La letra " + letra + " no está en la palabra.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
