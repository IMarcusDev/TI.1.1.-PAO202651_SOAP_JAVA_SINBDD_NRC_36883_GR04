package ec.edu.monster.pruebas.controlador;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.controlador.ValidadorConversion;
import ec.edu.monster.modelos.data.ValidacionResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import org.junit.Test;

public class ValidadorConversionTest {

    private static final Magnitud L = Magnitud.LONGITUD;
    private static final Magnitud T = Magnitud.TEMPERATURA;

    @Test
    public void filtrarValor_dejaSoloDigitosUnSeparadorYSignoPermitido() {
        Object[][] casos = {
            {null, false, ""},
            {"", false, ""},
            {"12a3", false, "123"},
            {"1.2.3", false, "1.23"},
            {"1,2.3", false, "1,23"},
            {"-5", false, "5"},
            {"-5", true, "-5"},
            {"5-", true, "5"},
            {"--5", true, "-5"},
            {" 1 2 ", false, "12"},
            {"1e5", false, "15"},
            {"+5", false, "5"},
            {"1234567890", false, "123456789"},
            {"1.12345678", false, "1.123456"},
            {"123456789012.12345678", true, "123456789.123456"},
        };
        for (Object[] caso : casos) {
            assertEquals("filtrar «" + caso[0] + "»", caso[2],
                    ValidadorConversion.filtrarValor((String) caso[0], (Boolean) caso[1]));
        }
    }

    @Test
    public void validar_devuelveElPrimerErrorConElMensajeExacto() {
        Object[][] casos = {
            {null, "1", "m", "cm", "Seleccione qué desea convertir"},
            {null, "", null, null, "Seleccione qué desea convertir"},
            {L, null, "m", "cm", "Ingrese un valor para convertir"},
            {L, "", "m", "cm", "Ingrese un valor para convertir"},
            {L, " 1", "m", "cm", "El valor no debe contener espacios"},
            {L, "1 2", "m", "cm", "El valor no debe contener espacios"},
            {L, "1 2", "m", "cm", "El valor no debe contener espacios"},
            {L, "-", "m", "cm", "Ingrese un número, no solo un signo o un separador"},
            {L, ".", "m", "cm", "Ingrese un número, no solo un signo o un separador"},
            {L, ",", "m", "cm", "Ingrese un número, no solo un signo o un separador"},
            {L, "-.", "m", "cm", "Ingrese un número, no solo un signo o un separador"},
            {L, "1.2.3", "m", "cm", "Use un solo separador decimal (punto o coma)"},
            {L, "1,2.3", "m", "cm", "Use un solo separador decimal (punto o coma)"},
            {L, "1e5", "m", "cm", "No se admite notación científica"},
            {L, "1E5", "m", "cm", "No se admite notación científica"},
            {L, "abc", "m", "cm", "El valor solo puede contener números"},
            {L, "NaN", "m", "cm", "El valor solo puede contener números"},
            {L, "Infinity", "m", "cm", "El valor solo puede contener números"},
            {L, "-Infinity", "m", "cm", "El valor solo puede contener números"},
            {L, "1d", "m", "cm", "El valor solo puede contener números"},
            {L, "0x1p3", "m", "cm", "El valor solo puede contener números"},
            {L, "1$", "m", "cm", "El valor no es un número válido"},
            {L, "+5", "m", "cm", "El valor no es un número válido"},
            {L, "1-2", "m", "cm", "El valor no es un número válido"},
            {L, "1234567890", "m", "cm", "El valor admite como máximo 9 dígitos enteros"},
            {L, "1.1234567", "m", "cm", "El valor admite como máximo 6 decimales"},
            {L, "5", null, "cm", "Seleccione la unidad de origen"},
            {L, "5", "m", null, "Seleccione la unidad de destino"},
            {L, "-5", null, null, "Seleccione la unidad de origen"},
            {L, "-5", "m", "cm", "No se permiten valores negativos en Longitud"},
            {Magnitud.AREA, "-1", "m2", "ha", "No se permiten valores negativos en Área"},
            {Magnitud.TIEMPO, "-0,5", "s", "h", "No se permiten valores negativos en Tiempo"},
            {T, "-300", "°C", "K", "La temperatura no puede ser menor al cero absoluto (-273,15 °C)"},
            {T, "-459.68", "°F", "K", "La temperatura no puede ser menor al cero absoluto (-459,67 °F)"},
            {T, "-1", "K", "°C", "La temperatura no puede ser menor al cero absoluto (0 K)"},
            {T, "-90.14", "°N", "°C", "La temperatura no puede ser menor al cero absoluto (-90,1395 °N)"},
            {T, "-218,53", "°Ré", "°C", "La temperatura no puede ser menor al cero absoluto (-218,52 °Ré)"},
        };
        for (Object[] caso : casos) {
            ValidacionResultado resultado = ValidadorConversion.validar(
                    (Magnitud) caso[0], (String) caso[1], (String) caso[2], (String) caso[3]);
            String contexto = caso[0] + " «" + caso[1] + "» " + caso[2] + "→" + caso[3];
            assertFalse(contexto + " debía ser inválido", resultado.isValido());
            assertEquals(contexto, caso[4], resultado.getMensaje());
        }
    }

    @Test
    public void validar_ceroAbsolutoConReaumurEnFormaNfd_tambienSeReconoce() {
        ValidacionResultado resultado = ValidadorConversion.validar(T, "-219", "°Ré", "°C");

        assertFalse(resultado.isValido());
        assertTrue(resultado.getMensaje(),
                resultado.getMensaje().startsWith("La temperatura no puede ser menor al cero absoluto (-218,52"));
    }

    @Test
    public void validar_valoresValidos_interpretaComaYPuntoSinDependerDelIdioma() {
        Object[][] casos = {
            {L, "5", "m", 5.0},
            {L, "5.", "m", 5.0},
            {L, ",5", "m", 0.5},
            {L, "2,5", "m", 2.5},
            {L, "2.5", "m", 2.5},
            {L, "-0", "m", 0.0},
            {L, "999999999.999999", "m", 999_999_999.999999},
            {T, "-273,15", "°C", -273.15},
            {T, "-459.67", "°F", -459.67},
            {T, "0", "K", 0.0},
            {T, "-90,1395", "°N", -90.1395},
            {T, "-218.52", "°Ré", -218.52},
            {T, "-40", "°C", -40.0},
        };
        for (Object[] caso : casos) {
            String origen = (String) caso[2];
            ValidacionResultado resultado = ValidadorConversion.validar(
                    (Magnitud) caso[0], (String) caso[1], origen, origen);
            assertTrue(caso[1] + ": " + resultado.getMensaje(), resultado.isValido());
            assertEquals((String) caso[1], (Double) caso[3], resultado.getValor(), 1e-9);
        }
    }

    @Test
    public void validar_menosCero_seInterpretaComoCeroPositivo() {
        double valor = ValidadorConversion.validar(L, "-0", "m", "cm").getValor();

        assertEquals(0.0, valor, 0);
        assertFalse(1 / valor < 0);
    }
}
