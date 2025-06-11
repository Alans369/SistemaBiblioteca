package Biblioteca.persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO (Data Access Object) para la entidad Usuario
 * Maneja todas las operaciones de acceso a datos relacionadas con usuarios
 */
public class UserDAO {

    /**
     * Autentica un usuario por nombre de usuario
     * @param nombreUsuario El nombre de usuario a buscar
     * @return Map con datos del usuario o null si no existe
     */
    public Map<String, Object> obtenerUsuarioPorNombre(String nombreUsuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> usuario = null;

        try {
            conn = ConnectionManager.getInstance().connect();
            // ¡CAMBIO AQUÍ: 'Email' a 'CorreoElectronico'!
            String sql = "SELECT UsuarioID, NombreUsuario, Contrasena, CorreoElectronico, Rol FROM Usuarios WHERE NombreUsuario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombreUsuario);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new HashMap<>();
                usuario.put("UsuarioID", rs.getInt("UsuarioID"));
                usuario.put("NombreUsuario", rs.getString("NombreUsuario"));
                usuario.put("Contrasena", rs.getString("Contrasena"));
                usuario.put("CorreoElectronico", rs.getString("CorreoElectronico")); // ¡CAMBIO AQUÍ!
                usuario.put("Rol", rs.getString("Rol"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por nombre: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, stmt, conn);
        }

        return usuario;
    }

    /**
     * Obtiene un usuario por su ID
     * @param usuarioID El ID del usuario
     * @return Map con datos del usuario o null si no existe
     */
    public Map<String, Object> obtenerUsuarioPorID(int usuarioID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> usuario = null;

        try {
            conn = ConnectionManager.getInstance().connect();
            // ¡CAMBIO AQUÍ: 'Email' a 'CorreoElectronico'!
            String sql = "SELECT UsuarioID, NombreUsuario, Contrasena, CorreoElectronico, Rol FROM Usuarios WHERE UsuarioID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new HashMap<>();
                usuario.put("UsuarioID", rs.getInt("UsuarioID"));
                usuario.put("NombreUsuario", rs.getString("NombreUsuario"));
                usuario.put("Contrasena", rs.getString("Contrasena"));
                usuario.put("CorreoElectronico", rs.getString("CorreoElectronico")); // ¡CAMBIO AQUÍ!
                usuario.put("Rol", rs.getString("Rol"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, stmt, conn);
        }

        return usuario;
    }

    /**
     * Crea un nuevo usuario en la base de datos
     * @param nombreUsuario Nombre del usuario
     * @param contrasena Contraseña encriptada
     * @param email Email del usuario
     * @param rol Rol del usuario
     * @return true si se creó exitosamente, false en caso contrario
     */
    public boolean crearUsuario(String nombreUsuario, String contrasena, String email, String rol) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean creado = false;

        try {
            conn = ConnectionManager.getInstance().connect();
            // ¡CAMBIO AQUÍ: 'Email' a 'CorreoElectronico'!
            String sql = "INSERT INTO Usuarios (NombreUsuario, Contrasena, CorreoElectronico, Rol) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contrasena);
            stmt.setString(3, email); // El parámetro de entrada sigue siendo 'email', pero se mapea a 'CorreoElectronico'
            stmt.setString(4, rol);

            int filasAfectadas = stmt.executeUpdate();
            creado = (filasAfectadas > 0);

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, stmt, conn);
        }

        return creado;
    }

    /**
     * Actualiza un usuario existente
     * @param usuarioID ID del usuario a actualizar
     * @param nombreUsuario Nuevo nombre de usuario
     * @param contrasena Nueva contraseña encriptada
     * @param email Nuevo email
     * @param rol Nuevo rol
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean actualizarUsuario(int usuarioID, String nombreUsuario, String contrasena, String email, String rol) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean actualizado = false;

        try {
            conn = ConnectionManager.getInstance().connect();
            // ¡CAMBIO AQUÍ: 'Email' a 'CorreoElectronico'!
            String sql = "UPDATE Usuarios SET NombreUsuario = ?, Contrasena = ?, CorreoElectronico = ?, Rol = ? WHERE UsuarioID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contrasena);
            stmt.setString(3, email); // El parámetro de entrada sigue siendo 'email', pero se mapea a 'CorreoElectronico'
            stmt.setString(4, rol);
            stmt.setInt(5, usuarioID);

            int filasAfectadas = stmt.executeUpdate();
            actualizado = (filasAfectadas > 0);

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, stmt, conn);
        }

        return actualizado;
    }

    /**
     * Elimina un usuario por su ID
     * @param usuarioID El ID del usuario a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean eliminarUsuario(int usuarioID) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean eliminado = false;

        try {
            conn = ConnectionManager.getInstance().connect();
            String sql = "DELETE FROM Usuarios WHERE UsuarioID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioID);

            int filasAfectadas = stmt.executeUpdate();
            eliminado = (filasAfectadas > 0);

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, stmt, conn);
        }

        return eliminado;
    }

    /**
     * Obtiene todos los usuarios de la base de datos
     * @return Lista de Maps con datos de usuarios
     */
    public List<Map<String, Object>> obtenerTodosLosUsuarios() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> usuarios = new ArrayList<>();

        try {
            conn = ConnectionManager.getInstance().connect();
            // ¡CAMBIO AQUÍ: 'Email' a 'CorreoElectronico'!
            String sql = "SELECT UsuarioID, NombreUsuario, Contrasena, CorreoElectronico, Rol FROM Usuarios";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> usuario = new HashMap<>();
                usuario.put("UsuarioID", rs.getInt("UsuarioID"));
                usuario.put("NombreUsuario", rs.getString("NombreUsuario"));
                usuario.put("Contrasena", rs.getString("Contrasena"));
                usuario.put("CorreoElectronico", rs.getString("CorreoElectronico")); // ¡CAMBIO AQUÍ!
                usuario.put("Rol", rs.getString("Rol"));
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, stmt, conn);
        }

        return usuarios;
    }

    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     * @param nombreUsuario El nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String nombreUsuario) {
        return obtenerUsuarioPorNombre(nombreUsuario) != null;
    }

    /**
     * Método auxiliar para cerrar recursos de base de datos
     * @param rs ResultSet a cerrar
     * @param stmt PreparedStatement a cerrar
     * @param conn Connection a cerrar
     */
    private void cerrarRecursos(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos de base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}