package Biblioteca.persistencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Biblioteca.dominio.Libro;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void createLibro() throws SQLException {
        Libro libro = new Libro(0,"esto es prueba","Edgard","ruta de imageb local","n/a","ruta local",1);
        libroDAO.create(libro);

    }
}