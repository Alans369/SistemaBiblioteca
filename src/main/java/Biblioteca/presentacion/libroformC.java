package Biblioteca.presentacion;

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

import Biblioteca.dominio.Libro;

public class libroformC extends JDialog {
    private JPanel libropanel;
    private JTextField txtdecripcion;
    private JComboBox boxcategoria;
    private JButton btnlibro;
    private JTextField txtTitulo;
    private JTextField txtautor;
    private JButton btnimagen;
    private JButton button1;
    private JButton crearButton;
    private JButton cancelarButton;
    private JLabel imagenurl;
    private String rutaImagenCargada;

    private String rutaImagenTemporal; // ALMACENA LA RUTA ORIGINAL DE LA IMAGEN SELECCIONADA (NO COPIADA AÚN)
    // ALMACENA LA RUTA FINAL DE LA IMAGEN EN 'resources' DESPUÉS DE COPIARLA


    // Define las dimensiones máximas para la imagen escalada en la vista previa



    public libroformC(Mainform mainForm){
        super(mainForm, "Crear libro ", true); // true para modal
        setSize(700, 700);
        setLocationRelativeTo(mainForm); // Centra el diálogo respecto a su propietario
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        add(libropanel);

        // Add action listener to btnimagen
        // ACCIÓN PARA EL BOTÓN "SELECCIONAR IMAGEN" (btnimagen)
        // Solo selecciona y muestra la vista previa, SIN COPIAR AÚN
        // =========================================================================
        btnimagen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Llama al método para seleccionar la imagen
                String tempPath = seleccionarImagen();
                if (tempPath != null) {
                    rutaImagenTemporal = tempPath; // Guarda la ruta temporal
                    try {
                        // Muestra la imagen seleccionada en el JLabel para vista previa
                        mostrarImagenEnLabel(rutaImagenTemporal);
                        JOptionPane.showMessageDialog(libroformC.this, "Imagen seleccionada para vista previa.");
                    } catch (Exception ex) { // Captura cualquier excepción al cargar la imagen para vista previa
                        JOptionPane.showMessageDialog(libroformC.this, "Error al mostrar la imagen de vista previa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        crearButton.addActionListener(e-> CraerLibro());

    } // Cierra solo el diálogo


    public void CraerLibro() {

        try{
            Libro libro = new Libro();
            libro.setTitulo(txtTitulo.getText());
            libro.setAutor(txtautor.getText());
            libro.setImagenR(cargarImagen(rutaImagenTemporal));

            libro.setDescripcion(txtdecripcion.getText());

            System.out.println(libro);

        }catch (IOException ex) {
            JOptionPane.showMessageDialog(libroformC.this, "Error de I/O al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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

    private String seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Imagen");

        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Archivos de Imagen", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(imageFilter);

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null; // User cancelled
    }

    private String cargarImagen(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            return null;
        }

        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            System.err.println("El archivo de origen no existe para copiar: " + sourcePath);
            return null;
        }

        Path resourceDirPath = Paths.get("src", "main", "resources", "images"); // Sugerencia: subcarpeta 'images'
        if (!Files.exists(resourceDirPath)) {
            Files.createDirectories(resourceDirPath);
            System.out.println("Carpeta de recursos para imágenes creada: " + resourceDirPath.toAbsolutePath());
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
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING); // Omitir REPLACE_EXISTING si siempre quieres nombres únicos
            System.out.println("Imagen copiada a: " + destinationPath.toAbsolutePath());
            return destinationPath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Error al copiar el archivo: " + e.getMessage());
            throw e;
        }
    }








}
