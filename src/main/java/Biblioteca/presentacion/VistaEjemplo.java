package Biblioteca.presentacion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class VistaEjemplo extends JFrame {

    private JPanel mainPanel;
    private JButton openPerfilButton;
    private int actualLoggedInUserId; // Variable para guardar el ID del usuario logueado

    // Constructor que recibe el ID del usuario logueado desde la vista de login
    public VistaEjemplo(int loggedInUserId) {
        this.actualLoggedInUserId = loggedInUserId; // Guarda el ID recibido

        setTitle("Vista de Ejemplo (Usuario ID: " + actualLoggedInUserId + ")"); // Opcional: muestra el ID en el título
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar esta ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        initComponents(); // Inicializa los componentes de la interfaz
        add(mainPanel); // Añade el panel principal al JFrame
    }

    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Un diseño simple para centrar el botón

        openPerfilButton = new JButton("Ir a Perfil del Usuario Logueado");

        mainPanel.add(openPerfilButton); // Añade el botón al panel

        // Añadir el ActionListener al botón
        openPerfilButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Al hacer clic en el botón, redirigimos a la vista Perfil.
                // ¡Se usa el ID del usuario REALMENTE logueado que se recibió en el constructor!

                dispose(); // Cierra la ventana actual de ejemplo

                // Abre la vista Perfil, pasándole el ID real del usuario
                Perfil perfilView = new Perfil(actualLoggedInUserId);
                perfilView.setVisible(true);
            }
        });
    }

    // El método main es solo para pruebas directas de VistaEjemplo.
    // Si la aplicación siempre inicia desde la ventana de login, este main no se usa.
    public static void main(String[] args) {
        // Ejecuta la interfaz gráfica en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // Para probar VistaEjemplo de forma independiente, pasamos un ID de prueba (ej. 1 o 2)
            new VistaEjemplo(1).setVisible(true);
        });
    }
}