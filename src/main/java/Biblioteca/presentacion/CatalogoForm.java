package Biblioteca.presentacion;

import Biblioteca.dominio.Categoria;
import Biblioteca.dominio.Libro;
import Biblioteca.persistencia.LibroDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
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



public class CatalogoForm extends JDialog {
    private JPanel Mainpanel;
    private JTable table1;
    private JTextField textField1;
    private JButton button1;
    private JComboBox comboBox2;

    private LibroDAO libroDAO;
    private JLabel textLabel;
    private Categoria cat1;
    private Mainform mainFrameReference;


    private List<Libro> libros;


    public CatalogoForm(Mainform mainForm){
        super(mainForm, "Buscar Libro ", true); // true para modal
        setSize(700, 700);
        setLocationRelativeTo(mainForm); // Centra el diálogo respecto a su propietario
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.mainFrameReference = mainForm;
        add(Mainpanel);
        setupLibrosTable();
        cargarCategoriasEnComboBox();

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
                System.out.println();
            }
        });
        libros = new ArrayList<>();

        button1.addActionListener(e->System.exit(0));




        comboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Este código se ejecutará cada vez que el usuario cambie la selección
                // y la acción sea "finalizada" (ej. al soltar el clic después de elegir)

                // Obtener el ítem seleccionado
                Categoria categoriaSeleccionada = (Categoria) comboBox2.getSelectedItem();
                int categoriaId = categoriaSeleccionada.getCategoriaID();
                cargarDatosEjemplo(categoriaId);
                // Aquí puedes poner la lógica que quieres ejecutar
                // por ejemplo, filtrar la tabla, cargar nuevos datos, etc.
                // Ejemplo: filtrarTablaPorCategoria(selectedItem);
            }
        });

    }


    private void setupLibrosTable() {
        // Crear modelo no editable
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo las columnas de botones son "editables" (clickeables)
                return column == 2 || column == 3; // Columnas de Detalles y Borrar
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: // Columna de imagen
                        return ImageIcon.class;
                    case 2:
                    case 3:
                        return JButton.class;
                    default:
                        return String.class;
                }
            }
        };

        // Asignar modelo a la tabla
        table1.setModel(model);

        // Añadir columnas basadas en tu estructura de BD

        model.addColumn("Libro");
        model.addColumn("----------------------");
        model.addColumn("Categoría");
       // model.addColumn("⛽");


        // Configurar altura de filas para mostrar imágenes
        table1.setRowHeight(120);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);





        // Configurar renderizador para imágenes
        table1.getColumn("Libro").setCellRenderer(new ImageTextCellRenderer()); // Usa tu nuevo renderizador aquí
        table1.getColumn("----------------------").setCellRenderer(new MultiLineTextCellRenderer());
        table1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);




        // Configurar renderizadores y editores para botones
        //table1.getColumn("⛽").setCellRenderer(new CatalogoForm.ButtonRenderer("Detalles"));
        //table1.getColumn("⛽").setCellEditor(new CatalogoForm.ButtonEditor(new JCheckBox(), "Detalles"));



        // Ajustar anchos de columnas

        table1.getColumnModel().getColumn(0).setPreferredWidth(120); // Imagen
        table1.getColumnModel().getColumn(1).setPreferredWidth(300); // PDF
        table1.getColumnModel().getColumn(2).setPreferredWidth(100);  // Categoría



        // Configurar selección
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        // Listener para clicks en la tabla (filas, no botones)
        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table1.rowAtPoint(evt.getPoint());
                int col = table1.columnAtPoint(evt.getPoint());
                table1.setSelectionBackground(Color.WHITE);

                table1.setShowVerticalLines(false);

                // Solo procesar clicks que no sean en las columnas de botones
                if (row >= 0 && col < 4) {
                    System.out.println("Click en fila: " + row + ", columna: " + col);
                    // Aquí puedes agregar lógica para mostrar información del libro
                    mostrarInfoLibro(row);
                }
            }
        });
    }

    private void cargarCategoriasEnComboBox() {
        // Limpiar el ComboBox antes de añadir nuevos ítems
        libroDAO = new LibroDAO();

        try {
            // Suponiendo que tienes una instancia de tu clase de persistencia (ej. CategoriaDAO)
            // Necesitas instanciar tu CategoriaDAO aquí
            // Por ejemplo:
            // CategoriaDAO categoriaDAO = new CategoriaDAO();
            // ArrayList<Categoria> categorias = categoriaDAO.selectCategoria();

            // Para este ejemplo, simulemos algunas categorías si no tienes el DAO configurado aún

            // Añadir los objetos Categoria al JComboBox
            for (Categoria cat : libroDAO.selectCategoria()) {
                comboBox2.addItem(cat);
            }


        } catch (Exception ex) { // Captura SQLException o cualquier otra excepción del DAO
            System.err.println("Error al cargar las categorías en el ComboBox: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private void cargarDatosEjemplo(int categoryId) {

        libroDAO = new LibroDAO();
        libros.clear();


        try {

            for (Libro libro : libroDAO.searchByCategory(categoryId)) {
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

        System.out.println(libros.size());


        // Agregar libros
        for (Libro libro : libros) {
            // Cargar imagen del libro o imagen por defecto
            ImageIcon imagenLibro = cargarImagenLibro(libro.getImagenR());

            try {
                cat1 = libroDAO.getByIdC(libro.getCategoriaId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Object[] imagenConTexto = new Object[]{
                    libro.getTitulo(), // El texto que aparecerá encima de la imagen
                    imagenLibro
            };

            model.addRow(new Object[]{
                    imagenConTexto, // Ahora es un ImageIcon en lugar de String
                    libro.getDescripcion(),
                    cat1.getNombreCategoria(),


            });
        }
    }

    private ImageIcon cargarImagenLibro(String rutaImagen) {
        // Si no hay ruta de imagen o está vacía, usar imagen por defecto
        String normalizedPath = normalizePath(rutaImagen);

        String extractedPath = extractPathFromImages(normalizedPath);
        System.out.println(extractedPath);
        if (extractedPath == null || extractedPath.trim().isEmpty()) {
            return crearImagenPorDefecto(70, 70);
        }

        // Intentar cargar la imagen específica
        ImageIcon imagen = loadImageIcon(extractedPath, 90, 100);

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
                if (label.equals("Detalles")) {
                    mostrarDetalles(currentRow);
                } else if (label.equals("Borrar")) {
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
    private void mostrarInfoLibro(int row) {
        if (row >= 0 && row < libros.size()) {
            Libro libro = libros.get(row);
            File pdfFile = new File(libro.getRutaPdf());
            if (pdfFile.exists()) {
                new PDFViewer(mainFrameReference,libro.getRutaPdf()).setVisible(true);
            } else {
                System.err.println("El archivo PDF no existe en la ruta: " + libro.getRutaPdf());
                // Abrir sin PDF inicial
                new PDFViewer(mainFrameReference,null).setVisible(true);
            }
        }
    }

    private void mostrarDetalles(int row) {
        if (row >= 0 && row < libros.size()) {
            Libro libro = libros.get(row);

            File pdfFile = new File(libro.getRutaPdf());
            if (pdfFile.exists()) {
                new PDFViewer(mainFrameReference,libro.getRutaPdf()).setVisible(true);
            } else {
                System.err.println("El archivo PDF no existe en la ruta: " + libro.getRutaPdf());
                // Abrir sin PDF inicial
                new PDFViewer(mainFrameReference,null).setVisible(true);
            }


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
                libros.remove(row);
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.",
                        "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public class ImageTextCellRenderer extends JPanel implements TableCellRenderer {

        private JLabel combinedLabel; // Usaremos un solo JLabel para texto e icono

        public ImageTextCellRenderer() {
            // Usamos un BorderLayout para tener el icono en el centro y el texto arriba si quisiéramos
            // Pero para el texto encima de la imagen en un solo componente, un JLabel es más simple.
            // O si quieres apilarlos, BoxLayout.Y_AXIS sería bueno.
            setLayout(new BorderLayout());
            setOpaque(true); // Muy importante para que el fondo se pinte correctamente

            combinedLabel = new JLabel();
            combinedLabel.setHorizontalAlignment(JLabel.CENTER); // Centrar horizontalmente el contenido
            combinedLabel.setVerticalTextPosition(JLabel.BOTTOM);    // Texto arriba del icono
            combinedLabel.setHorizontalTextPosition(JLabel.CENTER); // Centrar el texto con respecto al icono

            add(combinedLabel, BorderLayout.CENTER);
            // Opcional: Para añadir un poco de espacio si lo necesitas.
             setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // 'value' ahora se esperará que sea un Object[] donde:
            // value[0] es el String para el texto
            // value[1] es el ImageIcon para la imagen

            String text = "";
            ImageIcon icon = null;

            if (value instanceof Object[] cellData) {
                if (cellData.length > 0 && cellData[0] != null) {
                    text = cellData[0].toString();
                }
                if (cellData.length > 1 && cellData[1] instanceof ImageIcon) {
                    icon = (ImageIcon) cellData[1];
                }
            }

            combinedLabel.setText(text);
            combinedLabel.setIcon(icon);

            // Manejo de la selección de la celda
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                combinedLabel.setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                combinedLabel.setForeground(table.getForeground());
            }

            return this;
        }
    }

    public class MultiLineTextCellRenderer extends JLabel implements TableCellRenderer {

        public MultiLineTextCellRenderer() {
            setOpaque(true); // Muy importante para que el fondo se pinte correctamente
            // Alineación vertical del texto. Normalmente se querría arriba para que el texto empiece arriba
            setVerticalAlignment(JLabel.TOP);
            // Alineación horizontal del texto. Normalmente se querría a la izquierda
            setHorizontalAlignment(JLabel.LEFT);
            // Añadir un pequeño margen interno para que el texto no esté pegado a los bordes de la celda
            setBorder(new EmptyBorder(10, 5, 2, 5)); // Ajusta los valores (top, left, bottom, right)
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value != null) {
                // Obtenemos el ancho preferido de la columna actual.
                // Es importante hacerlo en getTableCellRendererComponent porque el ancho puede cambiar.
                int columnWidth = table.getColumnModel().getColumn(column).getWidth();
                // Resta un poco para los márgenes internos y evitar que el texto se salga.
                int textWrapWidth = columnWidth - 10; // Ajusta este valor (10 es un ejemplo)

                // Envuelve el texto en HTML para forzar el salto de línea
                // <body style='width: %dpx;'>%s</body>
                // El 'width' es clave para que JLabel sepa dónde cortar el texto.
                String htmlText = "<html><body style='width: " + textWrapWidth + "px;'>" + value.toString() + "</body></html>";
                setText(htmlText);
            } else {
                setText("");
            }

            // Manejo de la selección de la celda
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }
    }

}
