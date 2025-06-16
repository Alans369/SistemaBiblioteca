package Biblioteca.presentacion;

import Biblioteca.dominio.Categoria;
import Biblioteca.persistencia.categoriaDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CategoriaForm extends JFrame {

    // Componentes de la interfaz
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnNuevo;
    private JTable tablaCategoria;
    private DefaultTableModel modeloTabla;

    // Variables de control
    private int categoriaSeleccionadaID = 0;
    private categoriaDAO categoriaDAO;

    public CategoriaForm() {
        categoriaDAO = new categoriaDAO();
        initComponents();
        configurarTabla();
        cargarCategorias();
        configurarEventos();
    }

    private void initComponents() {
        setTitle("Gestión de Categorías");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel superior con formulario
        JPanel panelFormulario = createFormPanel();
        add(panelFormulario, BorderLayout.NORTH);

        // Panel central con tabla
        JPanel panelTabla = createTablePanel();
        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = createButtonPanel();
        add(panelBotones, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos de la Categoría"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panel.add(txtNombre, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        panel.add(scrollDescripcion, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Categorías"));

        tablaCategoria = new JTable();
        tablaCategoria.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tablaCategoria);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");

        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEliminar);

        return panel;
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Descripción");
        tablaCategoria.setModel(modeloTabla);

        // Ocultar la columna ID
        tablaCategoria.getColumnModel().getColumn(0).setMinWidth(0);
        tablaCategoria.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaCategoria.getColumnModel().getColumn(0).setWidth(0);
    }

    private void configurarEventos() {
        // Evento para selección en tabla
        tablaCategoria.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablaCategoria.getSelectedRow();
                if (selectedRow != -1) {
                    cargarDatosSeleccionados(selectedRow);
                }
            }
        });

        // Eventos de botones
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarCategoria());
        btnEliminar.addActionListener(e -> eliminarCategoria());
    }

    private void cargarDatosSeleccionados(int selectedRow) {
        categoriaSeleccionadaID = (Integer) modeloTabla.getValueAt(selectedRow, 0);
        txtNombre.setText((String) modeloTabla.getValueAt(selectedRow, 1));
        txtDescripcion.setText((String) modeloTabla.getValueAt(selectedRow, 2));

        btnEliminar.setEnabled(true);
    }

    private void limpiarFormulario() {
        categoriaSeleccionadaID = 0;
        txtNombre.setText("");
        txtDescripcion.setText("");
        tablaCategoria.clearSelection();
        btnEliminar.setEnabled(false);
        txtNombre.requestFocus();
    }

    private void guardarCategoria() {
        // Validaciones
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la categoría es obligatorio.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            // Verificar duplicados
            if (categoriaDAO.existeNombre(txtNombre.getText().trim(), categoriaSeleccionadaID)) {
                JOptionPane.showMessageDialog(this, "Ya existe una categoría con ese nombre.",
                        "Error de validación", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }

            Categoria categoria = new Categoria();
            categoria.setNombreCategoria(txtNombre.getText().trim());
            categoria.setDescripcion(txtDescripcion.getText().trim());

            if (categoriaSeleccionadaID == 0) {
                // Crear nueva categoría
                categoriaDAO.create(categoria);
                JOptionPane.showMessageDialog(this, "Categoría creada exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Actualizar categoría existente
                categoria.setCategoriaID(categoriaSeleccionadaID);
                categoriaDAO.update(categoria);
                JOptionPane.showMessageDialog(this, "Categoría actualizada exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            cargarCategorias();
            limpiarFormulario();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la categoría: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarCategoria() {
        if (categoriaSeleccionadaID == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría para eliminar.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar esta categoría?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoriaDAO.delete(categoriaSeleccionadaID);
                JOptionPane.showMessageDialog(this, "Categoría eliminada exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarCategorias();
                limpiarFormulario();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar la categoría: " + ex.getMessage(),
                        "Error de base de datos", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void cargarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.readAll();
            modeloTabla.setRowCount(0); // Limpiar tabla

            for (Categoria categoria : categorias) {
                Object[] fila = {
                        categoria.getCategoriaID(),
                        categoria.getNombreCategoria(),
                        categoria.getDescripcion()
                };
                modeloTabla.addRow(fila);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar las categorías: " + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Método main para probar el formulario
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CategoriaForm().setVisible(true);
        });
    }
}
