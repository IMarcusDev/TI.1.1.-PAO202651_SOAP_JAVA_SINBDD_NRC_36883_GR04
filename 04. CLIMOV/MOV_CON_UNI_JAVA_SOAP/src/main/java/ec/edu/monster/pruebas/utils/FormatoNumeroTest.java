package ec.edu.monster.pruebas.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.utils.FormatoNumero;
import java.util.Locale;
import org.junit.Test;

public class FormatoNumeroTest {

    @Test
    public void formatear_comaDecimalPuntoDeMilesYDecimalesSegunElTamano() {
        Object[][] casos = {
            {0.0, "0"},
            {-0.0, "0"},
            {1.0, "1"},
            {-40.0, "-40"},
            {0.5, "0,5"},
            {247.10516301527602, "247,1052"},
            {1234.56789, "1.234,5679"},
            {1234567.891, "1.234.567,891"},
            {-1234567.5, "-1.234.567,5"},
            {0.00000254, "0,00000254"},
            {0.1234567891234, "0,1234567891"},
            {1e-12, "≈ 0"},
            {-1e-12, "≈ 0"},
            {Double.NaN, "—"},
            {Double.POSITIVE_INFINITY, "—"},
        };
        for (Object[] caso : casos) {
            assertEquals("formatear " + caso[0], caso[1], FormatoNumero.formatear((Double) caso[0]));
        }
    }

    @Test
    public void formatear_numeroGrande_sinNotacionCientificaYLargo() {
        String texto = FormatoNumero.formatear(6.047999999999994E17);

        assertFalse(texto, texto.contains("E") || texto.contains("e"));
        assertTrue(texto, texto.startsWith("604.79"));
        assertTrue("debía activar la letra pequeña (más de 12 caracteres)", texto.length() > 12);
    }

    @Test
    public void formatear_noDependeDelIdiomaDelTelefono() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            assertEquals("1.234,5679", FormatoNumero.formatear(1234.56789));
        } finally {
            Locale.setDefault(original);
        }
    }
}
