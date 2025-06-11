package Biblioteca.presentacion;

import Biblioteca.persistencia.ConnectionManager;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Perfil extends JFrame {
    private JPanel Panel; // Este es tu panel principal, nombrado 'Panel'
    private JTextField usernameField;
    private JTextField email; // Campo para el correo electrónico
    private JPasswordField Pass; // Campo para la nueva contraseña
    private JPasswordField confirmPasswordField;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton; // ¡Renombrado a deleteButton para mayor claridad y evitar conflictos!

    // Variables para almacenar los datos originales del usuario de la base de datos
    private int UserId; // ID del usuario que estamos modificando
    private String originalEmail; // Para guardar el email original de la DB (necesario para la validación)
    private String originalHashedPassword; // Para guardar el hash de la contraseña original de la DB

    public Perfil(int userId) {
        this.UserId = userId;

        setTitle("Modificar Perfil de Usuario");
        setSize(450, 450); // Ajustamos el tamaño del formulario para el nuevo botón
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        initComponents(); // Inicializa y configura los componentes de la interfaz
        loadUserData(); // Carga los datos actuales del usuario desde la base de datos

        add(Panel); // Agrega el panel principal al JFrame
    }

    private void initComponents() {
        Panel = new JPanel(); // Inicializa el panel principal
        Panel.setLayout(new GridBagLayout()); // Usa GridBagLayout para un diseño flexible
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Los componentes se expanden horizontalmente

        // Inicialización de los componentes de la interfaz de usuario
        usernameField = new JTextField(25);
        email = new JTextField(25);
        Pass = new JPasswordField(25);
        confirmPasswordField = new JPasswordField(25);
        saveButton = new JButton("Guardar Cambios");
        cancelButton = new JButton("Cancelar");
        deleteButton = new JButton("Eliminar Cuenta"); // ¡Inicializa el nuevo botón!
        deleteButton.setBackground(new Color(220, 50, 50)); // Un color rojo para el botón de eliminar
        deleteButton.setForeground(Color.WHITE); // Texto blanco para el botón

        // --- Añadir componentes al panel con GridBagLayout ---

        // Campo "Nuevo Nombre de Usuario"
        gbc.gridx = 0; gbc.gridy = 0; // Columna 0, Fila 0
        Panel.add(new JLabel("Nuevo Nombre de Usuario:"), gbc); // Etiqueta
        gbc.gridx = 1; gbc.gridy = 0; // Columna 1, Fila 0
        Panel.add(usernameField, gbc); // Campo de texto para el nombre de usuario

        // Campo "Correo Electrónico"
        gbc.gridx = 0; gbc.gridy = 1; // Columna 0, Fila 1
        Panel.add(new JLabel("Correo Electrónico (requerido para cambios):"), gbc); // Etiqueta
        gbc.gridx = 1; gbc.gridy = 1; // Columna 1, Fila 1
        Panel.add(email, gbc); // Campo de texto para el correo electrónico

        // Campo "Nueva Contraseña"
        gbc.gridx = 0; gbc.gridy = 2; // Columna 0, Fila 2
        Panel.add(new JLabel("Nueva Contraseña:"), gbc); // Etiqueta
        gbc.gridx = 1; gbc.gridy = 2; // Columna 1, Fila 2
        Panel.add(Pass, gbc); // Campo de contraseña para la nueva contraseña

        // Campo "Confirmar Nueva Contraseña"
        gbc.gridx = 0; gbc.gridy = 3; // Columna 0, Fila 3
        Panel.add(new JLabel("Confirmar Nueva Contraseña:"), gbc); // Etiqueta
        gbc.gridx = 1; gbc.gridy = 3; // Columna 1, Fila 3
        Panel.add(confirmPasswordField, gbc); // Campo de contraseña para confirmar

        // Botones "Cancelar" y "Guardar Cambios"
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; // Columna 0, Fila 4, ocupa 1 columna
        Panel.add(cancelButton, gbc); // Botón Cancelar
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1; // Columna 1, Fila 4, ocupa 1 columna
        Panel.add(saveButton, gbc); // Botón Guardar Cambios

        // ¡Botón "Eliminar Cuenta" - Nueva Fila!
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; // Columna 0, Fila 5, ocupa 2 columnas
        gbc.anchor = GridBagConstraints.CENTER; // Centrar el botón en la celda
        Panel.add(deleteButton, gbc); // Añade el botón de eliminar

        // --- Manejadores de Eventos (Listeners) para los botones ---
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges(); // Llama al método para guardar los cambios
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra la ventana del formulario
                // Opcional: Podrías volver a la VistaEjemplo si lo deseas
                // VistaEjemplo exampleView = new VistaEjemplo(userId, /*Aquí necesitarías el rol del usuario*/);
                // exampleView.setVisible(true);
            }
        });

        // --- Manejador de Eventos para el botón ELIMINAR ---
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmResult = JOptionPane.showConfirmDialog(
                        Panel,
                        "¿Está seguro de que desea eliminar su cuenta? Esta acción es irreversible.",
                        "Confirmar Eliminación de Cuenta",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirmResult == JOptionPane.YES_OPTION) {
                    deleteUserAccount(); // Llama al método para eliminar la cuenta
                }
            }
        });
    }

    private void loadUserData() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getInstance().connect(); // Establece conexión con la base de datos
            // Recuperamos Nombre de Usuario, CorreoElectronico y el HASH de la Contraseña
            String sql = "SELECT NombreUsuario, CorreoElectronico, Contrasena FROM Usuarios WHERE UsuarioID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserId); // Establece el ID del usuario para la consulta
            rs = stmt.executeQuery(); // Ejecuta la consulta

            if (rs.next()) { // Si se encuentra el usuario
                // Para guardar el nombre de usuario original
                String originalUsername = rs.getString("NombreUsuario");
                originalEmail = rs.getString("CorreoElectronico"); // Guarda el email original (para la validación posterior)
                originalHashedPassword = rs.getString("Contrasena");

                usernameField.setText(originalUsername); // Muestra el nombre de usuario actual
                // ¡LÍNEA COMENTADA/ELIMINADA! email.setText(originalEmail); // NO MUESTRES el correo en el campo
                // El campo 'email' se mantiene vacío para que el usuario lo ingrese.
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron datos del usuario.", "Error de Carga", JOptionPane.ERROR_MESSAGE);
                dispose(); // Cierra la ventana si el usuario no se encuentra
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del usuario: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime la pila de errores para depuración
        } finally {
            // Cierra los recursos de la base de datos en el bloque finally para asegurar su liberación
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }

    private void saveChanges() {
        // Obtiene los valores ingresados por el usuario
        String newUsername = usernameField.getText().trim();
        String enteredEmail = email.getText().trim(); // El usuario lo ha introducido
        String newPass = new String(Pass.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        // --- Validaciones de Campos Obligatorios y Email ---
        if (newUsername.isEmpty() || enteredEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre de usuario y Correo Electrónico son campos obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!enteredEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Formato de correo electrónico inválido.", "Validación de Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- VERIFICACIÓN DE SEGURIDAD: Correo Electrónico ---
        if (!enteredEmail.equals(originalEmail)) {
            JOptionPane.showMessageDialog(this, "El Correo Electrónico ingresado no coincide con el registrado. No se pueden guardar los cambios.", "Error de Verificación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPasswordForDB = null;

        // Lógica para el cambio de contraseña
        if (!newPass.isEmpty()) {
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Las nuevas contraseñas no coinciden.", "Validación de Contraseña", JOptionPane.WARNING_MESSAGE);
                return;
            }
            hashedPasswordForDB = BCrypt.hashpw(newPass, BCrypt.gensalt(10));
        } else {
            hashedPasswordForDB = originalHashedPassword;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().connect();

            String sql = "UPDATE Usuarios SET NombreUsuario = ?, CorreoElectronico = ?, Contrasena = ? WHERE UsuarioID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newUsername);
            stmt.setString(2, enteredEmail);
            stmt.setString(3, hashedPasswordForDB);
            stmt.setInt(4, UserId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Perfil actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra la ventana del formulario
                // Aquí podrías redirigir a VistaEjemplo o volver a cargar la interfaz principal
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el perfil. ¿Usuario no encontrado?", "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar los cambios: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }

    // --- NUEVO MÉTODO PARA ELIMINAR LA CUENTA ---
    private void deleteUserAccount() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().connect();

            // SQL para eliminar el usuario basado en su UsuarioID
            String sql = "DELETE FROM Usuarios WHERE UsuarioID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserId); // Usa el ID del usuario logueado

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Su cuenta ha sido eliminada exitosamente.", "Cuenta Eliminada", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra la ventana actual de Perfil

                // Redirigir al usuario a la pantalla de login
                // Cierra cualquier otra ventana abierta si es necesario (ej. VistaEjemplo)
                // y abre una nueva ventana de login.
                SwingUtilities.invokeLater(() -> {
                    JFrame loginFrame = new JFrame("Login de Usuario");
                    loginFrame.setContentPane(new user().panelMain); // Instancia un nuevo login
                    loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    loginFrame.setSize(400, 250);
                    loginFrame.setLocationRelativeTo(null);
                    loginFrame.setVisible(true);

                    // Asegúrate de cerrar cualquier otra ventana principal si existiera
                    // Esto es un poco más complejo si tienes múltiples ventanas principales.
                    // Para un solo JFrame padre como VistaEjemplo, puedes hacer:
                    // JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this.Panel);
                    // if (parentFrame != null) {
                    //     parentFrame.dispose();
                    // }
                });


            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la cuenta. ¿Usuario no encontrado?", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la cuenta: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }

    // El método main para pruebas directas de Perfil (opcional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Para probar el perfil, pasa un UsuarioID válido de tu base de datos
            // Por ejemplo, el ID del usuario 'Administrador' o cualquier otro.
            new Perfil(1).setVisible(true); // Reemplaza 1 con un UsuarioID existente para pruebas
        });
    }
}