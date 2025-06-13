package Biblioteca.presentacion;

import javax.swing.*;
import java.awt.*;

public class Mainform extends JFrame{
    private JPanel Mainpanel;

    public Mainform() {
        setTitle("Sistema en java de escritorio"); // Establece el título de la ventana principal (JFrame).
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configura la operación por defecto al cerrar la ventana para que la aplicación se termine.
        setLocationRelativeTo(null); // Centra la ventana principal en la pantalla.
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(Mainpanel);
        setVisible(true);// Inicializa la ventana principal en estado maximizado, ocupando toda la pantalla.
        crearMenu();
    }

    public static void main(String[] args) {
        // Ejecutar la GUI en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new Mainform();
        });
    }

    private void crearMenu(){
        // 1. Crear la barra de menú
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);


        // 2. Crear los menús principales
        JMenu menuArchivo = new JMenu("Archivo");
        menuBar.add(menuArchivo);


        JMenuItem itemOpcion1 = new JMenuItem("Clientes");
        menuArchivo.add(itemOpcion1);
        itemOpcion1.addActionListener(s-> {
            libroformC clienteForm = new libroformC(this);
            clienteForm.setVisible(true);
        });



        JMenuItem itemSalir = new JMenuItem("Salir");
        menuArchivo.add(itemSalir);
        itemSalir.addActionListener(s-> System.exit(0));






    }


}
