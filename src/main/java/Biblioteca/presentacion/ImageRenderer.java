package Biblioteca.presentacion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.net.URL; // Para cargar imágenes desde recursos o URL

// Clase para renderizar imágenes en la JTable
class ImageRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // Llama al renderizador por defecto para manejar el fondo, selección, etc.
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof ImageIcon) {
            setIcon((ImageIcon) value);
            setText(""); // No mostrar texto si hay una imagen
        } else if (value instanceof String) {
            // Si el valor es una String (ej. ruta de la imagen)
            try {
                // Intenta cargar la imagen desde el Classpath (útil para imágenes en tu proyecto)
                URL imageUrl = getClass().getResource((String) value);
                if (imageUrl != null) {
                    setIcon(new ImageIcon(imageUrl));
                } else {
                    // Si no se encuentra como recurso, intenta como ruta de archivo
                    setIcon(new ImageIcon((String) value));
                }
                setText("");
            } catch (Exception e) {
                setIcon(null); // No mostrar imagen si hay error
                setText("Error Carga Imagen"); // Mostrar texto de error
                System.err.println("Error al cargar imagen: " + value + " - " + e.getMessage());
            }
        } else {
            setIcon(null); // No mostrar imagen para otros tipos de datos
            setText(value != null ? value.toString() : ""); // Mostrar texto si no es imagen
        }

        setHorizontalAlignment(JLabel.CENTER); // Centrar la imagen en la celda
        return this;
    }
}