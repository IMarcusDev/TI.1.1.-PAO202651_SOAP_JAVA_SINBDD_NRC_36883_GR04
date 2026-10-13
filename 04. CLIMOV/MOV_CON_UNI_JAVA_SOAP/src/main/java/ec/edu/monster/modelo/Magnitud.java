package ec.edu.monster.modelo;

public enum Magnitud {
    AREA("Area"),
    LONGITUD("Longitud"),
    MASA("Masa"),
    TEMPERATURA("Temperatura"),
    TIEMPO("Tiempo"),
    VOLUMEN("Volumen");
    
    private final String codigo;
    
    Magnitud(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }
}
