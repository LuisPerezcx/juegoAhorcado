package cliente;

import servidor.ServidorAhorcado;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

import static java.lang.System.exit;

public class UIAhorcado extends JFrame {
    private JLabel palabraLabel, intentosLabel;
    private JTextField letraInput;
    private JButton enviarBtn;
    private AhorcadoPanel ahorcadoPanel;
    private JPanel topPanel, bottomPanel;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    private boolean esHost;

    public UIAhorcado(String palabra, boolean esHost, String ipAddress) {
        this.esHost = esHost;


        setTitle("Ahorcado Multijugador 🎮 - " + (esHost ? "Host" : "Jugador"));
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        construirVentana();

        add(topPanel, BorderLayout.NORTH);
        add(ahorcadoPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        if(esHost){
            bottomPanel.setVisible(false);
            iniciarServidor(palabra);
        } else {
            enviarBtn.addActionListener(e -> enviarLetra());
            conectarCliente(ipAddress);
        }

        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void construirVentana(){
        // Panel Superior (Palabra + Intentos)
        topPanel = new JPanel();
        JPanel topLabelPnl = new JPanel();
        topLabelPnl.setLayout(new BoxLayout(topLabelPnl, BoxLayout.Y_AXIS));

        palabraLabel = new JLabel("_ _ _ _ _");
        palabraLabel.setFont(new Font("Arial", Font.BOLD, 24));
        intentosLabel = new JLabel("Intentos restantes: 6");

        topLabelPnl.add(palabraLabel);
        topLabelPnl.add(Box.createVerticalStrut(15));
        topLabelPnl.add(intentosLabel);

        topPanel.add(topLabelPnl);

        // Panel Central (Dibujo Ahorcado)
        ahorcadoPanel = new AhorcadoPanel();

        // Panel Inferior (Entrada Usuario)
        bottomPanel = new JPanel();
        letraInput = new JTextField(5);
        enviarBtn = new JButton("Enviar");

        bottomPanel.add(new JLabel("Ingresa una letra:"));
        bottomPanel.add(letraInput);
        bottomPanel.add(enviarBtn);
    }

    private void iniciarServidor(String palabra) {
        ServidorAhorcado servidorAhorcado = new ServidorAhorcado();
        servidorAhorcado.iniciarServidor(palabra);
    }

    private void conectarCliente(String ipAddress){
        ClienteAhorcado clienteAhorcado = new ClienteAhorcado(ipAddress);
        new Thread(() -> {
            try {
                String mensaje;
                // Simulamos la escucha de mensajes del servidor.
                while ((mensaje = clienteAhorcado.entradaServidor.readLine()) != null) {
                    // Procesamos el mensaje recibido, dependiendo del tipo.
                    if (mensaje.contains("Palabra")) {
                        //actualizarPalabra(mensaje);
                    } else if (mensaje.contains("Intentos restantes")) {
                        //actualizarIntentos(mensaje);
                    } else if (mensaje.contains("Juego terminado")) {
                        //finDelJuego(mensaje);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }



    private void manejarMensaje(String mensaje) {
        System.out.println(mensaje);
        SwingUtilities.invokeLater(() -> {
            if(mensaje.contains("Nuevo jugador conectado:")){
                JOptionPane.showMessageDialog(null, "Un nuevo jugador se ha unido al juego.", "Jugador Unido", JOptionPane.INFORMATION_MESSAGE);
            }
            if (mensaje.startsWith("Palabra:")) {
                String mensajeConEspacios = mensaje.substring(9);
                String mensajeConEspaciosSeparados = mensajeConEspacios.replaceAll("(.)", "$1 ");
                palabraLabel.setText(mensajeConEspaciosSeparados.trim());
            } else if (mensaje.contains("Intentos ")) {
                int intentosRestantes =  Integer.parseInt(mensaje.substring(20));
                intentosLabel.setText("Intentos restantes: " + intentosRestantes);
                ahorcadoPanel.actualizarDibujo(intentosRestantes);
            } else if (mensaje.contains("Juego terminado")) {
                JOptionPane.showMessageDialog(null, mensaje, "Juego terminado", JOptionPane.INFORMATION_MESSAGE);
                try {
                    socket.close(); // Cerrar conexión
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void enviarLetra() {
        String letra = letraInput.getText().trim();
        if (letra.length() == 1 && Character.isLetter(letra.charAt(0))) {
            salida.println(letra);
            letraInput.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Por favor ingresa solo una letra.", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
        }
    }

    static class AhorcadoPanel extends JPanel {
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
    public static String obtenerIPLocal() {
        try {
            // Obtener todas las interfaces de red disponibles
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Obtener las direcciones IP asociadas a la interfaz
                Enumeration<InetAddress> direcciones = networkInterface.getInetAddresses();
                while (direcciones.hasMoreElements()) {
                    InetAddress direccion = direcciones.nextElement();

                    // Filtramos solo las direcciones IPv4 no de loopback
                    if (direccion instanceof Inet4Address && !direccion.isLoopbackAddress()) {
                        return direccion.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "No se pudo obtener la IP de la red local";
    }
}