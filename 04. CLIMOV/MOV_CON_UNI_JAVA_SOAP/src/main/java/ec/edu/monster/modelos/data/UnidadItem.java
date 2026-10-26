package ec.edu.monster.modelos.data;

public class UnidadItem {
    private final String codigo;
    private final String nombre;

    public UnidadItem(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
