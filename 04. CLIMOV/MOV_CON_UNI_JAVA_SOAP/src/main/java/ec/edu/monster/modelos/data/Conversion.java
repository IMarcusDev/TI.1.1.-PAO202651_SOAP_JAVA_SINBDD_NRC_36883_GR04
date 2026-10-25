package ec.edu.monster.modelos.data;

import ec.edu.monster.modelos.enums.Magnitud;

public class Conversion {
    private final Magnitud magnitud;
    private final double valor;
    private final String unidadOrigen;
    private final String unidadDestino;
    private double resultado;

    public Conversion(Magnitud magnitud, double valor, String unidadOrigen, String unidadDestino) {
        this.magnitud = magnitud;
        this.valor = valor;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
    }

    public Magnitud getMagnitud() {
        return magnitud;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidadOrigen() {
        return unidadOrigen;
    }

    public String getUnidadDestino() {
        return unidadDestino;
    }

    public double getResultado() {
        return resultado;
    }

    public void setResultado(double resultado) {
        this.resultado = resultado;
    }
}
