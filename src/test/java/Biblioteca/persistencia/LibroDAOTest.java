package Biblioteca.persistencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Biblioteca.dominio.Libro;
import Biblioteca.dominio.Categoria;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;              // Clase para crear listas dinámicas de objetos, utilizada en algunas pruebas.
import java.util.Random;

import java.sql.SQLException;

class LibroDAOTest {
    private LibroDAO libroDAO;

    @BeforeEach
    void setUp(){
        // Método que se ejecuta antes de cada método de prueba (@Test).
        // Su propósito es inicializar el entorno de prueba, en este caso,
        // creando una nueva instancia de UserDAO para cada prueba.
        libroDAO = new LibroDAO();
    }

    private Libro createLibro(Libro libro) throws SQLException {
        // Llama al método 'create' del LibroDAO para persistir el libro en la base de datos
        Libro res = libroDAO.create(libro);

        // Realiza aserciones para verificar que la creación del libro fue exitosa
        // y que los datos del libro retornado coinciden con los datos originales.
        assertNotNull(res, "El libro creado no debería ser nulo."); // Verifica que el libro no sea nulo
        assertEquals(libro.getTitulo(), res.getTitulo(), "El título del libro creado debe ser igual al original.");
        assertEquals(libro.getAutor(), res.getAutor(), "El autor del libro creado debe ser igual al original.");
        assertEquals(libro.getImagenR(), res.getImagenR(), "La ruta de imagen del libro creado debe ser igual a la original.");
        assertEquals(libro.getDescripcion(), res.getDescripcion(), "La descripción del libro creado debe ser igual a la original.");
        assertEquals(libro.getRutaPdf(), res.getRutaPdf(), "La ruta PDF del libro creado debe ser igual a la original.");
        assertEquals(libro.getCategoriaId(), res.getCategoriaId(), "El ID de categoría del libro creado debe ser igual al original.");

        // Retorna el objeto Libro creado (tal como lo devolvió el LibroDAO).
        return res;
    }
    private void updateLibro(Libro libro) throws SQLException {
        // Modifica los atributos del objeto Libro para simular una actualización.
        libro.setTitulo(libro.getTitulo());


        libro.setAutor("Nuevo Autor: " + libro.getAutor());   // Añade "Nuevo Autor: " al inicio del autor.
        libro.setDescripcion("Descripción actualizada: " + libro.getDescripcion()); // Actualiza la descripción.
        libro.setCategoriaId(libro.getCategoriaId()); // Incrementa el ID de categoría en 1.
        // Puedes añadir más modificaciones para otras propiedades como imagenR o rutaPdf si lo deseas.

        // Llama al método 'update' del LibroDAO para actualizar el libro en la base de datos (simulada).
        boolean res = libroDAO.update(libro);

        // Realiza una aserción para verificar que la actualización fue exitosa.
        assertTrue(res, "La actualización del libro debería ser exitosa.");

        // Llama al método 'getById' (asumiendo que tienes uno similar a getUserById)
        // para verificar que los cambios se persistieron correctamente.
        // Aunque el método 'getById' ya tiene sus propias aserciones, esta llamada adicional
        // ayuda a asegurar que la actualización realmente tuvo efecto en la capa de datos.
        // Para esto, 'getById' necesitará el ID del libro, asumiendo que 'libro.getId()' ya está populado.
        getLibroById(libro);
    }
    public void getLibroById(Libro libro) throws SQLException {
        // Llama al método 'getById' del LibroDAO para obtener un libro por su ID.
        Libro res = libroDAO.getById(libro.getId());

        // Realiza aserciones para verificar que el libro obtenido coincide
        // con el libro original (o el libro modificado en pruebas de actualización).
        assertNotNull(res, "El libro obtenido por ID no debería ser nulo.");
        assertEquals(libro.getId(), res.getId(), "El ID del libro obtenido debe ser igual al original.");
        assertEquals(libro.getTitulo(), res.getTitulo(), "El título del libro obtenido debe ser igual al esperado.");
        assertEquals(libro.getAutor(), res.getAutor(), "El autor del libro obtenido debe ser igual al esperado.");
        assertEquals(libro.getImagenR(), res.getImagenR(), "La ruta de imagen del libro obtenido debe ser igual a la esperada.");
        assertEquals(libro.getDescripcion(), res.getDescripcion(), "La descripción del libro obtenido debe ser igual a la esperada.");
        assertEquals(libro.getRutaPdf(), res.getRutaPdf(), "La ruta PDF del libro obtenido debe ser igual a la esperada.");
        assertEquals(libro.getCategoriaId(), res.getCategoriaId(), "El ID de categoría del libro obtenido debe ser igual al esperado.");
    }
    public void searchLibroByTitulo(Libro libro) throws SQLException {
        // Llama al método 'search' del LibroDAO para buscar libros por título.
        // Asumo que el método search de tu LibroDAO ahora toma un String (el título a buscar)
        // y devuelve una lista de Libros.
        ArrayList<Libro> libros = libroDAO.search(libro.getTitulo());

        boolean allContainSearchTerm = true; // Variable para rastrear si todos los libros encontrados contienen la cadena de búsqueda.

        // Itera sobre la lista de libros devuelta por la búsqueda.
        for (Libro libroItem : libros) {
            // Verifica si el título de cada libro encontrado contiene la cadena de búsqueda.
            // La comparación debe ser insensible a mayúsculas/minúsculas si es lo que esperas.
            if (!libroItem.getTitulo().toLowerCase().contains(libro.getTitulo().toLowerCase())) {
                allContainSearchTerm = false; // Si un título no contiene la cadena de búsqueda, se establece a false.
                break; // Se sale del bucle, ya que no todos los resultados cumplen la condición.
            }
        }

        // Realiza una aserción para verificar que todos los libros encontrados contienen la cadena de búsqueda.
        // También podrías querer verificar que la lista 'libros' no esté vacía si esperas resultados.
        assertTrue(allContainSearchTerm, "No todos los libros encontrados contenían el término de búsqueda: " + libro.getTitulo());
        assertFalse(libros.isEmpty(), "La búsqueda no debería haber devuelto una lista vacía para el término: " + libro.getTitulo());
    }

    private void delete(Libro libro) throws SQLException{
        // Llama al método 'delete' del UserDAO para eliminar un usuario por su ID.
        boolean res = libroDAO.delete(libro);

        // Realiza una aserción para verificar que la eliminación fue exitosa.
        assertTrue(res, "La eliminación del usuario debería ser exitosa.");

        // Intenta obtener el usuario por su ID después de la eliminación.
        Libro res2 = libroDAO.getById(libro.getId());

        // Realiza una aserción para verificar que el usuario ya no existe en la base de datos
        // después de la eliminación (el método 'getById' debería retornar null).
        assertNull(res2, "El usuario debería haber sido eliminado y no encontrado por ID.");
    }


    @Test
    void testLibro() throws SQLException {

       Random random = new Random();
        // Genera un número aleatorio entre 1 y 1000 para asegurar la unicidad del email en cada prueba.
        int num = random.nextInt(1000) + 1;
        // Define una cadena base para el email y le concatena el número aleatorio generado.

        // Crea un nuevo objeto User con datos de prueba. El ID se establece en 0 ya que será generado por la base de datos.
        Libro libro = new Libro(0,"test"+num,"edgarone","rutaimagen","n/a","rutapdf",1);
        Libro librotest = createLibro(libro);

        updateLibro(librotest);

        getLibroById(librotest);

        searchLibroByTitulo(librotest);

        delete(librotest);





    }
}