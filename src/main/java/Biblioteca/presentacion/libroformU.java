package Biblioteca.presentacion;

import Biblioteca.dominio.Categoria;

import Biblioteca.dominio.Libro;

import Biblioteca.persistencia.LibroDAO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;

public class libroformU extends JDialog {
    private JPanel libropanel;
    private JTextField txtdecripcion;
    private JComboBox<Categoria> boxcategoria;
    private JButton btnlibro;
    private JTextField txtTitulo;
    private JTextField txtautor;
    private JButton btnimagen;
    private JButton button1;
    private JButton crearButton;
    private JButton cancelarButton;
    private JLabel imagenurl;
    private LibroDAO libroDA0;


    public libroformU(Mainform mainForm)  {
        super(mainForm, "Actualizar Libro ", true); // true para modal
        setSize(700, 700);
        setLocationRelativeTo(mainForm); // Centra el diálogo respecto a su propietario
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        libroDA0 = new LibroDAO();
        try {
           Libro libro = libroDA0.getById(39);
            txtTitulo.setText(libro.getTitulo());
            txtautor.setText(libro.getAutor());
            txtdecripcion.setText(libro.getDescripcion());

            String rutaCompletaPdf = libro.getRutaPdf();


            File archivoPdf = new File(rutaCompletaPdf);


            String nombrePdf = archivoPdf.getName();


            button1.setText("Nombre del PDF: " + nombrePdf);

             try {
                ImageIcon imageIcon = new ImageIcon(libro.getImagenR());

                 int labelWidth = imagenurl.getWidth() > 0 ? imagenurl.getWidth() : 200;
                 int labelHeight = imagenurl.getHeight() > 0 ? imagenurl.getHeight() : 200;

                 Image image = imageIcon.getImage().getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
                 imagenurl.setIcon(new ImageIcon(image));
                 imagenurl.setText(""); // Limpiar el texto si se establece un icono
             } catch (Exception e) {
                 imagenurl.setText("Error al cargar la imagen: " + e.getMessage());
                 // Manejar la excepción, por ejemplo, mostrar una imagen predeterminada o un mensaje de error
             }

             Categoria cat1 = libroDA0.getByIdC(libro.getCategoriaId());

             boxcategoria.addItem(cat1);

           System.out.println(libro);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cargarCategoriasEnComboBox();
        add(libropanel);
    }

    private void cargarCategoriasEnComboBox() {
        // Limpiar el ComboBox antes de añadir nuevos ítems

        libroDA0 = new LibroDAO();

        try {
            // Suponiendo que tienes una instancia de tu clase de persistencia (ej. CategoriaDAO)
            // Necesitas instanciar tu CategoriaDAO aquí
            // Por ejemplo:
            // CategoriaDAO categoriaDAO = new CategoriaDAO();
            // ArrayList<Categoria> categorias = categoriaDAO.selectCategoria();

            // Para este ejemplo, simulemos algunas categorías si no tienes el DAO configurado aún

            // Añadir los objetos Categoria al JComboBox
            for (Categoria cat : libroDA0.selectCategoria()) {
                boxcategoria.addItem(cat);
            }


        } catch (Exception ex) { // Captura SQLException o cualquier otra excepción del DAO
            System.err.println("Error al cargar las categorías en el ComboBox: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
