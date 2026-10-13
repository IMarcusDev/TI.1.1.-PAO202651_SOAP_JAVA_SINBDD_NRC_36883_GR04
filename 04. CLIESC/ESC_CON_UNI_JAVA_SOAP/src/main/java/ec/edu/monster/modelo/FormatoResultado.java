package ec.edu.monster.modelo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class FormatoResultado {

    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols();

    static {
        SYMBOLS.setDecimalSeparator(',');
    }

    private FormatoResultado() {
    }

    public static String formatear(double valor, String desde, double resultado, String hacia) {
        DecimalFormat entrada = new DecimalFormat("0.####", SYMBOLS);
        DecimalFormat salida = new DecimalFormat("0.0000", SYMBOLS);
        return entrada.format(valor) + " " + desde + " = " + salida.format(resultado) + " " + hacia;
    }
}