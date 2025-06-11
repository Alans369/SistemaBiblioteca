package Biblioteca.presentacion;

import javax.swing.*;
import java.awt.event.WindowAdapter; // Importa la clase WindowAdapter desde el paquete java.awt.event. WindowAdapter es una clase abstracta que implementa la interfaz WindowListener y proporciona implementaciones vacías para sus métodos. Se utiliza para extenderla y solo sobrescribir los métodos de los eventos de ventana que nos interesan.
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Image; // <-- ¡Añade esta línea!

public class Crealibroform extends JFrame {
    private JPanel mainpanel;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JButton btnImagen;
    private JTextField txtDescripcion;
    private JButton txtlibro;
    private JComboBox categoriaBox;
    private JLabel imagen;

    public Crealibroform(){
        setTitle("Mi Aplicación Principal");
        // *** Línea clave para maximizar la ventana ***
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setContentPane(mainpanel);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setVisible(true); // Hace visible la ventan
        btnImagen.addActionListener(e -> selectImage());



    }
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar una imagen");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de Imagen", "jpg", "jpeg", "png", "gif", "bmp"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Llama al método para cargar y mostrar la imagen en tu JLabel 'imagen'
            loadImageAndDisplay(selectedFile);
        }
    }

    // Método para cargar y mostrar la imagen en el JLabel 'imagen'
    private void loadImageAndDisplay(File file) {
        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                try {
                    return ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    BufferedImage originalImage = get();
                    if (originalImage != null) {
                        // Obtén el tamaño actual del JLabel 'imagen'
                        int labelWidth = imagen.getWidth();
                        int labelHeight = imagen.getHeight();

                        // Si el label aún no tiene dimensiones (ej. al inicio), usa las preferidas o un valor predeterminado
                        if (labelWidth == 0 || labelHeight == 0) {
                            labelWidth = imagen.getPreferredSize().width > 0 ? imagen.getPreferredSize().width : 500;
                            labelHeight = imagen.getPreferredSize().height > 0 ? imagen.getPreferredSize().height : 400;
                        }

                        // Asegúrate de que las dimensiones no sean 0 para evitar errores
                        if (labelWidth <= 0) labelWidth = 1;
                        if (labelHeight <= 0) labelHeight = 1;

                        // Calcula las nuevas dimensiones manteniendo la relación de aspecto
                        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
                        int newWidth = labelWidth;
                        int newHeight = (int) (newWidth / aspectRatio);

                        if (newHeight > labelHeight) {
                            newHeight = labelHeight;
                            newWidth = (int) (newHeight * aspectRatio);
                        }

                        // Escala la imagen
                        Image scaledImage = originalImage.getScaledInstance(
                                newWidth, newHeight, Image.SCALE_SMOOTH);

                        // Establece la imagen escalada en tu JLabel 'imagen'
                        imagen.setIcon(new ImageIcon(scaledImage));
                        imagen.setText(null); // Borra cualquier texto anterior
                    } else {
                        imagen.setIcon(null);
                        imagen.setText("No se pudo cargar la imagen.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    imagen.setIcon(null);
                    imagen.setText("Error al procesar la imagen seleccionada.");
                }
                // Solicita redibujar el componente y su contenedor
                imagen.revalidate();
                imagen.repaint();
                mainpanel.revalidate(); // Revalidar el panel principal
                mainpanel.repaint();    // Repintar el panel principal
                repaint();              // Repintar la ventana completa
            }
        }.execute();
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Crealibroform();
            ff
        });
    }




}
