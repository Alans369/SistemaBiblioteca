package Biblioteca.presentacion;

// Importa tu UserDAO si aún lo estás usando
import Biblioteca.persistencia.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map; // Para el tipo de retorno de UserDAO
import java.util.Objects;

public class user {
    public JPanel panelMain;
    private JLabel etiquetausuario;
    public JTextField userfield;
    private JLabel labelpass;
    private JPasswordField passfield;
    private JButton logginbutton;
    private JLabel email; // Se mantiene por si lo usas en el .form, no en la lógica de login
    private JTextField textField; // Se mantiene por si lo usas en el .form, no en la lógica de login
    private JButton registerButton; // ¡Este es el botón de registro!

    // Instancia del UserDAO (el que devuelve Map<String, Object>)
    private UserDAO UserDAO; // ¡Usa UserDAO si este es el que estás utilizando!

    public user() {
        // Inicializamos el UserDAO
        this.UserDAO = new UserDAO();

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

                Map<String, Object> usuarioLogueadoData = null; // Para almacenar los datos del usuario como un Map

                try {
                    // Llama al método del DAO para obtener el usuario por su nombre de usuario
                    usuarioLogueadoData = UserDAO.obtenerUsuarioPorNombre(username);

                } catch (Exception ex) { // Capturamos cualquier excepción que pueda surgir
                    JOptionPane.showMessageDialog(panelMain,
                            "Error al buscar usuario en la base de datos: " + ex.getMessage(),
                            "Error de Base de Datos",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return;
                }

                String hashedPasswordFromDB = null;
                // Verificamos si se encontró un usuario antes de intentar obtener la contraseña
                if (usuarioLogueadoData != null) {
                    hashedPasswordFromDB = (String) usuarioLogueadoData.get("Contrasena");
                }

                // --- Lógica de Verificación de Contraseña y Redirección a VistaEjemplo ---
                // Verifica que se encontró un usuario y que la contraseña hash no es nula,
                // y luego compara las contraseñas.
                if (usuarioLogueadoData != null && hashedPasswordFromDB != null && BCrypt.checkpw(enteredPassword, hashedPasswordFromDB)) {


                    // Cierra la ventana actual de login
                    ((JFrame) SwingUtilities.getWindowAncestor(panelMain)).dispose();

                    // ¡NUEVAS VERIFICACIONES DE NULIDAD AQUÍ!
                    Integer loggedInUserId = (Integer) usuarioLogueadoData.get("UsuarioID");
                    String loggedInUserRole = (String) usuarioLogueadoData.get("Rol");

                    if (loggedInUserId == null || loggedInUserRole == null) {
                        JOptionPane.showMessageDialog(panelMain,
                                "Error: Datos de usuario incompletos (ID o Rol nulo). Contacte al administrador.",
                                "Error de Datos",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }


                    if (Objects.equals(userfield.getText(), "admin")){

                        System.out.println(username);
                        new VistaAdmin().setVisible(true);


                    }else {
                        new VistaDeUsuario().setVisible(true);

                    }








                } else {
                    JOptionPane.showMessageDialog(panelMain,
                            "Usuario o contraseña incorrectos.",
                            "Acceso Denegado",
                            JOptionPane.ERROR_MESSAGE);
                }


            }
        });

        // --- Lógica del botón de REGISTRO (registerButton) ---
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la ventana actual de login
                ((JFrame) SwingUtilities.getWindowAncestor(panelMain)).dispose();

                // Abre la nueva ventana de registro (user_registration)
                SwingUtilities.invokeLater(() -> {
                    JFrame registrationFrame = new JFrame("Registro de Usuario");
                    // Asegúrate de que user_registration tenga un panel principal o sea un JFrame directamente
                    // Si user_registration es un JPanel:
                    user_registration registrationPanel = new user_registration();
                    registrationFrame.setContentPane(registrationPanel.panelMain); // Ajusta si el panel se llama diferente

                    // Si user_registration es un JFrame directamente (es decir, extiende JFrame):
                    // user_registration registrationFrame = new user_registration();

                    registrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana
                    registrationFrame.setSize(500, 400); // Ajusta el tamaño según tu formulario de registro
                    registrationFrame.setLocationRelativeTo(null); // Centra la ventana
                    registrationFrame.setVisible(true);
                });
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