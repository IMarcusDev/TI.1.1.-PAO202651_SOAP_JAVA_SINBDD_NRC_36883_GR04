package ec.edu.monster.modelos.data;

public class ValidacionResultado {
    private final boolean valido;
    private final String mensaje;
    private final double valor;

    private ValidacionResultado(boolean valido, String mensaje, double valor) {
        this.valido = valido;
        this.mensaje = mensaje;
        this.valor = valor;
    }

    public static ValidacionResultado error(String mensaje) {
        return new ValidacionResultado(false, mensaje, 0);
    }

    public static ValidacionResultado ok(double valor) {
        return new ValidacionResultado(true, "", valor);
    }

    public boolean isValido() {
        return valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public double getValor() {
        return valor;
    }
}
