package ec.edu.monster.modelos.data;

public class ConversionResultado {
    private final boolean exito;
    private final String mensaje;
    private final double valor;
    private final boolean sesionExpirada;
    private final Conversion conversion;

    public ConversionResultado(boolean exito, String mensaje, double valor, boolean sesionExpirada, Conversion conversion) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.valor = valor;
        this.sesionExpirada = sesionExpirada;
        this.conversion = conversion;
    }

    public static ConversionResultado ok(String mensaje, double valor) {
        return new ConversionResultado(true, mensaje, valor, false, null);
    }

    public static ConversionResultado error(String mensaje) {
        return new ConversionResultado(false, mensaje, 0, false, null);
    }

    public static ConversionResultado expirada(String mensaje) {
        return new ConversionResultado(false, mensaje, 0, true, null);
    }

    public ConversionResultado conMensaje(String nuevoMensaje) {
        return new ConversionResultado(exito, nuevoMensaje, valor, sesionExpirada, conversion);
    }

    public ConversionResultado conConversion(Conversion nueva) {
        return new ConversionResultado(exito, mensaje, valor, sesionExpirada, nueva);
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public double getValor() {
        return valor;
    }

    public boolean isSesionExpirada() {
        return sesionExpirada;
    }

    public Conversion getConversion() {
        return conversion;
    }
}
