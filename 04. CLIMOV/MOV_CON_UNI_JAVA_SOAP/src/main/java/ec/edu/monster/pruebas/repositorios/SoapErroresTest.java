package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.BuildConfig;
import ec.edu.monster.modelos.excepciones.HttpEstadoException;
import ec.edu.monster.modelos.excepciones.RespuestaInvalidaException;
import ec.edu.monster.modelos.excepciones.SoapFallaException;
import ec.edu.monster.modelos.repositorios.SoapErrores;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import org.junit.Test;

public class SoapErroresTest {

    private static final String NO_ENCONTRADO = "No se encontró el servidor en http://192.168.1.2:8080/WS_CON_UNI_JAVA_SOAP/. "
            + "Verifique que esté encendido y que la IP sea correcta.";
    private static final String SIN_COMUNICACION = "No se pudo comunicar con el servidor. Verifique que esté encendido y en la misma red.";

    @Test
    public void mensaje_cadaExcepcion_tieneSuTextoEnEspanol() {
        Object[][] casos = {
            {new ConnectException("Connection refused"), NO_ENCONTRADO},
            {new UnknownHostException("servidor"), NO_ENCONTRADO},
            {new NoRouteToHostException("sin ruta"), NO_ENCONTRADO},
            {new HttpEstadoException(404, "http://x/LoginWebService"), NO_ENCONTRADO},
            {new SocketTimeoutException("timeout"), "El servidor no respondió en 15 segundos. Intente de nuevo."},
            {new SoapFallaException("java.lang.NullPointerException"),
                    "El servidor no pudo procesar la solicitud: java.lang.NullPointerException"},
            {new RespuestaInvalidaException("vacía", null), SoapErrores.RESPUESTA_INCOMPLETA},
            {new MalformedURLException("no protocol"), "La dirección del servidor está mal configurada."},
            {new UnknownServiceException("CLEARTEXT communication not permitted"), SIN_COMUNICACION},
            {new HttpEstadoException(503, "http://x"), SIN_COMUNICACION},
            {new IOException("reset"), SIN_COMUNICACION},
            {new IllegalStateException("x"), "Ocurrió un error inesperado al contactar al servidor."},
        };
        for (Object[] caso : casos) {
            Throwable error = (Throwable) caso[0];
            String mensaje = SoapErrores.mensaje(error);
            assertTrue(error.getClass().getSimpleName() + " → " + mensaje, mensaje.startsWith((String) caso[1]));
        }
    }

    @Test
    public void mensaje_enDebug_agregaLaCausaMasInterna() {
        String mensaje = SoapErrores.mensaje(new IOException("envoltura", new ConnectException("refused")));

        if (BuildConfig.DEBUG) {
            assertTrue(mensaje, mensaje.endsWith("\n[Debug] ConnectException: refused"));
        } else {
            assertFalse(mensaje, mensaje.contains("[Debug]"));
        }
    }

    @Test
    public void esTokenInvalido_reconoceLosDosTextosDelServidor() {
        assertTrue(SoapErrores.esTokenInvalido("Token Inválido"));
        assertTrue(SoapErrores.esTokenInvalido("Invalid Token"));
        assertFalse(SoapErrores.esTokenInvalido("Credenciales Incorrectas"));
        assertFalse(SoapErrores.esTokenInvalido("Tipo de Catálogo Inexistente"));
        assertFalse(SoapErrores.esTokenInvalido(null));
    }

    @Test
    public void mensajeServidor_vacio_usaElMensajePorDefecto() {
        assertEquals("Por defecto", SoapErrores.mensajeServidor("  ", "Por defecto"));
        assertEquals("Por defecto", SoapErrores.mensajeServidor(null, "Por defecto"));
        assertEquals("Credenciales Incorrectas", SoapErrores.mensajeServidor(" Credenciales Incorrectas ", "x"));
    }
}
