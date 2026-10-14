package ec.edu.monster.pruebas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.enums.Magnitud;
import org.junit.Test;

public class ConversionTest {

    @Test
    public void constructor_datosValidos_guardaLosValores() {
        Conversion conversion = new Conversion(Magnitud.AREA, 2500, "cm2", "m2");

        assertEquals(Magnitud.AREA, conversion.getMagnitud());
        assertEquals(2500, conversion.getValor(), 0.0);
        assertEquals("cm2", conversion.getUnidadOrigen());
        assertEquals("m2", conversion.getUnidadDestino());
    }

    @Test
    public void nuevaConversion_sinConvertir_noTieneResultado() {
        Conversion conversion = new Conversion(Magnitud.AREA, 1, "cm2", "m2");

        assertFalse(conversion.tieneResultado());
        assertThrows(IllegalStateException.class, conversion::getResultado);
    }

    @Test
    public void setResultado_cero_esUnResultadoValido() {
        Conversion conversion = new Conversion(Magnitud.TEMPERATURA, 32, "°F", "°C");

        conversion.setResultado(0);

        assertTrue(conversion.tieneResultado());
        assertEquals(0, conversion.getResultado(), 0.0);
    }

    @Test
    public void constructor_magnitudNula_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new Conversion(null, 1, "m", "cm"));
    }

    @Test
    public void constructor_valorNaN_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new Conversion(Magnitud.LONGITUD, Double.NaN, "m", "cm"));
    }

    @Test
    public void constructor_unidadEnBlanco_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new Conversion(Magnitud.LONGITUD, 1, "   ", "cm"));
    }
}