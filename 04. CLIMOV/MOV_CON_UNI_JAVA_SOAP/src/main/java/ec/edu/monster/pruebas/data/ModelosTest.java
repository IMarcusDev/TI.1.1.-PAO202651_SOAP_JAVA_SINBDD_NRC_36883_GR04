package ec.edu.monster.pruebas.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.NombresUnidades;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.servicios.SesionServicio;
import org.junit.Test;

public class ModelosTest {

    @Test
    public void magnitud_codigosDelServidorConMayusculasExactas() {
        assertEquals("Area", Magnitud.AREA.getCodigoServidor());
        assertEquals("Longitud", Magnitud.LONGITUD.getCodigoServidor());
        assertEquals("Masa", Magnitud.MASA.getCodigoServidor());
        assertEquals("Temperatura", Magnitud.TEMPERATURA.getCodigoServidor());
        assertEquals("Volumen", Magnitud.VOLUMEN.getCodigoServidor());
        assertEquals("Tiempo", Magnitud.TIEMPO.getCodigoServidor());
    }

    @Test
    public void magnitud_soloTemperaturaPermiteNegativos() {
        for (Magnitud magnitud : Magnitud.values()) {
            assertEquals(magnitud.toString(), magnitud == Magnitud.TEMPERATURA, magnitud.permiteNegativos());
        }
    }

    @Test
    public void nombresUnidades_todasLasDelServidorTienenNombre() {
        String[] codigos = {"cm2", "m2", "km2", "ha", "ft2", "ac", "m", "cm", "ft", "yd", "mi",
            "g", "kg", "oz", "lb", "qq", "°C", "°F", "K", "°N", "°Ré", "mL", "L", "m3", "ft3", "gal",
            "ms", "s", "min", "h", "d", "w"};
        for (String codigo : codigos) {
            assertFalse(codigo, codigo.equals(NombresUnidades.nombre(codigo)));
        }
        assertEquals("Réaumur", NombresUnidades.nombre("°Ré"));
        assertEquals("xyz", NombresUnidades.nombre("xyz"));
    }

    @Test
    public void sesion_soloAutenticadaConTokenNoVacio() {
        SesionServicio sesion = new SesionServicio();
        assertFalse(sesion.estaAutenticado());

        sesion.iniciar("user", "   ");
        assertFalse(sesion.estaAutenticado());

        sesion.iniciar("user", "abc");
        assertTrue(sesion.estaAutenticado());

        sesion.limpiar();
        assertFalse(sesion.estaAutenticado());
    }
}
