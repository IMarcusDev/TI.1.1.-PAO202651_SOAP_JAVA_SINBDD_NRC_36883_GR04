package ec.edu.monster.controlador;

import ec.edu.monster.modelos.data.ValidacionResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.utils.FormatoNumero;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidadorConversion {

    public static final int MAX_ENTEROS = 9;
    public static final int MAX_DECIMALES = 6;
    public static final double VALOR_MAXIMO = 999_999_999.999999;

    static final String MENSAJE_SIN_MAGNITUD = "Seleccione qué desea convertir";

    private static final Map<String, Double> CERO_ABSOLUTO = new HashMap<>();

    static {
        CERO_ABSOLUTO.put("°C", -273.15);
        CERO_ABSOLUTO.put("°F", -459.67);
        CERO_ABSOLUTO.put("K", 0.0);
        CERO_ABSOLUTO.put("°N", -90.1395);  
        CERO_ABSOLUTO.put("°Ré", -218.52);  
    }

    private static final Pattern NUMERO_VALIDO = Pattern.compile("^-?(\\d+([.,]\\d*)?|[.,]\\d+)$");

    private ValidadorConversion() { }

    public static String filtrarValor(String texto, boolean permiteNegativos) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        StringBuilder limpio = new StringBuilder(texto.length());
        boolean haySeparador = false;
        int enteros = 0;
        int decimales = 0;

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '-') {
                if (permiteNegativos && limpio.length() == 0) {
                    limpio.append(c);
                }
            } else if (c == '.' || c == ',') {
                if (!haySeparador) {
                    haySeparador = true;
                    limpio.append(c);
                }
            } else if (esDigitoAscii(c)) {
                if (haySeparador && decimales < MAX_DECIMALES) {
                    limpio.append(c);
                    decimales++;
                } else if (!haySeparador && enteros < MAX_ENTEROS) {
                    limpio.append(c);
                    enteros++;
                }
            }
        }

        return limpio.toString();
    }

    public static ValidacionResultado validar(Magnitud magnitud, String texto, String origen, String destino) {
        if (magnitud == null) {
            return ValidacionResultado.error(MENSAJE_SIN_MAGNITUD);
        }

        ValidacionResultado valor = validarValor(texto);
        if (!valor.isValido()) {
            return valor;
        }

        if (estaVacio(origen)) {
            return ValidacionResultado.error("Seleccione la unidad de origen");
        }
        if (estaVacio(destino)) {
            return ValidacionResultado.error("Seleccione la unidad de destino");
        }

        if (valor.getValor() < 0 && !magnitud.permiteNegativos()) {
            return ValidacionResultado.error("No se permiten valores negativos en " + magnitud.getNombre());
        }

        if (magnitud == Magnitud.TEMPERATURA) {
            Double cero = CERO_ABSOLUTO.get(Normalizer.normalize(origen, Normalizer.Form.NFC));
            if (cero != null && valor.getValor() < cero) {
                return ValidacionResultado.error("La temperatura no puede ser menor al cero absoluto ("
                        + FormatoNumero.formatear(cero) + " " + origen + ")");
            }
        }

        return valor;
    }

    private static ValidacionResultado validarValor(String texto) {
        if (texto == null || texto.isEmpty()) {
            return ValidacionResultado.error("Ingrese un valor para convertir");
        }
        for (int i = 0; i < texto.length(); i++) {
            if (esEspacio(texto.charAt(i))) {
                return ValidacionResultado.error("El valor no debe contener espacios");
            }
        }

        if (!NUMERO_VALIDO.matcher(texto).matches()) {
            return ValidacionResultado.error(motivoFormatoInvalido(texto));
        }

        String sinSigno = texto.startsWith("-") ? texto.substring(1) : texto;
        int posSeparador = indiceSeparador(sinSigno);
        int enteros = posSeparador < 0 ? sinSigno.length() : posSeparador;
        int decimales = posSeparador < 0 ? 0 : sinSigno.length() - posSeparador - 1;

        if (enteros > MAX_ENTEROS) {
            return ValidacionResultado.error("El valor admite como máximo " + MAX_ENTEROS + " dígitos enteros");
        }
        if (decimales > MAX_DECIMALES) {
            return ValidacionResultado.error("El valor admite como máximo " + MAX_DECIMALES + " decimales");
        }

        double numero;
        try {
            numero = Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            return ValidacionResultado.error("El valor no es un número válido");
        }
        if (!Double.isFinite(numero)) {
            return ValidacionResultado.error("El valor no es un número válido");
        }

        if (Math.abs(numero) > VALOR_MAXIMO) {
            return ValidacionResultado.error("El valor está fuera del rango permitido");
        }

        return ValidacionResultado.ok(numero == 0 ? 0 : numero);
    }

    private static String motivoFormatoInvalido(String texto) {
        if (soloSignosOSeparadores(texto)) {
            return "Ingrese un número, no solo un signo o un separador";
        }
        int separadores = 0;
        boolean hayDigito = false;
        boolean hayLetra = false;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '.' || c == ',') {
                separadores++;
            }
            hayDigito |= esDigitoAscii(c);
            hayLetra |= Character.isLetter(c);
        }
        if (separadores > 1) {
            return "Use un solo separador decimal (punto o coma)";
        }
        if ((texto.indexOf('e') >= 0 || texto.indexOf('E') >= 0) && hayDigito) {
            return "No se admite notación científica";
        }
        if (hayLetra) {
            return "El valor solo puede contener números";
        }
        return "El valor no es un número válido";
    }

    private static boolean soloSignosOSeparadores(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c != '-' && c != '.' && c != ',') {
                return false;
            }
        }
        return true;
    }

    private static int indiceSeparador(String texto) {
        int punto = texto.indexOf('.');
        return punto >= 0 ? punto : texto.indexOf(',');
    }

    private static boolean esDigitoAscii(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean esEspacio(char c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c);
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
