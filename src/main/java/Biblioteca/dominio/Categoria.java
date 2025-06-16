package Biblioteca.dominio;

public class Categoria {
    private int categoriaID;
    private String nombreCategoria;
    private String descripcion;

    // Constructor vacío
    public Categoria() {
    }

    // Constructor con parámetros
    public Categoria(int categoriaID, String nombreCategoria, String descripcion) {
        this.categoriaID = categoriaID;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }

    // Constructor sin ID (para nuevas categorías)
    public Categoria(String nombreCategoria, String descripcion) {
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
        return nombreCategoria;
    }
}
