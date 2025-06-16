package Biblioteca.presentacion;

import Biblioteca.dominio.Categoria;
import Biblioteca.dominio.Libro;
import Biblioteca.persistencia.LibroDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class libroformR extends JDialog{
    private JButton crearLibroButton;
    private JTextField textField1;
    private JScrollPane table;
    private JPanel mainpanel;
    private JTable table1;
    private JButton button2;
    private LibroDAO libroDAO;
    private Mainform mainFrameReference;
    private Categoria cat;

    // Lista para almacenar los objetos Libro
    private List<Libro> libros;

    public libroformR(Mainform mainForm) {
        super(mainForm, "Actualizar Libro ", true); // true para modal
        setSize(700, 700);
        setLocationRelativeTo(mainForm); // Centra el diálogo respecto a su propietario
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        add(mainpanel);
        this.mainFrameReference = mainForm;

        textField1.addKeyListener(new KeyAdapter() {
            // Sobrescribe el método keyReleased, que se llama cuando se suelta una tecla.
            @Override
            public void keyReleased(KeyEvent e) {
                // Verifica si el campo de texto txtNombre no está vacío.
                if (!textField1.getText().trim().isEmpty()) {

                    cargarDatosEjemplo(textField1.getText());
                } else {
                    // Si el campo de texto está vacío, crea un modelo de tabla vacío y lo asigna a la tabla de usuarios para limpiarla.
                   System.out.println("vacio");
                }
            }
        });
        // Inicializar lista de libros
        libros = new ArrayList<>();

        crearLibroButton.addActionListener(e->
        {libroformC libroFormu = new libroformC(mainFrameReference);
        libroFormu.setVisible(true);
        }
        );

        // Configurar la tabla
        setupLibrosTable();

        cargarDatosEjemplo();

        // Cargar datos de ejemplo


        setVisible(true);
    }



    private void setupLibrosTable() {
        // Crear modelo no editable
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo las columnas de botones son "editables" (clickeables)
                return column == 4 || column == 5; // Columnas de Detalles y Borrar
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 1: // Columna de imagen
                        return ImageIcon.class;
                    case 4:
                    case 5:
                        return JButton.class;
                    default:
                        return String.class;
                }
            }
        };

        // Asignar modelo a la tabla
        table1.setModel(model);

        // Añadir columnas basadas en tu estructura de BD
        model.addColumn("Título");
        model.addColumn("Imagen");
        model.addColumn("Archivo PDF");
        model.addColumn("Categoría ");
        model.addColumn("Editar");
        model.addColumn("Borrar");

        // Configurar altura de filas para mostrar imágenes
        table1.setRowHeight(80);

        // Configurar renderizador para imágenes
        table1.getColumn("Imagen").setCellRenderer(new ImageRenderer());

        // Configurar renderizadores y editores para botones
        table1.getColumn("Editar").setCellRenderer(new ButtonRenderer("✏️"));
        table1.getColumn("Editar").setCellEditor(new ButtonEditor(new JCheckBox(), "✏️"));

        table1.getColumn("Borrar").setCellRenderer(new ButtonRenderer("🗑️"));
        table1.getColumn("Borrar").setCellEditor(new ButtonEditor(new JCheckBox(), "🗑️"));

        // Ajustar anchos de columnas
        table1.getColumnModel().getColumn(0).setPreferredWidth(50); // Título
        table1.getColumnModel().getColumn(1).setPreferredWidth(120); // Imagen
        table1.getColumnModel().getColumn(2).setPreferredWidth(80); // PDF
        table1.getColumnModel().getColumn(3).setPreferredWidth(40);  // Categoría
        table1.getColumnModel().getColumn(4).setPreferredWidth(50);  // Detalles
        table1.getColumnModel().getColumn(5).setPreferredWidth(50);  // Borrar

        // Configurar selección
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Listener para clicks en la tabla (filas, no botones)
        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table1.rowAtPoint(evt.getPoint());
                int col = table1.columnAtPoint(evt.getPoint());

                // Solo procesar clicks que no sean en las columnas de botones
                if (row >= 0 && col < 4) {
                    System.out.println("Click en fila: " + row + ", columna: " + col);
                    // Aquí puedes agregar lógica para mostrar información del libro
                    mostrarInfoLibro(row);
                }
            }
        });
    }

    private void cargarDatosEjemplo() {

        // Crear libros de ejemplo con rutas reales de tu proyecto


        libroDAO = new LibroDAO();

        try {

            for (Libro libro : libroDAO.select()) {
                libros.add(libro);
            }


        } catch (Exception ex) { // Captura SQLException o cualquier otra excepción del DAO
            System.err.println("Error al cargar las categorías en el ComboBox: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Actualizar tabla
        actualizarTabla();
    }

    private void cargarDatosEjemplo(String titulo) {

        libroDAO = new LibroDAO();
        libros.clear();


        try {

            for (Libro libro : libroDAO.select(titulo)) {
                libros.add(libro);
            }


        } catch (Exception ex) { // Captura SQLException o cualquier otra excepción del DAO
            System.err.println("Error al cargar las categorías en el ComboBox: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Actualizar tabla
        actualizarTabla();
    }

    private void actualizarTabla() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();

        model.setRowCount(0);


        // Agregar libros
        for (Libro libro : libros) {
            // Cargar imagen del libro o imagen por defecto


            libroDAO = new LibroDAO();

            try {
                cat  = libroDAO.getByIdC(libro.getCategoriaId());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String rutaCompletaPdf = libro.getRutaPdf();


            File archivoPdf = new File(rutaCompletaPdf);


            String nombrePdf = archivoPdf.getName();

            ImageIcon imagenLibro = cargarImagenLibro(libro.getImagenR());
            model.addRow(new Object[]{
                    libro.getTitulo(),
                    imagenLibro, // Ahora es un ImageIcon en lugar de String
                    nombrePdf,
                    cat,
                    "✏️",
                    "🗑️",
            });
        }
    }

    private void mostrarInfoLibro(int row) {
        if (row >= 0 && row < libros.size()) {
            Libro libro = libros.get(row);
            String info = String.format(
                    "INFORMACIÓN DEL LIBRO\n\n" +
                            "Título: %s\n" +
                            "Autor: %s\n" +
                            "Descripción: %s\n" +
                            "Categoría ID: %d",
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getDescripcion(),
                    libro.getCategoriaId()
            );

            JOptionPane.showMessageDialog(this, info, "Información del Libro",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarDetalles(int row) {
        if (row >= 0 && row < libros.size()) {
            Libro libro = libros.get(row);

            libroformU libroFormu = new libroformU(mainFrameReference,libro.getId());
            libroFormu.setVisible(true);
        }
    }

    private void borrarLibro(int row) {
        if (row >= 0 && row < libros.size()) {
            Libro libro = libros.get(row);

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea borrar el libro:\n" + libro.getTitulo() + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                   if( libroDAO.delete(libro)){
                       libros.remove(row);
                       actualizarTabla();
                       JOptionPane.showMessageDialog(this, "Libro eliminado '"+libro.getTitulo()+"' correctamente.",
                               "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                   }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    // Renderer para los botones
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setOpaque(true);
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            return this;
        }
    }

    // Editor para los botones
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, String buttonText) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
            this.label = buttonText;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.currentRow = row;
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                if (label.equals("✏️")) {
                    mostrarDetalles(currentRow);
                } else if (label.equals("🗑️")) {
                    borrarLibro(currentRow);
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    /**
     * Método para cargar imagen del libro con imagen por defecto
     */
    private ImageIcon cargarImagenLibro(String rutaImagen) {
        // Si no hay ruta de imagen o está vacía, usar imagen por defecto
        String normalizedPath = normalizePath(rutaImagen);

        String extractedPath = extractPathFromImages(normalizedPath);
        System.out.println(extractedPath);
        if (extractedPath == null || extractedPath.trim().isEmpty()) {
            return crearImagenPorDefecto(70, 70);
        }

        // Intentar cargar la imagen específica
        ImageIcon imagen = loadImageIcon(extractedPath, 70, 70);

        // Si no se pudo cargar, usar imagen por defecto
        if (imagen == null) {
            return crearImagenPorDefecto(70, 70);
        }

        return imagen;
    }

    public static String normalizePath(String path) {
        return path.replace("\\", "/");
    }


    public static String extractPathFromImages(String fullPath) {
        int indexOfImages = fullPath.indexOf("/images");
        if (indexOfImages != -1) {
            return fullPath.substring(indexOfImages);
        } else {
            return null; // Or throw an exception, or return an empty string, depending on desired behavior
        }
    }

    /**
     * Método para cargar imágenes desde resources con redimensionamiento
     */
    private ImageIcon loadImageIcon(String path, int width, int height) {
        try {
            // Intentar cargar desde resources
            URL imageURL = getClass().getResource(path);

            if (imageURL != null) {
                ImageIcon originalIcon = new ImageIcon(imageURL);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            } else {
                System.err.println("No se pudo encontrar la imagen: " + path);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen " + path + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Crear una imagen por defecto para libros sin imagen
     */
    private ImageIcon crearImagenPorDefecto(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // Configurar renderizado de calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo con gradiente azul claro
        GradientPaint gradient = new GradientPaint(0, 0, new Color(173, 216, 230),
                width, height, new Color(135, 206, 250));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Borde
        g2d.setColor(new Color(70, 130, 180));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(1, 1, width-2, height-2);

        // Dibujar un libro estilizado
        g2d.setColor(Color.WHITE);
        int bookWidth = width / 3;
        int bookHeight = height / 2;
        int bookX = (width - bookWidth) / 2;
        int bookY = (height - bookHeight) / 2;

        g2d.fillRect(bookX, bookY, bookWidth, bookHeight);
        g2d.setColor(new Color(70, 130, 180));
        g2d.drawRect(bookX, bookY, bookWidth, bookHeight);

        // Líneas del libro
        for (int i = 1; i < 4; i++) {
            g2d.drawLine(bookX + 3, bookY + (i * bookHeight / 4),
                    bookX + bookWidth - 3, bookY + (i * bookHeight / 4));
        }

        // Texto "LIBRO"
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        FontMetrics fm = g2d.getFontMetrics();
        String texto = "LIBRO";
        int textWidth = fm.stringWidth(texto);
        int textX = (width - textWidth) / 2;
        int textY = bookY + bookHeight + 15;

        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, textX, textY);

        g2d.dispose();
        return new ImageIcon(img);
    }


}