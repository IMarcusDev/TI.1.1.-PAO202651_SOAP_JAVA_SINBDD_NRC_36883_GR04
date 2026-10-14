package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Magnitudes;
import java.util.regex.Pattern;

/**
 * Input validation of the conversion form, free of servlet types so it can
 * be unit-tested. Same rules and messages as the desktop client.
 */
public final class ValidadorConversion {

    public static final String VALOR_INVALIDO = "Ingrese un valor numérico válido";
    public static final String SIN_MAGNITUD = "Seleccione una magnitud";
    public static final String SIN_UNIDADES = "Seleccione la unidad de origen y de destino";
    // Optional sign, digits and at most one decimal separator ("." or ","). No exponents or suffixes.
    private static final Pattern NUMERO = Pattern.compile("[+-]?(\\d+([.,]\\d+)?|[.,]\\d+)");

    private ValidadorConversion() {
    }

    /** Accepts plain decimals such as "2.5" or "2,5"; returns null for anything else. */
    public static Double leerValor(String texto) {
        if (texto == null || !NUMERO.matcher(texto.trim()).matches()) {
            return null;
        }
        double valor = Double.parseDouble(texto.trim().replace(',', '.'));
        return Double.isFinite(valor) ? valor : null;
    }

    /** Returns the message to show for the first invalid field, or null when the request is complete. */
    public static String validar(String valor, String magnitud, String desde, String hacia) {
        if (leerValor(valor) == null) {
            return VALOR_INVALIDO;
        }
        if (magnitud == null || !Magnitudes.TODAS.contains(magnitud)) {
            return SIN_MAGNITUD;
        }
        if (estaVacio(desde) || estaVacio(hacia)) {
            return SIN_UNIDADES;
        }
        return null;
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
