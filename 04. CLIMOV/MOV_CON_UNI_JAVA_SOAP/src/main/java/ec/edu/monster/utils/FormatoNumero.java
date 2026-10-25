package ec.edu.monster.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class FormatoNumero {

    private FormatoNumero() { }

    public static String formatear(double valor) {
        if (!Double.isFinite(valor)) {
            return "—";
        }
        if (valor == 0) {
            return "0";
        }

        String patron = Math.abs(valor) >= 1 ? "#,##0.####" : "0.##########";
        DecimalFormat formato = new DecimalFormat(patron, simbolos());
        formato.setRoundingMode(RoundingMode.HALF_UP);
        String texto = formato.format(valor);

        return texto.equals("0") || texto.equals("-0") ? "≈ 0" : texto;
    }

    private static DecimalFormatSymbols simbolos() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.ROOT);
        simbolos.setDecimalSeparator(',');
        simbolos.setGroupingSeparator('.');
        simbolos.setMinusSign('-');
        return simbolos;
    }
}
