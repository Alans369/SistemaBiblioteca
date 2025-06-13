package Biblioteca.dominio;

public class Categoria {
    private int categoriaID;
    private String nombreCategoria;
    private String descripcion;

    // Constructor vacío
    public Categoria() {
    }

    // Constructor "lleno" (con todos los atributos)
    public Categoria(int categoriaID, String nombreCategoria, String descripcion) {
        this.categoriaID = categoriaID;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }

    // Getters y Setters

    public int getCategoriaID() {
        return categoriaID;
    }

    public void setCategoriaID(int categoriaID) {
        this.categoriaID = categoriaID;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        // Esto es lo que se mostrará en el JComboBox
        return nombreCategoria;
    }
}
