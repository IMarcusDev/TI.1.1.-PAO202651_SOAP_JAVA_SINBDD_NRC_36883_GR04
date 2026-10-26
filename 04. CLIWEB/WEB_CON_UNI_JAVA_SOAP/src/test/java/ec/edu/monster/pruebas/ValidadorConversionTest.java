package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import ec.edu.monster.controlador.ValidadorConversion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ValidadorConversionTest {

    private static final String VALOR_INVALIDO = "Ingrese un valor numérico válido";
    private static final String SIN_MAGNITUD = "Seleccione una magnitud";
    private static final String SIN_UNIDADES = "Seleccione la unidad de origen y de destino";

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "100;100.0",
        "2.5;2.5",
        "2,5;2.5",
        "-3;-3.0",
        "+4;4.0",
        ".5;0.5",
        ",5;0.5",
        "  7  ;7.0"
    })
    void readsPlainDecimalsWithDotOrComma(String texto, double esperado) {
        assertEquals(esperado, ValidadorConversion.leerValor(texto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "abc", "1e5", "1.2.3", "1,2,3", "5.", "--1", "1 000",
        "Infinity", "NaN", "0x10", "10d", "10f"})
    void rejectsAnythingThatIsNotAPlainDecimal(String texto) {
        assertNull(ValidadorConversion.leerValor(texto));
    }

    @Test
    void rejectsNumbersTooLargeToBeFinite() {
        assertNull(ValidadorConversion.leerValor("1" + "0".repeat(400)));
    }

    @Test
    void acceptsACompleteRequest() {
        assertNull(ValidadorConversion.validar("100", "Longitud", "m", "ft"));
    }

    @Test
    void checksTheValueFirst() {
        assertEquals(VALOR_INVALIDO, ValidadorConversion.validar("abc", null, null, null));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Peso", "longitud", "Área"})
    void requiresAKnownServerMagnitude(String magnitud) {
        assertEquals(SIN_MAGNITUD, ValidadorConversion.validar("1", magnitud, "m", "ft"));
    }

    @ParameterizedTest
    @CsvSource(value = {"'',ft", "m,''", "'',''", " ,ft", "m, "}, nullValues = "null")
    void requiresBothUnits(String desde, String hacia) {
        assertEquals(SIN_UNIDADES, ValidadorConversion.validar("1", "Longitud", desde, hacia));
    }

    @Test
    void requiresBothUnitsWhenMissing() {
        assertEquals(SIN_UNIDADES, ValidadorConversion.validar("1", "Area", null, null));
    }
}
