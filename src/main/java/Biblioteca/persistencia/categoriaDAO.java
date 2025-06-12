package Biblioteca.persistencia;

import Biblioteca.presentacion.categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class categoriaDAO {

    private ConnectionManager connection; // Objeto para gestionar la conexión con la base de datos.
    private PreparedStatement ps;   // Objeto para ejecutar consultas SQL preparadas.
    private ResultSet rs;

    // Constructor para establecer la conexión
    public categoriaDAO() {
        connection = ConnectionManager.getInstance();
    }

    // Crear una nueva categoría
    public void create(categoria cat) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setString(1, cat.getNombre());
            stmt.setString(2, cat.getDescripcion());
            stmt.executeUpdate();
        }
    }

    // Leer todas las categorías
    public List<categoria> readAll() throws SQLException {
        List<categoria> categories = new ArrayList<>();
        String sql = "SELECT * FROM categoria";
        try (Statement stmt = connection.connect().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                categoria cat = new categoria();
                cat.setId(id);
                cat.setNombre(nombre);
                cat.setDescripcion(descripcion);
                categories.add(cat);
            }
        }
        return categories;
    }

    // Actualizar una categoría existente
    public void update(categoria cat) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setString(1, cat.getNombre());
            stmt.setString(2, cat.getDescripcion());
            stmt.setInt(3, cat.getId());
            stmt.executeUpdate();
        }
    }

    // Eliminar una categoría por id
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}