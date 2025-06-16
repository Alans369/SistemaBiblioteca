package Biblioteca.presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VistaAdmin extends JFrame {
    private JButton botonLibros;
    private JButton btnCerrar;
    private Mainform father;

    // Constructor que recibe el frame padre
    public VistaAdmin() {
        initComponents();
        setupLayout();
        setupEvents();
        configureDialog();

    }

    // Inicializar componentes
    private void initComponents() {
        // Crear el icono personalizado de libro
        Icon iconoLibro = crearIconoLibro();

        // Crear el botón con icono
        botonLibros = new JButton("Inventario", iconoLibro);
        botonLibros.setPreferredSize(new Dimension(150, 60));
        botonLibros.setFont(new Font("Arial", Font.BOLD, 14));
        botonLibros.setBackground(new Color(70, 130, 180));
        botonLibros.setForeground(Color.WHITE);
        botonLibros.setOpaque(true);
        botonLibros.setBorderPainted(false);
        botonLibros.setFocusPainted(false);
        botonLibros.setHorizontalTextPosition(SwingConstants.RIGHT);
        botonLibros.setVerticalTextPosition(SwingConstants.CENTER);
        botonLibros.setIconTextGap(10);

        // Botón cerrar
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setPreferredSize(new Dimension(100, 30));
    }

    // Configurar el layout
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
        panelPrincipal.setBackground(new Color(245, 245, 250));
        panelPrincipal.add(botonLibros);

        // Panel inferior
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(new Color(245, 245, 250));
        panelInferior.add(btnCerrar);

        // Agregar paneles al dialog
        add(panelPrincipal, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // Configurar eventos
    private void setupEvents() {
        // Evento del botón Libros
        botonLibros.addActionListener(this::onLibrosButtonClick);

        // Efecto hover para el botón Libros
        botonLibros.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botonLibros.setBackground(new Color(100, 149, 237));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botonLibros.setBackground(new Color(70, 130, 180));
            }
        });

        // Evento del botón Cerrar
        btnCerrar.addActionListener(e -> dispose());
    }

    // Configurar propiedades del dialog
    private void configureDialog() {
        setSize(300, 200);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // Método que maneja el evento del botón Libros
    private void onLibrosButtonClick(ActionEvent e) {
        libroformR libroFormr = new libroformR(new Mainform());
        libroFormr.setVisible(true);
    }

    // Método para crear el icono personalizado de libro
    private Icon crearIconoLibro() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Libro cerrado - tapa
                g2d.setColor(new Color(139, 69, 19)); // Marrón
                g2d.fillRoundRect(x + 2, y + 2, 20, 16, 2, 2);

                // Páginas del libro
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x + 3, y + 3, 18, 14, 1, 1);

                // Líneas de las páginas
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(0.5f));
                for (int i = 0; i < 4; i++) {
                    g2d.drawLine(x + 5, y + 6 + i * 2, x + 19, y + 6 + i * 2);
                }

                // Lomo del libro
                g2d.setColor(new Color(101, 67, 33)); // Marrón más oscuro
                g2d.fillRect(x + 1, y + 2, 3, 16);

                // Título en el lomo (líneas decorativas)
                g2d.setColor(new Color(218, 165, 32)); // Dorado
                g2d.fillRect(x + 1, y + 4, 3, 1);
                g2d.fillRect(x + 1, y + 7, 3, 1);
                g2d.fillRect(x + 1, y + 14, 3, 1);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return 24;
            }

            @Override
            public int getIconHeight() {
                return 20;
            }
        };
    }

    // Métodos adicionales que podrías implementar
    private void abrirVentanaListaLibros() {
        // Implementar apertura de ventana de lista de libros
    }

    private void cargarDatosLibros() {
        // Implementar carga de datos desde base de datos
    }

    private void navegarASeccionLibros() {
        // Implementar navegación a otra sección
    }
}
