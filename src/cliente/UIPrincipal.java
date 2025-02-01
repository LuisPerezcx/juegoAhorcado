package cliente;

import servidor.ServidorAhorcado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIPrincipal extends JFrame {
    private JPanel ventana;
    public JButton btnCrear,btnUnirse;


    public UIPrincipal(){
        setTitle("Ahorcado Multijugador 🎮");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        construirVentana();
        addListeners();
        setVisible(true);
    }

    private void construirVentana() {
        // Panel Principal con imagen de fondo
        ventana = new FondoPanel();
        ventana.setLayout(new BorderLayout());
        add(ventana, BorderLayout.CENTER);

        JPanel pnlContenido = new JPanel();
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));
        pnlContenido.setOpaque(false);
        pnlContenido.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Margen interno


        JLabel lblTitulo = new JLabel("¡Bienvenido al Ahorcado Multijugador!");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.BLACK);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel lblInstrucciones = new JLabel("Selecciona una opción para jugar");
        lblInstrucciones.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInstrucciones.setForeground(Color.black);
        lblInstrucciones.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel pnlBtns = new JPanel();
        pnlBtns.setLayout(new FlowLayout());
        pnlBtns.setOpaque(false);

        btnCrear = new JButton("Crear juego");
        btnUnirse = new JButton("Unirse a juego");

        pnlBtns.add(btnCrear);
        pnlBtns.add(btnUnirse);


        pnlContenido.add(Box.createVerticalStrut(100));
        pnlContenido.add(lblTitulo);
        pnlContenido.add(Box.createVerticalStrut(10));
        pnlContenido.add(lblInstrucciones);
        pnlContenido.add(Box.createVerticalStrut(20));
        pnlContenido.add(pnlBtns);


        ventana.add(pnlContenido, BorderLayout.EAST);
    }

    private void addListeners(){
        btnCrear.addActionListener(new ControllerBtns(this));
        btnUnirse.addActionListener(new ControllerBtns(this));
    }

}

class FondoPanel extends JPanel {
    private Image imagen;

    public FondoPanel() {
        imagen = new ImageIcon("src/assets/ahorcado2.png").getImage(); // Ruta de la imagen
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this); // Dibuja la imagen escalada
    }
}

class ControllerBtns implements ActionListener{
    private final UIPrincipal ui;

    public ControllerBtns(UIPrincipal ui) {
        this.ui = ui;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object e = actionEvent.getSource();
        if(e == ui.btnCrear){
            System.out.println("crear juego!");
            String palabra = JOptionPane.showInputDialog("ingresa una palabra para jugar",null);
            int maximoLetras = 15;
            if(palabra != null && !palabra.isEmpty()){
                if (palabra.split("\\s+").length > 1) {
                    JOptionPane.showMessageDialog(null, "Solo puedes ingresar una palabra.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (palabra.length() > maximoLetras) {
                    JOptionPane.showMessageDialog(null,"La palabra no puede tener más de " + maximoLetras + " caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
                }else if (palabra.matches("^[A-Za-z\\s]+$")) {
                    ui.dispose();
                    new UIAhorcado(palabra, true, null);
                }else {
                    JOptionPane.showMessageDialog(null, "La palabra solo puede contener letras, números y caracteres especiales válidos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null, "Debes ingresar una palabra válida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if(e == ui.btnUnirse){
            String ipAdress = JOptionPane.showInputDialog("Ingresa la ip del host",null);


            if(ipAdress != null && !ipAdress.trim().isEmpty()){
                ipAdress = ipAdress.trim();
                ui.dispose();
                new UIAhorcado(null,false, ipAdress);
            }else {
                System.out.println("El usuario cancelo.");

            }
            System.out.println("unirse a juego!");
        }
    }

    public static void main(String[] args) {
        new UIPrincipal();
    }
}