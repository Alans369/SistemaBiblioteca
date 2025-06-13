package Biblioteca.presentacion;

import javax.swing.*;

public class libroformC extends JDialog {
    private JPanel libropanel;
    private JTextField textField5;
    private JComboBox comboBox1;
    private JButton subirLibroButton;
    private JTextField textField6;
    private JTextField textField7;
    private JButton selecionarButton;
    private JButton button1;
    private JButton crearButton;
    private JButton cancelarButton;


    public libroformC(Mainform mainForm){
        super(mainForm, "Crear libro ", true); // true para modal
        setSize(700, 700);
        setLocationRelativeTo(mainForm); // Centra el diálogo respecto a su propietario
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        add(libropanel);
    } // Cierra solo el diálogo




}
