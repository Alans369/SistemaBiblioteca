package Biblioteca.presentacion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class VistaEjemplo extends JFrame {

    private JPanel mainPanel;
    private JButton openPerfilButton;
    private int actualLoggedInUserId; // Variable para guardar el ID del usuario logueado
    private String actualLoggedInUserRole; // ¡NUEVA VARIABLE para guardar el rol del usuario!
    private JButton adminButton; // ¡NUEVO BOTÓN DE EJEMPLO para administradores!

    // Constructor que ahora recibe el ID y el ROL del usuario logueado
    public VistaEjemplo(int loggedInUserId, String loggedInUserRole) { // ¡Constructor modificado!
        this.actualLoggedInUserId = loggedInUserId;
        this.actualLoggedInUserRole = loggedInUserRole; // Guarda el rol recibido

        // Opcional: Muestra el rol en el título de la ventana
        setTitle("Vista de Ejemplo (Usuario ID: " + actualLoggedInUserId + " - Rol: " + actualLoggedInUserRole + ")");
        setSize(400, 250); // Aumentamos un poco el tamaño para el nuevo botón
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents(); // Inicializa los componentes de la interfaz
        add(mainPanel); // Añade el panel principal al JFrame
    }

    private void initComponents() {
        mainPanel = new JPanel();
        // Usamos FlowLayout con espaciado para que los botones no estén pegados
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        openPerfilButton = new JButton("Ir a Perfil del Usuario Logueado");
        adminButton = new JButton("Panel de Administración"); // Crea el botón de administrador

        mainPanel.add(openPerfilButton); // Añade el botón de perfil

        // --- LÓGICA PARA MOSTRAR/OCULTAR EL BOTÓN DE ADMINISTRADOR BASADA EN EL ROL ---
        // Comparamos el rol ignorando mayúsculas/minúsculas para mayor robustez
        if ("Administrador".equalsIgnoreCase(actualLoggedInUserRole)) {
            adminButton.setVisible(true); // Hace visible el botón si es administrador
            mainPanel.add(adminButton); // Añade el botón de administrador al panel
        } else {
            adminButton.setVisible(false); // Asegura que el botón no sea visible si no es administrador
            // No lo añadimos al panel si no es visible para evitar espacio innecesario,
            // pero si se añade siempre, su visibilidad lo controlará.
            // Para FlowLayout es mejor añadirlo condicionalmente o usar un layout más complejo.
            // Para simplicidad, si es visible se añade, si no, se ignora el add.
        }

        // --- Manejadores de Eventos (ActionListeners) ---
        openPerfilButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra la ventana actual de ejemplo
                // Abre la vista Perfil, pasándole el ID real del usuario
                Perfil perfilView = new Perfil(actualLoggedInUserId);
                perfilView.setVisible(true);
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mensaje de ejemplo para el panel de administración
                JOptionPane.showMessageDialog(mainPanel, "Acceso al panel de administración para el rol: " + actualLoggedInUserRole, "Panel de Admin", JOptionPane.INFORMATION_MESSAGE);
                // Aquí podrías abrir una nueva ventana como 'AdminDashboard'
                // AdminDashboard adminView = new AdminDashboard();
                // adminView.setVisible(true);
            }
        });
    }

    // El método main es solo para pruebas directas de VistaEjemplo.
    // Utiliza este para probar VistaEjemplo independientemente del login.
    public static void main(String[] args) {
        // Ejecuta la interfaz gráfica en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // Ejemplo para probar como "Administrador" (ID 1, Rol "Administrador")
            new VistaEjemplo(1, "Administrador").setVisible(true);

            // Ejemplo para probar como "Usuario" (ID 2, Rol "Usuario")
            // new VistaEjemplo(2, "Usuario").setVisible(true);
        });
    }
}