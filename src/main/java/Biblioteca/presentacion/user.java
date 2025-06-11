package Biblioteca.presentacion;

import Biblioteca.persistencia.ConnectionManager;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class user {
    public JPanel panelMain;
    private JLabel etiquetausuario;
    private JTextField userfield;
    private JLabel labelpass;
    private JPasswordField passfield;
    private JButton logginbutton;
    private JLabel email; // Se mantiene por si lo usas en el .form, no en la lógica de login
    private JTextField textField; // Se mantiene por si lo usas en el .form, no en la lógica de login

    public user() {
        // --- Lógica del botón de LOGIN (logginbutton) ---
        logginbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userfield.getText();
                String enteredPassword = new String(passfield.getPassword());

                if (username.isEmpty() || enteredPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(panelMain, "Por favor completa todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null;
                int loggedInUserId = -1; // Para almacenar el ID del usuario logueado
                String hashedPasswordFromDB = null;

                try {
                    conn = ConnectionManager.getInstance().connect();

                    // Consulta SQL: SELECCIONA UsuarioID y Contrasena de la tabla Usuarios
                    String sql = "SELECT UsuarioID, Contrasena FROM Usuarios WHERE NombreUsuario = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, username);
                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        loggedInUserId = rs.getInt("UsuarioID"); // Obtiene el UsuarioID
                        hashedPasswordFromDB = rs.getString("Contrasena");
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panelMain,
                            "Error al conectar con la base de datos o al ejecutar la consulta: " + ex.getMessage(),
                            "Error de Base de Datos",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Imprime la traza completa del error en la consola
                    return;
                } finally {
                    // Asegura que los recursos de la base de datos se cierren
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException ex) {
                        System.err.println("Error al cerrar los recursos de la base de datos: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

                // --- Lógica de Verificación de Contraseña y Redirección a VistaEjemplo ---
                if (hashedPasswordFromDB != null && BCrypt.checkpw(enteredPassword, hashedPasswordFromDB)) {
                    JOptionPane.showMessageDialog(panelMain,
                            "¡Inicio de sesión exitoso, " + username + "!",
                            "Acceso Concedido",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Cierra la ventana actual de login
                    ((JFrame) SwingUtilities.getWindowAncestor(panelMain)).dispose();

                    // ¡REDirecciona a la VISTA DE EJEMPLO, pasando el ID real del usuario!
                    VistaEjemplo exampleView = new VistaEjemplo(loggedInUserId);
                    exampleView.setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(panelMain,
                            "Usuario o contraseña incorrectos.",
                            "Acceso Denegado",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        // Ejecuta la interfaz gráfica en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login de Usuario");
            frame.setContentPane(new user().panelMain);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 250);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}