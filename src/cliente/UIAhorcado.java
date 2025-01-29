package cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class UIAhorcado extends JFrame {
    private JLabel palabraLabel;
    private JLabel intentosLabel;
    private JTextField letraInput;
    private JButton enviarBtn;
    private JTextArea logArea;
    private AhorcadoPanel ahorcadoPanel;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    public UIAhorcado() {
        setTitle("Ahorcado Multijugador 🎮");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Superior (Palabra + Intentos)
        JPanel topPanel = new JPanel();
        palabraLabel = new JLabel("_ _ _ _ _");
        palabraLabel.setFont(new Font("Arial", Font.BOLD, 24));
        intentosLabel = new JLabel("Intentos restantes: 6");
        topPanel.add(palabraLabel);
        topPanel.add(intentosLabel);

        // Panel Central (Dibujo Ahorcado)
        ahorcadoPanel = new AhorcadoPanel();

        // Panel Inferior (Entrada Usuario)
        JPanel bottomPanel = new JPanel();
        letraInput = new JTextField(5);
        enviarBtn = new JButton("Enviar");
        bottomPanel.add(new JLabel("Ingresa una letra:"));
        bottomPanel.add(letraInput);
        bottomPanel.add(enviarBtn);

        // Área de Log
        logArea = new JTextArea(6, 40);
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);

        add(topPanel, BorderLayout.NORTH);
        add(ahorcadoPanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Conectar con el Servidor
        conectarServidor();

        // Evento del Botón
        enviarBtn.addActionListener(e -> enviarLetra());
    }

    private void conectarServidor() {
        try {
            socket = new Socket("localhost", 5000);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = entrada.readLine()) != null) {
                        manejarMensaje(mensaje);
                    }
                } catch (IOException e) {
                    logArea.append("❌ Error de conexión\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manejarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            if (mensaje.startsWith("Palabra:")) {
                palabraLabel.setText(mensaje.substring(9));
            } else if (mensaje.startsWith("Intentos:")) {
                intentosLabel.setText("Intentos restantes: " + mensaje.substring(9));
                ahorcadoPanel.actualizarDibujo(Integer.parseInt(mensaje.substring(9)));
            } else if (mensaje.startsWith("Log:")) {
                logArea.append(mensaje.substring(5) + "\n");
            } else if (mensaje.startsWith("FIN")) {
                JOptionPane.showMessageDialog(this, mensaje.substring(4), "Juego terminado", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void enviarLetra() {
        String letra = letraInput.getText().trim();
        if (!letra.isEmpty() && letra.length() == 1) {
            salida.println(letra);
            letraInput.setText("");
        }
    }

    class AhorcadoPanel extends JPanel {
        private int intentosRestantes = 6;

        public void actualizarDibujo(int intentos) {
            this.intentosRestantes = intentos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawLine(50, 250, 200, 250); // Base
            g.drawLine(125, 50, 125, 250); // Poste
            g.drawLine(125, 50, 200, 50);  // Soporte
            g.drawLine(200, 50, 200, 75);  // Cuerda

            if (intentosRestantes <= 5) g.drawOval(175, 75, 50, 50); // Cabeza
            if (intentosRestantes <= 4) g.drawLine(200, 125, 200, 180); // Cuerpo
            if (intentosRestantes <= 3) g.drawLine(200, 140, 170, 170); // Brazo izq
            if (intentosRestantes <= 2) g.drawLine(200, 140, 230, 170); // Brazo der
            if (intentosRestantes <= 1) g.drawLine(200, 180, 170, 220); // Pierna izq
            if (intentosRestantes == 0) g.drawLine(200, 180, 230, 220); // Pierna der
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UIAhorcado().setVisible(true));
    }
}