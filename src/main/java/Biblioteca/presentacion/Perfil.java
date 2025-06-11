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
    // Declaración de los componentes de la interfaz de usuario
    private JPanel Panel; // Este es tu panel principal, nombrado 'Panel'
    private JTextField usernameField;
    private JTextField email; // Campo para el correo electrónico
    private JPasswordField Pass; // Campo para la nueva contraseña
    private JPasswordField confirmPasswordField;
    private JButton saveButton;
    private JButton cancelButton;

    // Variables para almacenar los datos originales del usuario de la base de datos
    private int userId; // ID del usuario que estamos modificando
    private String originalUsername; // Para guardar el nombre de usuario original
    private String originalEmail; // Para guardar el email original de la DB (necesario para la validación)
    private String originalHashedPassword; // Para guardar el hash de la contraseña original de la DB

    public Perfil(int userId) {
        this.userId = userId;

        setTitle("Modificar Perfil de Usuario");
        setSize(450, 400); // Ajustamos el tamaño del formulario
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        initComponents(); // Inicializa y configura los componentes de la interfaz
        loadUserData(); // Carga los datos actuales del usuario desde la base de datos
        // (pero ahora el campo de email no se precargará)

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
        // Asegúrate de que el campo 'email' se inicialice vacío si es lo que deseas,
        // esto lo haces NO llamando a email.setText(originalEmail) en loadUserData()
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
            stmt.setInt(1, userId); // Establece el ID del usuario para la consulta
            rs = stmt.executeQuery(); // Ejecuta la consulta

            if (rs.next()) { // Si se encuentra el usuario
                originalUsername = rs.getString("NombreUsuario");
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

        // --- Validaciones de Campos Obligatorios ---
        if (newUsername.isEmpty() || enteredEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre de usuario y Correo Electrónico son campos obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return; // Detiene la ejecución si hay campos vacíos obligatorios
        }

        // 1. Validar formato de correo electrónico
        if (!enteredEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // Regex para validación básica de email
            JOptionPane.showMessageDialog(this, "Formato de correo electrónico inválido.", "Validación de Email", JOptionPane.WARNING_MESSAGE);
            return; // Detiene la ejecución si el formato del email es inválido
        }

        // --- 2. VERIFICACIÓN DE SEGURIDAD: Solo Correo Electrónico ---
        // ¡Esta lógica se mantiene! Si el correo ingresado por el usuario NO coincide con el correo original en la DB, no se permite NINGÚN cambio.
        if (!enteredEmail.equals(originalEmail)) {
            JOptionPane.showMessageDialog(this, "El Correo Electrónico ingresado no coincide con el registrado. No se pueden guardar los cambios.", "Error de Verificación", JOptionPane.ERROR_MESSAGE);
            return; // Detiene el proceso si la verificación del correo falla
        }

        String hashedPasswordForDB = null; // Variable para almacenar el hash de la contraseña a guardar

        // Lógica para el cambio de contraseña
        if (!newPass.isEmpty()) { // Si el usuario ingresó algo en "Nueva Contraseña"
            if (!newPass.equals(confirmPass)) { // Verifica que las nuevas contraseñas coincidan
                JOptionPane.showMessageDialog(this, "Las nuevas contraseñas no coinciden.", "Validación de Contraseña", JOptionPane.WARNING_MESSAGE);
                return; // Detiene la ejecución si las contraseñas no coinciden
            }
            // Hashear la nueva contraseña con jBCrypt
            hashedPasswordForDB = BCrypt.hashpw(newPass, BCrypt.gensalt(10));
        } else {
            // Si no se ingresó una nueva contraseña, mantener el hash original que ya está en la DB
            hashedPasswordForDB = originalHashedPassword;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getInstance().connect(); // Establece conexión con la base de datos

            // Consulta SQL para actualizar los datos del usuario
            String sql = "UPDATE Usuarios SET NombreUsuario = ?, CorreoElectronico = ?, Contrasena = ? WHERE UsuarioID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newUsername); // Nuevo nombre de usuario
            stmt.setString(2, enteredEmail); // Correo electrónico (verificado)
            stmt.setString(3, hashedPasswordForDB); // Hash de la contraseña (nueva o la original)
            stmt.setInt(4, userId); // ID del usuario a actualizar

            int rowsAffected = stmt.executeUpdate(); // Ejecuta la actualización

            if (rowsAffected > 0) { // Si se afectó al menos una fila (la actualización fue exitosa)
                JOptionPane.showMessageDialog(this, "Perfil actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra la ventana del formulario
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el perfil. ¿Usuario no encontrado?", "Error de Actualización", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar los cambios: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime la pila de errores para depuración
        } finally {
            // Cierra los recursos de la base de datos en el bloque finally
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
    }
}