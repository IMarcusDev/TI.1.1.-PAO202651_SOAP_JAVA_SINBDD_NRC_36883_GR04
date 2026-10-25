package ec.edu.monster.pruebas.controlador;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.controlador.ControladorConSesion;
import ec.edu.monster.controlador.ConversionControlador;
import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.modelos.interfaces.ConversionRepositorio;
import ec.edu.monster.servicios.SesionServicio;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class ConversionControladorTest {

    private static class ConversionFalsa implements ConversionRepositorio {
        int llamadas;
        String tokenRecibido;
        ConversionResultado respuesta = ConversionResultado.ok("Conversión Exitosa", 247.10516301527602);

        @Override
        public ConversionResultado convertir(String token, Conversion conversion) {
            llamadas++;
            tokenRecibido = token;
            return respuesta;
        }
    }

    private static class CatalogoFalso implements CatalogoRepositorio {
        CatalogoResultado respuesta = CatalogoResultado.ok("ok", Arrays.asList("km2", "ac"));

        @Override
        public CatalogoResultado obtenerCatalogo(String token, Magnitud magnitud) {
            return respuesta;
        }
    }

    private ConversionFalsa conversiones;
    private CatalogoFalso catalogo;
    private SesionServicio sesion;
    private ConversionControlador controlador;

    @Before
    public void preparar() {
        conversiones = new ConversionFalsa();
        catalogo = new CatalogoFalso();
        sesion = new SesionServicio();
        sesion.iniciar("user", "token-1");
        controlador = new ConversionControlador(conversiones, catalogo, sesion);
    }

    @Test
    public void getMagnitudes_enElOrdenDeLaReferencia() {
        assertEquals(Arrays.asList(Magnitud.AREA, Magnitud.LONGITUD, Magnitud.MASA,
                Magnitud.TEMPERATURA, Magnitud.VOLUMEN, Magnitud.TIEMPO), controlador.getMagnitudes());
    }

    @Test
    public void convertir_ok_enviaElTokenYDevuelveElDetalle() {
        ConversionResultado resultado = controlador.convertir(Magnitud.AREA, "1", "km2", "ac");

        assertTrue(resultado.isExito());
        assertEquals("token-1", conversiones.tokenRecibido);
        Conversion detalle = resultado.getConversion();
        assertNotNull(detalle);
        assertEquals("km2", detalle.getUnidadOrigen());
        assertEquals(247.10516301527602, detalle.getResultado(), 1e-12);
    }

    @Test
    public void convertir_origenIgualDestino_noLlamaAlServidor() {
        ConversionResultado resultado = controlador.convertir(Magnitud.LONGITUD, "2,5", "m", "m");

        assertTrue(resultado.isExito());
        assertEquals(ConversionControlador.MENSAJE_MISMA_UNIDAD, resultado.getMensaje());
        assertEquals("Origen y destino son la misma unidad; el valor no cambia.", resultado.getMensaje());
        assertEquals(2.5, resultado.getValor(), 0);
        assertEquals(0, conversiones.llamadas);
    }

    @Test
    public void convertir_origenIgualDestino_igualValidaAntes() {
        ConversionResultado resultado = controlador.convertir(Magnitud.MASA, "-1", "kg", "kg");

        assertEquals("No se permiten valores negativos en Masa", resultado.getMensaje());
    }

    @Test
    public void convertir_valorInvalido_noLlamaAlServidor() {
        ConversionResultado resultado = controlador.convertir(Magnitud.AREA, "1e5", "km2", "ac");

        assertEquals("No se admite notación científica", resultado.getMensaje());
        assertEquals(0, conversiones.llamadas);
    }

    @Test
    public void convertir_tokenRechazado_limpiaLaSesionYAvisa() {
        conversiones.respuesta = new ConversionResultado(false, "Invalid Token", 0, true, null);

        ConversionResultado resultado = controlador.convertir(Magnitud.AREA, "1", "km2", "ac");

        assertTrue(resultado.isSesionExpirada());
        assertEquals(ControladorConSesion.MENSAJE_SESION_EXPIRADA, resultado.getMensaje());
        assertFalse(sesion.estaAutenticado());
    }

    @Test
    public void convertir_sinSesion_expiraSinLlamarAlServidor() {
        sesion.limpiar();

        ConversionResultado resultado = controlador.convertir(Magnitud.AREA, "1", "km2", "ac");

        assertTrue(resultado.isSesionExpirada());
        assertEquals(0, conversiones.llamadas);
    }

    @Test
    public void convertir_errorDelServidor_seDevuelveTalCual() {
        conversiones.respuesta = ConversionResultado.error("No se pudo hacer la conversión");

        ConversionResultado resultado = controlador.convertir(Magnitud.AREA, "1", "km2", "ac");

        assertEquals("No se pudo hacer la conversión", resultado.getMensaje());
        assertFalse(resultado.isSesionExpirada());
        assertTrue(sesion.estaAutenticado());
    }

    @Test
    public void obtenerUnidades_sinMagnitud_pideSeleccionar() {
        assertEquals("Seleccione qué desea convertir", controlador.obtenerUnidades(null).getMensaje());
    }

    @Test
    public void obtenerUnidades_tokenRechazado_limpiaLaSesion() {
        catalogo.respuesta = new CatalogoResultado(false, "Token Inválido", Arrays.asList(), true);

        CatalogoResultado resultado = controlador.obtenerUnidades(Magnitud.AREA);

        assertTrue(resultado.isSesionExpirada());
        assertEquals(ControladorConSesion.MENSAJE_SESION_EXPIRADA, resultado.getMensaje());
        assertFalse(sesion.estaAutenticado());
    }

    @Test
    public void filtrarValor_soloTemperaturaAdmiteElSigno() {
        assertEquals("-5", controlador.filtrarValor("-5", Magnitud.TEMPERATURA));
        assertEquals("5", controlador.filtrarValor("-5", Magnitud.LONGITUD));
        assertEquals("5", controlador.filtrarValor("-5", null));
    }
}
