package ec.edu.monster.modelo;

public class Conversion {
    private final Magnitud magnitud;
    private final double valor;
    private final String unidadOrigen;
    private final String unidadDestino;
    private Double resultado;
    
    public Conversion(Magnitud magnitud, double valor, String unidadOrigen, String unidadDestino){
        if(magnitud == null){
            throw new IllegalArgumentException("Debe seleccionar una magnitud");
        }
        
        if(!Double.isFinite(valor)){
            throw new IllegalArgumentException("El valor debe ser un número válido");
        }
        if(textoVacio(unidadOrigen) || textoVacio(unidadDestino)) {
            throw new IllegalArgumentException("Debe seleccionar las unidades de origen y destino");
        }
        
        this.magnitud = magnitud;
        this.valor = valor;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
    }
    
    private static boolean textoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
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

    public boolean tieneResultado() {
        return resultado != null;
    }

    public double getResultado() {
        if(resultado == null){
            throw new IllegalStateException("La conversión aún no tiene resultado");
        }
        return resultado;
    }
    
    public void setResultado(double resultado){
        this.resultado = resultado;
    }
}
