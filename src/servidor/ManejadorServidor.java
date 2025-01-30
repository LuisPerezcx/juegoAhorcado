package servidor;

import java.util.concurrent.Callable;

public class ManejadorServidor implements Runnable {
    private String mensajeServidor;

    public ManejadorServidor(String mensaje) {
        this.mensajeServidor = mensaje;
    }

    @Override
    public void run() {
        // Aquí manejas el mensaje recibido
        // Puedes hacer algo como actualizar la UI o realizar otra acción dependiendo del mensaje
        if (mensajeServidor.startsWith("Palabra:")) {
            // Lógica para manejar la palabra
        } else if (mensajeServidor.contains("Intentos restantes")) {
            // Lógica para actualizar los intentos
        } else if (mensajeServidor.contains("Juego terminado")) {
            // Lógica para finalizar el juego
        }
    }
}
