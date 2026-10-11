package ec.edu.monster.pruebas.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.soap.AutenticacionSoapCliente;
import ec.edu.monster.modelo.soap.SoapTransporte;
import org.junit.Test;

public class AutenticacionSoapClienteTest {

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
             + "<ns2:loginResponse xmlns:ns2=\"http://ws.monster.edu.ec/\">" + contenido
             + "</ns2:loginResponse></S:Body></S:Envelope>";
    }

    @Test
    public void iniciarSesion_credencialesValidas_devuelveElToken() throws ServicioException {
        TransporteFalso transporte = new TransporteFalso(
                respuestaLogin("<return>bb989ed5-e249-4f5f-8baf-76f80f2e842f</return>"));
        AutenticacionSoapCliente cliente = new AutenticacionSoapCliente("http://servidor/Login", transporte);

        String token = cliente.iniciarSesion("user", "1234");

        assertEquals("bb989ed5-e249-4f5f-8baf-76f80f2e842f", token);
        assertTrue(transporte.peticionRecibida.contains(
                "<username>user</username><password>1234</password>"));
    }

    @Test
    public void iniciarSesion_sinTokenEnLaRespuesta_lanzaExcepcion() {
        TransporteFalso transporte = new TransporteFalso(respuestaLogin(""));
        AutenticacionSoapCliente cliente = new AutenticacionSoapCliente("http://servidor/Login", transporte);

        ServicioException error = assertThrows(ServicioException.class,
                () -> cliente.iniciarSesion("user", "incorrecta"));

        assertEquals("Usuario o contraseña incorrectos", error.getMessage());
    }
}