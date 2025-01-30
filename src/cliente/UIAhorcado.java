package cliente;

import servidor.ServidorAhorcado;
import servidor.ServidorListener;

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

public class UIAhorcado extends JFrame implements ServidorListener {
    private JLabel palabraLabel, intentosLabel, letraLabel;
    private JTextField letraInput;
    private JButton enviarBtn;
    private AhorcadoPanel ahorcadoPanel;
    private JPanel topPanel, bottomPanel;

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    private boolean esHost;
    private boolean nuevoJugador = false;
    private String palabra;

    public UIAhorcado(String palabra, boolean esHost, String ipAddress) {
        this.esHost = esHost;
        this.palabra = palabra;

        setTitle("Ahorcado Multijugador 🎮 - " + (esHost ? "Host" : "Jugador"));
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        construirVentana();

        add(topPanel, BorderLayout.NORTH);
        add(ahorcadoPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        if(esHost){
            iniciarServidor(palabra);
        } else {
            enviarBtn.addActionListener(e -> enviarLetra());
            letraInput.addActionListener(e -> enviarLetra());
            conectarCliente(ipAddress);
        }

        setVisible(true);
        setLocationRelativeTo(null);
        if(esHost){
            do{
                JOptionPane.showMessageDialog(null,"Esperando jugador...\nIP: "+obtenerIPLocal());
            }while(!nuevoJugador);
        }
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
        if(esHost) topLabelPnl.add(new JLabel("Palabra: "+palabra));

        topPanel.add(topLabelPnl);

        // Panel Central (Dibujo Ahorcado)
        ahorcadoPanel = new AhorcadoPanel();

        // Panel Inferior (Entrada Usuario)
        bottomPanel = new JPanel();
        letraInput = new JTextField(5);
        enviarBtn = new JButton("Enviar");
        letraLabel = new JLabel("");

        if(esHost){
            letraLabel.setFont(new Font("Arial", Font.BOLD,20));
            letraLabel.setForeground(Color.red);
            bottomPanel.add(letraLabel);
        }else{
            bottomPanel.add(new JLabel("Ingresa una letra:"));
            bottomPanel.add(letraInput);
            bottomPanel.add(enviarBtn);
        }
    }

    private void iniciarServidor(String palabra) {
        ServidorAhorcado servidorAhorcado = new ServidorAhorcado(this);
        servidorAhorcado.iniciarServidor(palabra);
        //aqui debo de darle un valor a salida
    }

    private void conectarCliente(String ipAddress){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, 5000),1000);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = entrada.readLine()) != null) {
                        manejarMensaje(mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (SocketTimeoutException e) {
            JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor en el tiempo establecido.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void manejarMensaje(String mensaje) {
        System.out.println(mensaje);
        SwingUtilities.invokeLater(() -> {
            if(mensaje.contains("Nuevo jugador conectado:")){
                JOptionPane.showMessageDialog(null, "Un nuevo jugador se ha unido al juego.", "Jugador Unido", JOptionPane.INFORMATION_MESSAGE);
                nuevoJugador = true;
            }
            if (mensaje.startsWith("Palabra:")) {
                String mensajeConEspacios = mensaje.substring(9);
                String mensajeConEspaciosSeparados = mensajeConEspacios.replaceAll("(.)", "$1 ");
                palabraLabel.setText(mensajeConEspaciosSeparados.trim());
            } else if (mensaje.contains("Intentos ")) {
                int intentosRestantes =  Integer.parseInt(mensaje.substring(20));
                intentosLabel.setText("Intentos restantes: " + intentosRestantes);
                ahorcadoPanel.actualizarDibujo(intentosRestantes);
            } else if (mensaje.startsWith("Letra: ")){
                String letra = mensaje.substring(7);
                letraLabel.setText("Ultima letra intentada: " + letra);
            } else if (mensaje.contains("¡Felicidades! Has adivinado la palabra:") && !esHost){
                if(esHost) JOptionPane.showMessageDialog(null,"El jugador adivino la palabra","Perdiste!",JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(null,mensaje,"Ganaste!",JOptionPane.OK_OPTION);
            } else if(mensaje.contains("¡Lo siento! Has perdido. La palabra era:")){
                if(esHost) JOptionPane.showMessageDialog(null,"El jugador no adivino la palabra","Ganaste!",JOptionPane.OK_OPTION);
                JOptionPane.showMessageDialog(null,mensaje,"Perdiste :(",JOptionPane.ERROR_MESSAGE);
            } else if (mensaje.contains("Juego terminado")) {
                JOptionPane.showMessageDialog(null, mensaje, "Juego terminado", JOptionPane.INFORMATION_MESSAGE);
                int juegoNuevo = JOptionPane.showConfirmDialog(this, "Deseas jugar de nuevo?");
                if (juegoNuevo == JOptionPane.YES_OPTION) {
                    this.dispose();
                    new UIPrincipal();
                } else if (juegoNuevo == JOptionPane.NO_OPTION){
                    System.exit(0);
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

    @Override
    public void onMensajeRecibido(String mensaje) {
        System.out.println(mensaje);
        manejarMensaje(mensaje);
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