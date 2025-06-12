package Biblioteca.persistencia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt; // Aunque no se usa directamente en este test, se mantiene si tu DAO lo usa

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private UserDAO userDAO;
    private Connection conn; // Para la conexión de test, usada en setup/teardown

    // Datos de prueba para el usuario que insertaremos
    private final String TEST_USERNAME = "usuario_id_test";
    private final String TEST_PASSWORD = "pass_id_test";
    private final String TEST_EMAIL = "id_test@example.com";
    private final String TEST_ROLE = "test_rol";
    private int insertedUserId; // Almacenará el ID del usuario insertado para la prueba

    @BeforeEach
    @DisplayName("Configuración inicial: Limpiar DB e insertar un usuario para probar por ID")
    void setUp() throws SQLException {
        userDAO = new UserDAO();
        conn = ConnectionManager.getInstance().connect();

        // Limpiar la tabla Usuarios antes de cada test para asegurar un estado conocido
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Usuarios")) {
            stmt.executeUpdate();
        }

        // Insertar un usuario específico para probar la búsqueda por ID
        insertedUserId = insertTestUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL, TEST_ROLE);
        assertTrue(insertedUserId > 0, "Debe insertarse un usuario de prueba para poder buscarlo.");
    }

    @AfterEach
    @DisplayName("Limpieza después de cada prueba: Cerrar conexión")
    void tearDown() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión en tearDown: " + e.getMessage());
                e.printStackTrace();
            }
        }
        userDAO = null;
    }

    /**
     * Método auxiliar para insertar un usuario de prueba directamente.
     * @return El ID del usuario insertado, o -1 si falla.
     * @throws SQLException
     */
    private int insertTestUser(String username, String password, String email, String role) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO Usuarios (NombreUsuario, Contrasena, CorreoElectronico, Rol) " +
                "OUTPUT INSERTED.UsuarioID " + // Para SQL Server
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.setString(4, role);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    // --- Test para obtenerUsuarioPorID ---

    @Test
    @DisplayName("Debe obtener un usuario existente por ID")
    void obtenerUsuarioPorID_UsuarioExistente_DebeRetornarUsuario() {
        // Usamos el ID del usuario que insertamos en setUp
        Map<String, Object> foundUser = userDAO.obtenerUsuarioPorID(insertedUserId);

        assertNotNull(foundUser, "El usuario debería ser encontrado por ID");
        assertEquals(insertedUserId, (Integer) foundUser.get("UsuarioID"), "El ID del usuario debe coincidir");
        assertEquals(TEST_USERNAME, foundUser.get("NombreUsuario"), "El nombre de usuario debe coincidir");
        assertEquals(TEST_EMAIL, foundUser.get("CorreoElectronico"), "El correo debe coincidir");
        assertEquals(TEST_ROLE, foundUser.get("Rol"), "El rol debe coincidir");
    }

    @Test
    @DisplayName("Debe retornar null si el usuario no existe por ID")
    void obtenerUsuarioPorID_UsuarioNoExistente_DebeRetornarNull() {
        Map<String, Object> foundUser = userDAO.obtenerUsuarioPorID(99999); // ID que no existe
        assertNull(foundUser, "El usuario no debería ser encontrado por un ID inexistente");
    }
}