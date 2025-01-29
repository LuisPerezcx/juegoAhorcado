package cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class UIAhorcado {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private int intentosRestantes;

    private JFrame ventana;
    private JTextField campoLetra;
    private JButton btnEnviar;
    private JTextArea areaMensajes;
    private JPanel panelDibujo;

    public UIAhorcado() {
        try {
            // Establecer conexión al servidor
            socket = new Socket(HOST, PUERTO);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            intentosRestantes = 6; // Número inicial de intentos.

            // Crear la interfaz gráfica
            ventana = new JFrame("Ahorcado");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setSize(500, 400);  // Aumenta el tamaño de la ventana
            ventana.setLayout(new BorderLayout());

            // Panel para mostrar el dibujo del ahorcado
            panelDibujo = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    dibujarAhorcado(g);
                }
            };
            panelDibujo.setPreferredSize(new Dimension(200, 300));  // Ajuste del tamaño del panel
            ventana.add(panelDibujo, BorderLayout.WEST);

            areaMensajes = new JTextArea(10, 30);
            areaMensajes.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(areaMensajes);
            ventana.add(scrollPane, BorderLayout.CENTER);

            JPanel panel = new JPanel();
            campoLetra = new JTextField(2);
            btnEnviar = new JButton("Enviar Letra");
            panel.add(campoLetra);
            panel.add(btnEnviar);
            ventana.add(panel, BorderLayout.SOUTH);

            // Configurar evento del botón
            btnEnviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String letra = campoLetra.getText().trim();
                    if (!letra.isEmpty()) {
                        enviarLetra(letra);
                        campoLetra.setText("");  // Limpiar el campo
                    }
                }
            });

            // Hilo para escuchar respuestas del servidor
            Thread escucharRespuestas = new Thread(new Runnable() {
                @Override
                public void run() {
                    escucharServidor();
                }
            });
            escucharRespuestas.start();

            // Mostrar ventana
            ventana.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarLetra(String letra) {
        try {
            salida.println(letra);
            areaMensajes.append("Letra enviada: " + letra + "\n");
        } catch (Exception e) {
            areaMensajes.append("Error al enviar la letra: " + e.getMessage() + "\n");
        }
    }

    private void escucharServidor() {
        String mensaje;
        try {
            while ((mensaje = entrada.readLine()) != null) {
                areaMensajes.append(mensaje + "\n");

                // Aquí procesas la solicitud del servidor para pedir la letra
                if (mensaje.startsWith("Ingresa una letra:")) {
                    campoLetra.setEditable(true);  // Permitir la entrada
                    btnEnviar.setEnabled(true);
                }

                // Procesar si el intento falló (ejemplo de recibo del error)
                if (mensaje.contains("fallo")) {
                    intentosRestantes--;
                    panelDibujo.repaint();  // Redibujar la figura con el nuevo número de intentos
                }
            }
        } catch (IOException e) {
            areaMensajes.append("Error de comunicación con el servidor.\n");
        }
    }

    private void dibujarAhorcado(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(30, 150, 150, 150);  // Base
        g.drawLine(75, 20, 75, 150);   // Poste
        g.drawLine(75, 20, 150, 20);   // Soporte
        g.drawLine(150, 20, 150, 40);  // Cuerda

        if (intentosRestantes <= 5) g.drawOval(130, 40, 40, 40);  // Cabeza
        if (intentosRestantes <= 4) g.drawLine(150, 80, 150, 130); // Cuerpo
        if (intentosRestantes <= 3) g.drawLine(150, 95, 120, 120); // Brazo izq
        if (intentosRestantes <= 2) g.drawLine(150, 95, 180, 120); // Brazo der
        if (intentosRestantes <= 1) g.drawLine(150, 130, 120, 160); // Pierna izq
        if (intentosRestantes == 0) g.drawLine(150, 130, 180, 160); // Pierna der
    }

    public static void main(String[] args) {
        new UIAhorcado();
    }
}
