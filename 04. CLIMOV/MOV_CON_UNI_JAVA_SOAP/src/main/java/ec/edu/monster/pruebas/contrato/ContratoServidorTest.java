package ec.edu.monster.pruebas.contrato;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import ec.edu.monster.controlador.CatalogoControlador;
import ec.edu.monster.controlador.ConversionControlador;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelos.data.CatalogoCompletoResultado;
import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.modelos.repositorios.CatalogoSoapRepositorio;
import ec.edu.monster.modelos.repositorios.ConversionSoapRepositorio;
import ec.edu.monster.modelos.repositorios.LoginSoapRepositorio;
import ec.edu.monster.modelos.soap.SoapTransporte;
import ec.edu.monster.servicios.SesionServicio;
import ec.edu.monster.utils.FormatoNumero;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContratoServidorTest {

    private static final VerificadorRed CON_RED = () -> false;

    private String url;
    private SesionServicio sesion;
    private ExecutorService hilos;
    private SoapTransporte transporte;

    @Before
    public void preparar() {
        url = System.getProperty("servidor", "");
        assumeFalse("Sin -Pservidor: se omite la prueba de contrato", url.isEmpty());
        if (!url.endsWith("/")) {
            url += "/";
        }
        sesion = new SesionServicio();
        hilos = Executors.newCachedThreadPool();
        transporte = new SoapTransporte();
    }

    @After
    public void liberar() {
        if (hilos != null) {
            hilos.shutdownNow();
        }
    }

    private LoginControlador login() {
        return new LoginControlador(new LoginSoapRepositorio(url, transporte, CON_RED), sesion);
    }

    private ConversionControlador conversion() {
        return new ConversionControlador(new ConversionSoapRepositorio(url, transporte, CON_RED),
                new CatalogoSoapRepositorio(url, transporte, CON_RED), sesion);
    }

    private void iniciarSesion() {
        LoginResultado resultado = login().iniciarSesion("user", "1234");
        assertTrue(resultado.getMensaje(), resultado.isExito());
    }

    @Test
    public void login_credencialesIncorrectas() {
        LoginResultado resultado = login().iniciarSesion("user", "mala");

        assertFalse(resultado.isExito());
        assertEquals("Credenciales Incorrectas", resultado.getMensaje());
    }

    @Test
    public void login_correcto_guardaUnTokenUuid() {
        iniciarSesion();

        UUID.fromString(sesion.getToken());
    }

    @Test
    public void catalogo_lasSeisMagnitudesConSusUnidades() {
        iniciarSesion();
        Object[][] esperadas = {
            {Magnitud.AREA, "cm2,m2,km2,ha,ft2,ac"},
            {Magnitud.LONGITUD, "m,cm,ft,yd,mi"},
            {Magnitud.MASA, "g,kg,oz,lb,qq"},
            {Magnitud.TEMPERATURA, "°C,°F,K,°N,°Ré"},
            {Magnitud.VOLUMEN, "mL,L,m3,ft3,gal"},
            {Magnitud.TIEMPO, "ms,s,min,h,d,w"},
        };
        ConversionControlador controlador = conversion();
        for (Object[] caso : esperadas) {
            CatalogoResultado resultado = controlador.obtenerUnidades((Magnitud) caso[0]);
            assertTrue(resultado.getMensaje(), resultado.isExito());
            assertEquals(caso[0].toString(), new HashSet<>(Arrays.asList(((String) caso[1]).split(","))),
                    new HashSet<>(resultado.getUnidades()));
        }
    }

    @Test
    public void catalogoCompleto_enParalelo() {
        iniciarSesion();

        CatalogoCompletoResultado resultado = new CatalogoControlador(
                new CatalogoSoapRepositorio(url, transporte, CON_RED), sesion, hilos).obtenerCatalogo();

        assertTrue(resultado.getMensaje(), resultado.isExito());
        assertEquals(6, resultado.getGrupos().size());
    }

    @Test
    public void conversiones_conResultadosConocidos() {
        iniciarSesion();
        Object[][] casos = {
            {Magnitud.AREA, "1", "km2", "ac", "247,1052"},
            {Magnitud.LONGITUD, "1234", "cm", "m", "12,34"},
            {Magnitud.MASA, "1", "kg", "g", "1.000"},
            {Magnitud.TEMPERATURA, "100", "°C", "°Ré", "80"},
            {Magnitud.TEMPERATURA, "-40", "°C", "°F", "-40"},
            {Magnitud.TEMPERATURA, "0", "K", "°C", "-273,15"},
            {Magnitud.TEMPERATURA, "33", "°N", "°C", "100"},
            {Magnitud.VOLUMEN, "1", "L", "mL", "1.000"},
            {Magnitud.TIEMPO, "1", "h", "min", "60"},
        };
        ConversionControlador controlador = conversion();
        for (Object[] caso : casos) {
            ConversionResultado resultado = controlador.convertir(
                    (Magnitud) caso[0], (String) caso[1], (String) caso[2], (String) caso[3]);
            String contexto = caso[1] + " " + caso[2] + " → " + caso[3];
            assertTrue(contexto + ": " + resultado.getMensaje(), resultado.isExito());
            assertEquals(contexto, caso[4], FormatoNumero.formatear(resultado.getValor()));
        }
    }

    @Test
    public void conversion_valorMaximo_sinNotacionCientifica() {
        iniciarSesion();

        ConversionResultado resultado = conversion().convertir(Magnitud.TIEMPO, "999999999,999999", "w", "ms");

        assertTrue(resultado.getMensaje(), resultado.isExito());
        assertFalse(FormatoNumero.formatear(resultado.getValor()).contains("E"));
    }

    @Test
    public void tokenDesconocido_expiraLaSesionEnConversionYCatalogo() {
        sesion.iniciar("user", UUID.randomUUID().toString());
        ConversionResultado conversion = conversion().convertir(Magnitud.AREA, "1", "km2", "ac");
        assertTrue(conversion.getMensaje(), conversion.isSesionExpirada());
        assertFalse(sesion.estaAutenticado());

        sesion.iniciar("user", UUID.randomUUID().toString());
        CatalogoCompletoResultado catalogo = new CatalogoControlador(
                new CatalogoSoapRepositorio(url, transporte, CON_RED), sesion, hilos).obtenerCatalogo();
        assertTrue(catalogo.getMensaje(), catalogo.isSesionExpirada());
        assertFalse(sesion.estaAutenticado());
    }

    @Test
    public void rutaDeServicioInexistente_noSeEncuentraElServidor() {
        LoginResultado resultado = new LoginSoapRepositorio(url + "NoExiste/", transporte, CON_RED)
                .iniciarSesion("user", "1234");

        assertTrue(resultado.getMensaje(), resultado.getMensaje().startsWith("No se encontró el servidor en"));
    }

    @Test
    public void puertoSinServidor_noSeEncuentraElServidor() {
        LoginResultado resultado = new LoginSoapRepositorio("http://127.0.0.1:5999/WS/", transporte, CON_RED)
                .iniciarSesion("user", "1234");

        assertTrue(resultado.getMensaje(), resultado.getMensaje().startsWith("No se encontró el servidor en"));
    }
}
