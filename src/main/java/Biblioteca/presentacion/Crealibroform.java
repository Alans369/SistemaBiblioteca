package Biblioteca.presentacion;

import javax.swing.*;

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
        //setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setVisible(true); // Hace visible la ventana
    }





    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Crealibroform();
        });
    }







}
