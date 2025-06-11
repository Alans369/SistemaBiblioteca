package Biblioteca.presentacion;

import javax.swing.*;
import java.awt.event.WindowAdapter; // Importa la clase WindowAdapter desde el paquete java.awt.event. WindowAdapter es una clase abstracta que implementa la interfaz WindowListener y proporciona implementaciones vacías para sus métodos. Se utiliza para extenderla y solo sobrescribir los métodos de los eventos de ventana que nos interesan.
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Crealibroform extends JFrame {
    private JPanel mainpanel;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JButton btnImagen;
    private JTextField txtDescripcion;
    private JButton txtlibro;
    private JComboBox categoriaBox;

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
        // Implementación de la lógica para seleccionar y mostrar la imagen
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona una imagen");

        // Opcional: Filtrar solo archivos de imagen
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de Imagen", "jpg", "jpeg", "png", "gif", "bmp"));

        int userSelection = fileChooser.showOpenDialog(this); // 'this' se refiere a tu JFrame actual

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Aquí iría la lógica para cargar la imagen (como mi método loadImage())
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
            // Llama a tu método para cargar y mostrar la imagen aquí
            // Por ejemplo: loadImage(selectedFile);
        } else if (userSelection == JFileChooser.CANCEL_OPTION) {
            System.out.println("Selección de archivo cancelada por el usuario.");
        }


    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Crealibroform();
        });
    }




}
