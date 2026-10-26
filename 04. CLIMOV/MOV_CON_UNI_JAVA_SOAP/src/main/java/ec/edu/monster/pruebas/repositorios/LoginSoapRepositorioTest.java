package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.modelos.repositorios.LoginSoapRepositorio;
import ec.edu.monster.modelos.repositorios.SoapErrores;
import ec.edu.monster.pruebas.apoyo.TransporteFalso;
import java.net.ConnectException;
import org.junit.Test;

public class LoginSoapRepositorioTest {

    private static final String URL_BASE = "http://servidor:8080/WS_CON_UNI_JAVA_SOAP/";

    private static LoginSoapRepositorio repositorio(TransporteFalso transporte) {
        return new LoginSoapRepositorio(URL_BASE, transporte, () -> false);
    }

    @Test
    public void iniciarSesion_ok_devuelveElTokenYEnviaLasCredenciales() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("login",
                "<estado>OK</estado><mensaje>Ingreso de Sesión Exitoso!</mensaje><token>abc-123</token>"));

        LoginResultado resultado = repositorio(transporte).iniciarSesion("user", "1234");

        assertTrue(resultado.isExito());
        assertEquals("abc-123", resultado.getToken());
        assertEquals(URL_BASE + "LoginWebService", transporte.ultimaUrl());
        assertTrue(transporte.ultimoSobre().contains(
                "<ws:login><username>user</username><password>1234</password></ws:login>"));
    }

    @Test
    public void iniciarSesion_credencialesIncorrectas_muestraElMensajeDelServidor() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("login",
                "<estado>ERROR</estado><mensaje>Credenciales Incorrectas</mensaje><token></token>"));

        LoginResultado resultado = repositorio(transporte).iniciarSesion("user", "x");

        assertFalse(resultado.isExito());
        assertEquals("Credenciales Incorrectas", resultado.getMensaje());
    }

    @Test
    public void iniciarSesion_okSinToken_esRespuestaIncompleta() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("login",
                "<estado>OK</estado><mensaje>ok</mensaje><token></token>"));

        assertEquals(SoapErrores.RESPUESTA_INCOMPLETA, repositorio(transporte).iniciarSesion("user", "1234").getMensaje());
    }

    @Test
    public void iniciarSesion_respuestaSinEstado_esRespuestaIncompleta() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("login", "<token>t</token>"));

        assertEquals(SoapErrores.RESPUESTA_INCOMPLETA, repositorio(transporte).iniciarSesion("user", "1234").getMensaje());
    }

    @Test
    public void iniciarSesion_sinRed_noLlamaAlServidor() {
        TransporteFalso transporte = TransporteFalso.responde("");
        LoginSoapRepositorio repositorio = new LoginSoapRepositorio(URL_BASE, transporte, () -> true);

        assertEquals(SoapErrores.SIN_CONEXION, repositorio.iniciarSesion("user", "1234").getMensaje());
        assertEquals(0, transporte.peticiones());
    }

    @Test
    public void iniciarSesion_conexionRechazada_noLanzaYDevuelveElMensaje() {
        TransporteFalso transporte = TransporteFalso.lanza(new ConnectException("refused"));

        LoginResultado resultado = repositorio(transporte).iniciarSesion("user", "1234");

        assertFalse(resultado.isExito());
        assertTrue(resultado.getMensaje(), resultado.getMensaje().startsWith("No se encontró el servidor en"));
    }
}
