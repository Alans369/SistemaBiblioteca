package Biblioteca.persistencia;

import Biblioteca.dominio.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class categoriaDAO {

    private ConnectionManager connection;

    public categoriaDAO() {
        connection = ConnectionManager.getInstance();
    }

    // Crear una nueva categoría
    public void create(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO Categorias (NombreCategoria, Descripcion) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombreCategoria());
            stmt.setString(2, categoria.getDescripcion());
            stmt.executeUpdate();
        }
    }

    // Leer todas las categorías
    public List<Categoria> readAll() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT CategoriaID, NombreCategoria, Descripcion FROM Categorias ORDER BY NombreCategoria";

        try (Statement stmt = connection.connect().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int categoriaID = rs.getInt("CategoriaID");
                String nombreCategoria = rs.getString("NombreCategoria");
                String descripcion = rs.getString("Descripcion");

                Categoria categoria = new Categoria(categoriaID, nombreCategoria, descripcion);
                categorias.add(categoria);
            }
        }
        return categorias;
    }

    // Leer una categoría por ID
    public Categoria readById(int id) throws SQLException {
        String sql = "SELECT CategoriaID, NombreCategoria, Descripcion FROM Categorias WHERE CategoriaID = ?";

        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int categoriaID = rs.getInt("CategoriaID");
                    String nombreCategoria = rs.getString("NombreCategoria");
                    String descripcion = rs.getString("Descripcion");

                    return new Categoria(categoriaID, nombreCategoria, descripcion);
                }
            }
        }
        return null;
    }

    // Actualizar una categoría existente
    public void update(Categoria categoria) throws SQLException {
        String sql = "UPDATE Categorias SET NombreCategoria = ?, Descripcion = ? WHERE CategoriaID = ?";

        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombreCategoria());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setInt(3, categoria.getCategoriaID());
            stmt.executeUpdate();
        }
    }

    // Eliminar una categoría por ID
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Categorias WHERE CategoriaID = ?";

        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Verificar si existe una categoría con el mismo nombre (para evitar duplicados)
    public boolean existeNombre(String nombreCategoria, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categorias WHERE NombreCategoria = ? AND CategoriaID != ?";

        try (PreparedStatement stmt = connection.connect().prepareStatement(sql)) {
            stmt.setString(1, nombreCategoria);
            stmt.setInt(2, idExcluir);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}