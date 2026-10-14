package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.excepciones.SoapException;
import ec.edu.monster.modelos.repositorios.AutenticacionSoapRepositorio;
import ec.edu.monster.modelos.soap.SoapTransporte;
import org.junit.Test;

public class AutenticacionSoapRepositorioTest {

    private static class TransporteFalso extends SoapTransporte {
        private final String respuesta;
        String peticionRecibida;

        TransporteFalso(String respuesta) {
            this.respuesta = respuesta;
        }

        @Override
        public String enviar(String url, String sobre) {
            this.peticionRecibida = sobre;
            return respuesta;
        }
    }

    private static String respuestaLogin(String contenido) {
        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body>"
             + "<ns2:loginResponse xmlns:ns2=\"http://ws.monster.edu.ec/\">"
             + "<return>" + contenido + "</return>"
             + "</ns2:loginResponse></S:Body></S:Envelope>";
    }

    @Test
    public void iniciarSesion_credencialesValidas_devuelveElToken() throws SoapException {
        TransporteFalso transporte = new TransporteFalso(respuestaLogin(
                "<estado>OK</estado><mensaje>Ingreso de Sesión Exitoso!</mensaje>"
              + "<token>bb989ed5-e249-4f5f-8baf-76f80f2e842f</token>"));
        AutenticacionSoapRepositorio cliente = new AutenticacionSoapRepositorio("http://servidor/Login", transporte);

        String token = cliente.iniciarSesion("user", "1234");

        assertEquals("bb989ed5-e249-4f5f-8baf-76f80f2e842f", token);
        assertTrue(transporte.peticionRecibida.contains(
                "<username>user</username><password>1234</password>"));
    }

    @Test
    public void iniciarSesion_estadoError_lanzaExcepcionConElMensajeDelServidor() {
        TransporteFalso transporte = new TransporteFalso(respuestaLogin(
                "<estado>ERROR</estado><mensaje>Credenciales Incorrectas</mensaje><token></token>"));
        AutenticacionSoapRepositorio cliente = new AutenticacionSoapRepositorio("http://servidor/Login", transporte);

        SoapException error = assertThrows(SoapException.class,
                () -> cliente.iniciarSesion("cualquiera", "incorrecta"));

        assertEquals("Credenciales Incorrectas", error.getMessage());
    }

    @Test
    public void iniciarSesion_estadoOkSinToken_lanzaExcepcion() {
        TransporteFalso transporte = new TransporteFalso(respuestaLogin(
                "<estado>OK</estado><mensaje>Ingreso de Sesión Exitoso!</mensaje><token></token>"));
        AutenticacionSoapRepositorio cliente = new AutenticacionSoapRepositorio("http://servidor/Login", transporte);

        assertThrows(SoapException.class, () -> cliente.iniciarSesion("user", "1234"));
    }
}