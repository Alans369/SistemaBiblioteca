package Biblioteca.presentacion;

import Biblioteca.persistencia.ConnectionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class user {
    public JPanel panelMain;
    private JLabel eqtiquetausuario;
    private JTextField userfield;
    private JLabel labelpass;
    private JPasswordField passfield;
    private JButton logginbutton;
    private JLabel email;
    private JTextField textField;
    private JLabel statusLabel;

    public user() {
        logginbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userfield.getText();
                String password = new String(passfield.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(panelMain, "Por favor completa todos los campos.");
                    return;
                }

                try {
                    Connection conn = ConnectionManager.getInstance().connect();

                    String sql = "SELECT * FROM Usuarios WHERE NombreUsuario = ? AND Contrasena = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.setString(2, password); // Ideal: en un proyecto real se usa hash

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(panelMain,
                                "¡Inicio de sesión exitoso, " + username + "!",
                                "Acceso concedido",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panelMain,
                                "Usuario o contraseña incorrectos.",
                                "Acceso denegado",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panelMain,
                            "Error al conectar con la base de datos: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Inicio de sesión");
            frame.setContentPane(new user().panelMain);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 250);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
