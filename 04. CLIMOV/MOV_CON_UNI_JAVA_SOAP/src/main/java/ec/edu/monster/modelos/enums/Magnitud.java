package ec.edu.monster.modelos.enums;

import ec.edu.monster.R;

public enum Magnitud {
    AREA("Area", "Área", "Extensión de una superficie.", R.drawable.mag_area),
    LONGITUD("Longitud", "Longitud", "Distancia entre dos puntos.", R.drawable.mag_longitud),
    MASA("Masa", "Masa", "Cantidad de materia de un cuerpo.", R.drawable.mag_masa),
    TEMPERATURA("Temperatura", "Temperatura", "Nivel de calor de un cuerpo o ambiente.", R.drawable.mag_temperatura),
    VOLUMEN("Volumen", "Volumen", "Espacio que ocupa un cuerpo.", R.drawable.mag_volumen),
    TIEMPO("Tiempo", "Tiempo", "Duración de un suceso.", R.drawable.mag_tiempo);

    private final String codigoServidor;
    private final String nombre;
    private final String descripcion;
    private final int icono;

    Magnitud(String codigoServidor, String nombre, String descripcion, int icono) {
        this.codigoServidor = codigoServidor;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.icono = icono;
    }

    public String getCodigoServidor() {
        return codigoServidor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getIcono() {
        return icono;
    }

    public boolean permiteNegativos() {
        return this == TEMPERATURA;
    }
}
