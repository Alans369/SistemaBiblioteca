package Biblioteca.presentacion;

import Biblioteca.dominio.Categoria;

import Biblioteca.dominio.Libro;

import Biblioteca.persistencia.LibroDAO;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    private String rutaPdfTemporal;    // ALMACENA LA RUTA ORIGINAL DEL PDF SELECCIONADO (NO COPIADO AÚN)

    private String rutaImagenTemporal;

    private Libro librotemporal ;


    public libroformU(Mainform mainForm,int libroid)  {
        super(mainForm, "Actualizar Libro ", true); // true para modal
        setSize(700, 700);
        setLocationRelativeTo(mainForm); // Centra el diálogo respecto a su propietario
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        libroDA0 = new LibroDAO();
        try {
           Libro libro = libroDA0.getById(libroid);

           librotemporal = libro;
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

        btnimagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Llama al método para seleccionar la imagen
                String tempPath = seleccionarArchivo(new FileNameExtensionFilter("Archivos de Imagen", "jpg", "jpeg", "png", "gif", "bmp"));
                if (tempPath != null) {
                    rutaImagenTemporal = tempPath; // Guarda la ruta temporal
                    try {
                        // Muestra la imagen seleccionada en el JLabel para vista previa
                        mostrarImagenEnLabel(rutaImagenTemporal);
                        JOptionPane.showMessageDialog(libroformU.this, "Imagen seleccionada para vista previa.");
                    } catch (Exception ex) { // Captura cualquier excepción al cargar la imagen para vista previa
                        JOptionPane.showMessageDialog(libroformU.this, "Error al mostrar la imagen de vista previa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                        rutaImagenTemporal = null; // Limpia la ruta temporal si hay un error
                        imagenurl.setIcon(null);
                        imagenurl.setText("Error al cargar vista previa");
                    }
                } else {
                    // Si el usuario cancela, limpia la vista previa y la ruta temporal
                    rutaImagenTemporal = null;
                    imagenurl.setIcon(null);
                    imagenurl.setText("Vista Previa de la Imagen");
                }
            }
        });

        // =========================================================================
        // ACCIÓN PARA EL BOTÓN "SELECCIONAR PDF" (btnSeleccionarPdf)
        // =========================================================================
        btnlibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Llama a una versión genérica de seleccionarArchivo, filtrando solo PDFs
                String tempPath = seleccionarArchivo(new FileNameExtensionFilter("Archivos PDF", "pdf"));
                if (tempPath != null) {
                    rutaPdfTemporal = tempPath; // Guarda la ruta temporal del PDF
                    File pdfFile = new File(rutaPdfTemporal);
                    button1.setText("PDF Seleccionado: " + pdfFile.getName()); // Muestra solo el nombre
                    JOptionPane.showMessageDialog(libroformU.this, "PDF seleccionado temporalmente: " + pdfFile.getName());
                } else {
                    rutaPdfTemporal = null; // Si el usuario cancela, limpia la ruta
                    button1.setText("PDF Seleccionado: Ninguno"); // Restablece el texto
                }
            }
        });

        crearButton.addActionListener(e-> UpdateLibro(librotemporal));
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

    public void UpdateLibro(Libro libro)  {

        try{
            Categoria categoriaSeleccionada = (Categoria) boxcategoria.getSelectedItem();
            int categoriaId = categoriaSeleccionada.getCategoriaID();

            libro.setTitulo(txtTitulo.getText());
            libro.setAutor(txtautor.getText());
            libro.setImagenR(copiarArchivo(rutaImagenTemporal,"images"));
            libro.setDescripcion(txtdecripcion.getText());
            libro.setRutaPdf(copiarArchivo(rutaPdfTemporal,"pdfs"));
            libro.setCategoriaId(categoriaId);
            System.out.println(libro);

           if (libroDA0.update(libro)) {
               JOptionPane.showMessageDialog(libroformU.this, "Actualizado correctamente: " , "Error", JOptionPane.ERROR_MESSAGE);
           }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(libroformU.this, "Error de I/O al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void mostrarImagenEnLabel(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imagenurl.setIcon(null);
            imagenurl.setText("Vista Previa de la Imagen");
            return;
        }

        ImageIcon originalIcon = new ImageIcon(imagePath);

        // Verificar si la imagen se cargó correctamente en el ImageIcon
        if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image image = originalIcon.getImage();
            int originalWidth = image.getWidth(null);
            int originalHeight = image.getHeight(null);

            // Evitar división por cero si la imagen es inválida o de tamaño 0
            if (originalWidth <= 0 || originalHeight <= 0) {
                imagenurl.setIcon(null);
                imagenurl.setText("Imagen inválida");
                System.err.println("Imagen con dimensiones inválidas: " + imagePath);
                return;
            }


            int labelWidth = imagenurl.getWidth() > 0 ? imagenurl.getWidth() : 200;
            int labelHeight = imagenurl.getHeight() > 0 ? imagenurl.getHeight() : 300;

            Image scaledImage = image.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            imagenurl.setIcon(scaledIcon);
            imagenurl.setText(""); // Limpiar cualquier texto al mostrar la imagen
        } else {
            imagenurl.setIcon(null);
            imagenurl.setText("Error al cargar imagen");
            System.err.println("No se pudo cargar la imagen para vista previa: " + imagePath);
        }
    }

    private String seleccionarArchivo(FileNameExtensionFilter filter) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Archivo");
        fileChooser.setFileFilter(filter);
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    private String copiarArchivo(String sourcePath, String subfolder) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            return null;
        }

        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            System.err.println("El archivo de origen no existe para copiar: " + sourcePath);
            return null;
        }

        // Ruta a la subcarpeta dentro de 'resources'
        Path resourceDirPath = Paths.get("src", "main", "resources", subfolder);
        if (!Files.exists(resourceDirPath)) {
            Files.createDirectories(resourceDirPath);
            System.out.println("Carpeta de recursos creada: " + resourceDirPath.toAbsolutePath());
        }

        // Generar un nombre de archivo único para evitar sobrescribir
        String fileName = sourceFile.getName();
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            fileExtension = fileName.substring(dotIndex);
            fileName = fileName.substring(0, dotIndex);
        }
        String uniqueFileName = fileName + "_" + System.currentTimeMillis() + fileExtension;


        Path destinationPath = resourceDirPath.resolve(uniqueFileName);

        try {
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo copiado a: " + destinationPath.toAbsolutePath());
            return destinationPath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Error al copiar el archivo: " + e.getMessage());
            throw e;
        }
    }
}
