package Biblioteca.presentacion;

import Biblioteca.persistencia.ConnectionManager;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

                if (!email.contains("@")) {
                    JOptionPane.showMessageDialog(panelMain, "Por favor ingresa un correo válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "INSERT INTO Usuarios (NombreUsuario, Contrasena, CorreoElectronico) VALUES (?, ?, ?)";

                try (Connection conn = ConnectionManager.getInstance().connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, username);
                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                    stmt.setString(2, hashedPassword);
                    stmt.setString(3, email);

                    int rowsInserted = stmt.executeUpdate();

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(panelMain, "Registro exitoso, bienvenido " + username + "!");
                        userField.setText("");
                        emailField.setText("");
                        passfield.setText("");
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panelMain, "Error al registrar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
