package Biblioteca.presentacion;

import Biblioteca.persistencia.categoriaDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class categoria {
    private int id;
    private String nombre;
    private String descripcion;
    private JTextField textField1;
    private JTextArea textArea1;
    private JButton guardarButton;
    private JButton eliminarButton;
    private JTable table1;

    // Constructor
    public categoria() {
        // Inicializar componentes
        textField1 = new JTextField(20);
        textArea1 = new JTextArea(5, 20);
        guardarButton = new JButton("Guardar");
        eliminarButton = new JButton("Eliminar");
        table1 = new JTable();



        // Agregar listeners
        guardarButton.addActionListener(e -> guardarCategoria());
        eliminarButton.addActionListener(e -> eliminarCategoria());
        table1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    this.id = (int) table1.getValueAt(selectedRow, 0);
                    this.nombre = (String) table1.getValueAt(selectedRow, 1);
                    this.descripcion = (String) table1.getValueAt(selectedRow, 2);
                    textField1.setText(this.nombre);
                    textArea1.setText(this.descripcion);
                }
            }
        });

        // Cargar categorías al iniciar
        cargarCategorias();
    }

    // Método para guardar o actualizar una categoría
    private void guardarCategoria() {
        String nombre = textField1.getText();
        String descripcion = textArea1.getText();
        categoria cat = new categoria();
        cat.setNombre(nombre);
        cat.setDescripcion(descripcion);
        try {
            categoriaDAO dao = new categoriaDAO();
            if (this.id == 0) {
                dao.create(cat); // Crear nueva categoría
            } else {
                cat.setId(this.id);
                dao.update(cat); // Actualizar categoría existente
            }
            cargarCategorias(); // Refrescar la tabla
            this.id = 0; // Resetear id
            textField1.setText("");
            textArea1.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Método para eliminar una categoría
    private void eliminarCategoria() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) table1.getValueAt(selectedRow, 0);
            try {
                categoriaDAO dao = new categoriaDAO();
                dao.delete(id); // Eliminar categoría
                cargarCategorias(); // Refrescar la tabla
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Método para cargar las categorías en la tabla
    private void cargarCategorias() {
        try {
            categoriaDAO dao = new categoriaDAO();
            List<categoria> categories = dao.readAll();
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nombre");
            model.addColumn("Descripción");
            for (categoria cat : categories) {
                model.addRow(new Object[]{cat.getId(), cat.getNombre(), cat.getDescripcion()});
            }
            table1.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}