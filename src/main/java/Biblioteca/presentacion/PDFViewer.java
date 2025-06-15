package Biblioteca.presentacion;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDFViewer extends JFrame {
    private PDDocument document;
    private PDFRenderer pdfRenderer;
    private JLabel imageLabel;
    private JScrollPane scrollPane;
    private int currentPage = 0;
    private float zoomLevel = 1.0f;
    private int totalPages = 0;

    public PDFViewer() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Visor PDF con Zoom");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton openButton = new JButton("Abrir PDF");
        JButton zoomInButton = new JButton("Zoom +");
        JButton zoomOutButton = new JButton("Zoom -");
        JButton prevButton = new JButton("Página Anterior");
        JButton nextButton = new JButton("Página Siguiente");

        buttonPanel.add(openButton);
        buttonPanel.add(zoomInButton);
        buttonPanel.add(zoomOutButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        // Área de visualización
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        scrollPane = new JScrollPane(imageLabel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Event listeners
        openButton.addActionListener(e -> openPDF());
        zoomInButton.addActionListener(e -> zoom(1.2f));
        zoomOutButton.addActionListener(e -> zoom(0.8f));
        prevButton.addActionListener(e -> previousPage());
        nextButton.addActionListener(e -> nextPage());

        pack();
        setLocationRelativeTo(null);
    }

    private void openPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos PDF", "pdf"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadPDF(selectedFile);
        }
    }

    private void loadPDF(File file) {
        try {
            // Cerrar documento anterior si existe
            if (document != null) {
                document.close();
            }

            // Alternativa que funciona en todas las versiones de PDFBox
            try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                document = PDDocument.load(fis);
            }

            pdfRenderer = new PDFRenderer(document);
            totalPages = document.getNumberOfPages();
            currentPage = 0;
            zoomLevel = 1.0f;

            renderCurrentPage();
            setTitle("Visor PDF - " + file.getName() +
                    " (Página " + (currentPage + 1) + " de " + totalPages + ")");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar el PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderCurrentPage() {
        if (pdfRenderer == null) return;

        try {
            // Renderizar la página como imagen con el zoom actual
            float dpi = 72 * zoomLevel; // DPI base * factor de zoom
            BufferedImage image = pdfRenderer.renderImageWithDPI(
                    currentPage, dpi, ImageType.RGB);

            imageLabel.setIcon(new ImageIcon(image));
            imageLabel.revalidate();

            // Actualizar título con información de página
            setTitle("Visor PDF (Página " + (currentPage + 1) + " de " + totalPages +
                    " - Zoom: " + Math.round(zoomLevel * 100) + "%)");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al renderizar la página: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void zoom(float factor) {
        zoomLevel *= factor;
        // Limitar el zoom entre 0.25x y 5x
        zoomLevel = Math.max(0.25f, Math.min(5.0f, zoomLevel));
        renderCurrentPage();
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            renderCurrentPage();
        }
    }

    private void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            renderCurrentPage();
        }
    }

    @Override
    public void dispose() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.dispose();
    }

    // Constructor que recibe la ruta del PDF
    public PDFViewer(String pdfPath) {
        initializeUI();
        if (pdfPath != null && !pdfPath.isEmpty()) {
            loadPDF(new File(pdfPath));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ejemplo con ruta específica - cambia esta ruta por la tuya
            String rutaPDF = "C:\\Users\\MINEDUCYT\\OneDrive\\Desktop\\parctica030625\\SistemaBiblioteca\\src\\main\\resources\\pdfs\\4G1-Alan-Campos-CulturaAmbiental_1749863529837.pdf";
            // O para sistemas Unix/Mac: "/home/usuario/Documents/ejemplo.pdf"

            // Verificar si el archivo existe
            File pdfFile = new File(rutaPDF);
            if (pdfFile.exists()) {
                new PDFViewer(rutaPDF).setVisible(true);
            } else {
                System.err.println("El archivo PDF no existe en la ruta: " + rutaPDF);
                // Abrir sin PDF inicial
                new PDFViewer(null).setVisible(true);
            }
        });
    }
}
