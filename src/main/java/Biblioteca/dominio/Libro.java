package Biblioteca.dominio;

public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String imagenR;
    private String descripcion;
    private String rutapdf;
    private int categoriaId;

    public Libro(){

    }

    public Libro(int id, String titulo, String autor, String imagenR, String descripcion, String rutaPdf, int categoriaId) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.imagenR = imagenR;
        this.descripcion = descripcion;
        this.rutapdf = rutaPdf;
        this.categoriaId = categoriaId;
    }

    // --- GETTERS ---
    // Métodos para obtener el valor de cada atributo

    public int getId() {return id;}

    public String getTitulo() {return titulo;}

    public String getAutor() {return autor;}

    public String getImagenR() {return imagenR;}

    public String getDescripcion() {return descripcion;}

    public String getRutaPdf() {return rutapdf;}

    public int getCategoriaId() {return categoriaId;}

    // --- SETTERS ---
    // Métodos para establecer o modificar el valor de cada atributo

    public void setId(int id) {this.id = id;}

    public void setTitulo(String titulo) {this.titulo = titulo;}

    public void setAutor(String autor) {this.autor = autor;}

    public void setImagenR(String imagenR) {this.imagenR = imagenR;}

    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public void setRutaPdf(String RutaPdf) {this.rutapdf = RutaPdf;}

    public void setCategoriaId(int categoriaId) {this.categoriaId = categoriaId;}

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", imagenR='" + imagenR + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", rutaPdf='" + rutapdf + '\'' +
                ", categoriaId=" + categoriaId +
                '}';
    }


}
