package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ec.edu.monster.controlador.Opcion;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpcionTest {

    @Test
    void pairsEachServerMagnitudeWithItsDisplayText() {
        List<Opcion> magnitudes = Opcion.magnitudes();
        assertEquals(List.of("Longitud", "Masa", "Volumen", "Tiempo", "Temperatura", "Area"),
                magnitudes.stream().map(Opcion::getValor).toList());
        assertEquals("Área", magnitudes.get(5).getTexto());
    }

    @Test
    void pairsEachUnitSymbolWithItsSpanishNameKeepingServerOrder() {
        List<Opcion> unidades = Opcion.unidades(List.of("m", "ft", "xyz"));
        assertEquals(List.of("m", "ft", "xyz"), unidades.stream().map(Opcion::getValor).toList());
        assertEquals(List.of("Metro", "Pie", "xyz"), unidades.stream().map(Opcion::getTexto).toList());
    }
}
