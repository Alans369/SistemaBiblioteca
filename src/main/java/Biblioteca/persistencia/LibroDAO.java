package Biblioteca.persistencia;

import java.sql.PreparedStatement; // Clase para ejecutar consultas SQL preparadas, previniendo inyecciones SQL.
import java.sql.ResultSet;        // Interfaz para representar el resultado de una consulta SQL.
import java.sql.SQLException;     // Clase para manejar errores relacionados con la base de datos SQL.
import java.util.ArrayList;

import Biblioteca.dominio.Libro;

public class LibroDAO {
    private ConnectionManager conn; // Objeto para gestionar la conexión con la base de datos.
    private PreparedStatement ps;   // Objeto para ejecutar consultas SQL preparadas.
    private ResultSet rs;           // Objeto para almacenar el resultado de una consulta SQL.

    public LibroDAO(){
        conn = ConnectionManager.getInstance();
    }

    public void create(Libro libro) throws SQLException{
        try {

            PreparedStatement ps  = conn.connect().prepareStatement(
                    "INSERT INTO " +
                            "Libros(Titulo,Autor,ImagenURL,Descripcion,RutaArchivoPDF,CategoriaID)" +
                            "values(?,?,?,?,?,?)"
            );
            ps.setString(1,libro.getTitulo());
            ps.setString(2,libro.getAutor());
            ps.setString(3, libro.getImagenR());
            ps.setString(4, libro.getDescripcion());
            ps.setString(5, libro.getRutaPdf());
            ps.setInt(6,libro.getCategoriaId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows != 0) {
                System.out.print("se inserto el libro correctamente");
            }

            ps.close();

        } catch (Exception ex) {
            throw new SQLException("Error al crear el usuario: " + ex.getMessage(), ex);
        }
        finally {
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.

        }


        }

    }


