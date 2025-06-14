package Biblioteca.persistencia;

import java.sql.PreparedStatement; // Clase para ejecutar consultas SQL preparadas, previniendo inyecciones SQL.
import java.sql.ResultSet;        // Interfaz para representar el resultado de una consulta SQL.
import java.sql.SQLException;     // Clase para manejar errores relacionados con la base de datos SQL.
import java.util.ArrayList;

import Biblioteca.dominio.Libro;
import Biblioteca.dominio.Categoria;

public class LibroDAO {
    private ConnectionManager conn; // Objeto para gestionar la conexión con la base de datos.
    private PreparedStatement ps;   // Objeto para ejecutar consultas SQL preparadas.
    private ResultSet rs;           // Objeto para almacenar el resultado de una consulta SQL.

    public LibroDAO(){
        conn = ConnectionManager.getInstance();
    }

    public Categoria getByIdC(int id) throws SQLException{
        Categoria cat  = new Categoria(); // Inicializar un objeto User que se retornará.

        try {
            // Preparar la sentencia SQL para seleccionar un usuario por su ID.
            ps = conn.connect().prepareStatement("SELECT * " +
                    "FROM Categorias " +
                    "WHERE CategoriaID  = ?");


            // Establecer el valor del parámetro en la sentencia preparada (el ID a buscar).
            ps.setInt(1,id);

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Verificar si se encontró algún registro.
            if (rs.next()) {
                cat.setCategoriaID(rs.getInt(1));       // Obtener el ID del usuario.
                cat.setNombreCategoria(rs.getString(2));   // Obtener el nombre del usuario.
                cat.setDescripcion(rs.getString(3));
            } else {
                // Si no se encontró ningún usuario con el ID especificado, establecer el objeto User a null.
                cat = null;
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        } catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al obtener un usuario por id: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return cat;

    }

    public Libro getById(int id) throws SQLException{
        Libro libro  = new Libro(); // Inicializar un objeto User que se retornará.

        try {
            // Preparar la sentencia SQL para seleccionar un usuario por su ID.
            ps = conn.connect().prepareStatement("SELECT ID,Titulo,Autor,ImagenURL,Descripcion,RutaArchivoPDF,CategoriaID " +
                    "FROM Libros " +
                    "WHERE ID = ?");


            // Establecer el valor del parámetro en la sentencia preparada (el ID a buscar).
            ps.setInt(1,id);

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Verificar si se encontró algún registro.
            if (rs.next()) {

                    // Asignar los valores de las columnas a los atributos del objeto User.
                    libro.setId(rs.getInt("ID"));       // Obtener el ID del usuario.
                    libro.setTitulo(rs.getString("Titulo"));   // Obtener el nombre del usuario.
                    libro.setAutor(rs.getString("Autor"));  // Obtener el correo electrónico del usuario.
                    libro.setImagenR(rs.getString("ImagenURL"));
                    libro.setDescripcion((rs.getString("Descripcion")));
                    libro.setRutaPdf((rs.getString("RutaArchivoPDF")));
                    libro.setCategoriaId(rs.getInt("CategoriaID"));
                    // Obtener el estado del usuario.
                    // Agregar el objeto User a la lista de resultados
            } else {
                // Si no se encontró ningún usuario con el ID especificado, establecer el objeto User a null.
                libro = null;
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        } catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al obtener un usuario por id: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return libro; // Retornar el objeto User encontrado o null si no existe.
    }

    public Libro create(Libro libro) throws SQLException{
        Libro res = null;
        try {

            PreparedStatement ps  = conn.connect().prepareStatement(
                    "INSERT INTO " +
                            "Libros(Titulo,Autor,ImagenURL,Descripcion,RutaArchivoPDF,CategoriaID)" +
                            "values(?,?,?,?,?,?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1,libro.getTitulo());
            ps.setString(2,libro.getAutor());
            ps.setString(3, libro.getImagenR());
            ps.setString(4, libro.getDescripcion());
            ps.setString(5, libro.getRutaPdf());
            ps.setInt(6,libro.getCategoriaId());

            // Ejecutar la sentencia de inserción y obtener el número de filas afectadas.
            int affectedRows = ps.executeUpdate();

            // Verificar si la inserción fue exitosa (al menos una fila afectada).
            if (affectedRows != 0) {
                // Obtener las claves generadas automáticamente por la base de datos (en este caso, el ID).
                ResultSet  generatedKeys = ps.getGeneratedKeys();
                // Mover el cursor al primer resultado (si existe).
                if (generatedKeys.next()) {
                    // Obtener el ID generado. Generalmente la primera columna contiene la clave primaria.
                    System.out.print("id generado " + generatedKeys.getInt(1));
                    int idGenerado= generatedKeys.getInt(1);

                    // Recuperar el usuario completo utilizando el ID generado.
                    res = getById(idGenerado);
                } else {
                    // Lanzar una excepción si la creación del usuario falló y no se obtuvo un ID.
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos

        } catch (Exception ex) {
            throw new SQLException("Error al crear el usuario: " + ex.getMessage(), ex);
        }
        finally {
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.

        }
        return res; // Retornar el usuario creado (con su ID asignado) o null si hubo un error.
        }

    public boolean update(Libro libro) throws SQLException{
        boolean res = false; // Variable para indicar si la actualización fue exitosa.
        try{
            ps = conn.connect().prepareStatement(
                    "UPDATE Libros " +
                            "SET Titulo = ?, Autor = ?, ImagenURL = ?, Descripcion = ?, RutaArchivoPDF = ? " +
                            "WHERE ID = ?"
            );


            ps.setString(1,libro.getTitulo());
            ps.setString(2,libro.getAutor());
            ps.setString(3, libro.getImagenR());
            ps.setString(4, libro.getDescripcion());
            ps.setString(5, libro.getRutaPdf());
            ps.setInt(6,libro.getId());

            // Ejecutar la sentencia de actualización y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la actualización fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recurso



        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al modificar el usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res; // Retornar el resultado de la operación de actualización.
    }

    public boolean delete(Libro libro) throws SQLException {
        boolean res = false; // Variable para indicar si la eliminación fue exitosa.
        try{
            // Preparar la sentencia SQL para eliminar un usuario por su ID.
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Libros WHERE id = ?"
            );
            // Establecer el valor del parámetro en la sentencia preparada (el ID del usuario a eliminar).
            ps.setInt(1, libro.getId());

            // Ejecutar la sentencia de eliminación y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la eliminación fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al eliminar el usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res; // Retornar el resultado de la operación de eliminación.


    }

    public ArrayList<Libro> search(String name) throws SQLException {
        ArrayList<Libro> records  = new ArrayList<>();

        try {
            // Preparar la sentencia SQL para buscar usuarios por nombre (usando LIKE para búsqueda parcial).
            ps = conn.connect().prepareStatement("SELECT Titulo,Autor,ImagenURL,Descripcion,RutaArchivoPDF,CategoriaID " +
                    "FROM Libros " +
                    "WHERE Titulo LIKE ?");

            // Establecer el valor del parámetro en la sentencia preparada.
            // El '%' al inicio y al final permiten la búsqueda de la cadena 'name' en cualquier parte del nombre del usuario.
            ps.setString(1, "%" + name + "%");

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Iterar a través de cada fila del resultado.
            while (rs.next()){
                // Crear un nuevo objeto User para cada registro encontrado.
                Libro libro = new Libro();
                // Asignar los valores de las columnas a los atributos del objeto User.
                     // Obtener el ID del usuario.
                libro.setTitulo(rs.getString(1));   // Obtener el nombre del usuario.
                libro.setAutor(rs.getString(2));  // Obtener el correo electrónico del usuario.
                libro.setImagenR(rs.getString(3));
                libro.setDescripcion((rs.getString(4)));
                libro.setRutaPdf((rs.getString(5)));

                libro.setCategoriaId(rs.getInt(6));
                // Obtener el estado del usuario.
                // Agregar el objeto User a la lista de resultados.
                records.add(libro);
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al buscar usuarios: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return records; // Retornar la lista de usuarios encontrados
    }

    public  ArrayList<Categoria> selectCategoria() throws SQLException{
        ArrayList<Categoria> records  = new ArrayList<>();

        try {
            // Preparar la sentencia SQL para buscar usuarios por nombre (usando LIKE para búsqueda parcial).
            ps = conn.connect().prepareStatement("SELECT * FROM Categorias");

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Iterar a través de cada fila del resultado.
            while (rs.next()){
                // Crear un nuevo objeto User para cada registro encontrado.
                Categoria cat = new Categoria();
                // Asignar los valores de las columnas a los atributos del objeto User.
                cat.setCategoriaID(rs.getInt(1));       // Obtener el ID del usuario.
                cat.setNombreCategoria(rs.getString(2));   // Obtener el nombre del usuario.
                cat.setDescripcion(rs.getString(3));  // Obtener el correo electrónico del usuario.
                records.add(cat);
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al buscar usuarios: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return records; // Retornar la lista de usuarios encontrados

    }




    }


