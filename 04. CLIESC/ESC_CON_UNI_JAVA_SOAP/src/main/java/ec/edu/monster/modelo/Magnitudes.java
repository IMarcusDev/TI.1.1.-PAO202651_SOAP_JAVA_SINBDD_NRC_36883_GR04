package ec.edu.monster.modelo;

import java.util.List;

/**
 * Magnitude names exactly as the SOAP server expects them (case-sensitive).
 */
public final class Magnitudes {

    public static final List<String> TODAS =
            List.of("Area", "Longitud", "Masa", "Temperatura", "Tiempo", "Volumen");

    private Magnitudes() {
    }
}
