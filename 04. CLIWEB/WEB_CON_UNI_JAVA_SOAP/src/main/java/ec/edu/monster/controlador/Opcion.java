package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Magnitudes;
import ec.edu.monster.modelo.NombresUnidades;
import java.util.List;

/**
 * A value sent to the server paired with the text shown to the user.
 * Plain getters so the JSPs can read it through EL.
 */
public final class Opcion {

    private final String valor;
    private final String texto;

    public Opcion(String valor, String texto) {
        this.valor = valor;
        this.texto = texto;
    }

    public String getValor() {
        return valor;
    }

    public String getTexto() {
        return texto;
    }

    /** Server magnitude names with their display text. */
    public static List<Opcion> magnitudes() {
        return Magnitudes.TODAS.stream()
                .map(m -> new Opcion(m, Magnitudes.nombreVisible(m)))
                .toList();
    }

    /** Unit symbols with their Spanish names. */
    public static List<Opcion> unidades(List<String> simbolos) {
        return simbolos.stream()
                .map(s -> new Opcion(s, NombresUnidades.nombre(s)))
                .toList();
    }
}
