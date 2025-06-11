package Biblioteca.presentacion;

import Biblioteca.persistencia.ConnectionManager;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class user_registration {
    private JLabel email;
    private JLabel usuario;
    private JTextField userField;       // Antes textField1
    private JTextField emailField;      // Antes textField2
    private JPasswordField passfield;
    private JButton register;
    public JPanel panelMain;

    public user_registration() {
        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passfield.getPassword());

                if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(panelMain, "Por favor completa todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validación básica de formato de correo electrónico
                if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    JOptionPane.showMessageDialog(panelMain, "Por favor ingresa un correo electrónico válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Asumimos un rol predeterminado para nuevos registros, por ejemplo, "usuario"
                String defaultRole = "usuario"; // O "cliente", o el rol que desees asignar por defecto

                // Sentencia SQL para insertar el usuario y obtener el ID generado
                // Para SQL Server, se usa INSERT ... OUTPUT INSERTED.UsuarioID
                String sql = "INSERT INTO Usuarios (NombreUsuario, Contrasena, CorreoElectronico, Rol) " +
                        "OUTPUT INSERTED.UsuarioID " + // Obtener el ID generado inmediatamente
                        "VALUES (?, ?, ?, ?)";

                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null; // Para el resultado de OUTPUT INSERTED.UsuarioID

                try {
                    conn = ConnectionManager.getInstance().connect();
                    stmt = conn.prepareStatement(sql);

                    stmt.setString(1, username);
                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                    stmt.setString(2, hashedPassword);
                    stmt.setString(3, email);
                    stmt.setString(4, defaultRole); // Asigna el rol predeterminado

                    // Usar executeQuery() porque OUTPUT INSERTED.UsuarioID devuelve un ResultSet
                    rs = stmt.executeQuery();

                    int newUserId = -1; // Valor predeterminado para el ID
                    if (rs.next()) {
                        newUserId = rs.getInt(1); // Obtener el primer (y único) valor del ResultSet, que es UsuarioID
                    }

                    if (newUserId != -1) {
                        JOptionPane.showMessageDialog(panelMain, "Registro exitoso, bienvenido " + username + "!");

                        // Cierra la ventana actual de registro
                        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(panelMain);
                        if (currentFrame != null) {
                            currentFrame.dispose();
                        }

                        // --- CAMBIOS PARA SOLUCIONAR EL ERROR DE LAMBDA AQUÍ ---
                        // Captura el valor final de newUserId y defaultRole en variables final/effectively final
                        final int finalNewUserId = newUserId;
                        final String finalDefaultRole = defaultRole;

                        // Abre la VistaEjemplo pasando el ID del nuevo usuario y su rol predeterminado
                        SwingUtilities.invokeLater(() -> {
                            // Ahora usamos las variables finalNewUserId y finalDefaultRole
                            VistaEjemplo exampleView = new VistaEjemplo(finalNewUserId, finalDefaultRole);
                            exampleView.setVisible(true);
                        });
                    } else {
                        // Esto debería ser raro si la inserción fue exitosa pero no se pudo obtener el ID
                        JOptionPane.showMessageDialog(panelMain, "Error al obtener ID de usuario después del registro.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    // Manejo específico si el nombre de usuario ya existe (ej. UNIQUE constraint)
                    if (ex.getMessage().contains("UNIQUE KEY constraint") || ex.getMessage().contains("duplicate key")) {
                        JOptionPane.showMessageDialog(panelMain, "El nombre de usuario o correo electrónico ya existe.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panelMain, "Error al registrar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    ex.printStackTrace(); // Para depuración
                } finally {
                    // Cerrar recursos en el bloque finally
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (conn != null) conn.close();
                    } catch (SQLException closeEx) {
                        System.err.println("Error al cerrar recursos: " + closeEx.getMessage());
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Registro de Usuario");
            user_registration registro = new user_registration();
            frame.setContentPane(registro.panelMain);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}