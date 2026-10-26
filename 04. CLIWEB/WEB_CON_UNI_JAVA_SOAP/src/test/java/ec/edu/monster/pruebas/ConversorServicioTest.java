package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.ws.conversor.CatalogDTO;
import ec.edu.monster.ws.conversor.ConversorWebService;
import ec.edu.monster.ws.conversor.ConverterDTO;
import ec.edu.monster.ws.login.LoginDTO;
import ec.edu.monster.ws.login.LoginWebService;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.List;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

class ConversorServicioTest {

    private final Sesion sesion = new Sesion();

    @Test
    void storesTokenWhenLoginSucceeds() throws ServicioException {
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> loginOk("tok-1"), new ConversorFalso(), sesion);

        servicio.iniciarSesion("user", "1234");

        assertTrue(sesion.estaActiva());
        assertEquals("user", sesion.getUsuario());
        assertEquals("tok-1", sesion.getToken());
    }

    @Test
    void throwsServerMessageWhenLoginFails() {
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> loginError("Credenciales incorrectas"), new ConversorFalso(), sesion);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.iniciarSesion("user", "mala"));

        assertEquals("Credenciales incorrectas", error.getMessage());
        assertFalse(sesion.estaActiva());
    }

    @Test
    void convertsUsingSessionToken() throws ServicioException {
        ConversorFalso conversor = new ConversorFalso();
        conversor.respuestaConversion = conversionOk(328.084);
        ConversorServicio servicio = servicioConSesion(conversor);

        double resultado = servicio.convertir("Longitud", 100, "m", "ft");

        assertEquals(328.084, resultado);
        assertEquals("tok-1", conversor.ultimoToken);
    }

    @Test
    void throwsServerMessageWhenConversionFails() {
        ConversorFalso conversor = new ConversorFalso();
        conversor.respuestaConversion = conversionError("Unidad no soportada");
        ConversorServicio servicio = servicioConSesion(conversor);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.convertir("Longitud", 1, "m", "xx"));

        assertEquals("Unidad no soportada", error.getMessage());
    }

    @Test
    void rejectsConversionWithoutSession() {
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> loginOk("tok-1"), new ConversorFalso(), sesion);

        assertThrows(ServicioException.class, () -> servicio.convertir("Longitud", 1, "m", "ft"));
    }

    @Test
    void returnsCatalogUnits() throws ServicioException {
        ConversorFalso conversor = new ConversorFalso();
        conversor.respuestaCatalogo = catalogoOk("m", "ft", "km");
        ConversorServicio servicio = servicioConSesion(conversor);

        assertEquals(List.of("m", "ft", "km"), servicio.catalogo("Longitud"));
    }

    @Test
    void translatesConnectionFailures() {
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> {
                    throw new WebServiceException("connection refused");
                }, new ConversorFalso(), sesion);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.iniciarSesion("user", "1234"));

        assertEquals("No se pudo conectar con el servidor", error.getMessage());
    }

    @Test
    void rejectsSuccessfulLoginWithoutToken() {
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> loginOk(null), new ConversorFalso(), sesion);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.iniciarSesion("user", "1234"));

        assertEquals("El servidor no devolvió un token de sesión", error.getMessage());
        assertFalse(sesion.estaActiva());
    }

    @Test
    void reportsSoapFaultMessageInsteadOfConnectionError() throws SOAPException {
        SOAPFault fault = SOAPFactory.newInstance().createFault("Error interno del servidor",
                new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server"));
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> {
                    throw new SOAPFaultException(fault);
                }, new ConversorFalso(), sesion);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.iniciarSesion("user", "1234"));

        assertEquals("Error interno del servidor", error.getMessage());
    }

    @Test
    void usesGenericMessageWhenLoginErrorHasNoMessage() {
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> loginError(null), new ConversorFalso(), sesion);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.iniciarSesion("user", "1234"));

        assertEquals("El servidor respondió con un error", error.getMessage());
    }

    @Test
    void usesGenericMessageWhenConversionErrorHasNoMessage() {
        ConversorFalso conversor = new ConversorFalso();
        conversor.respuestaConversion = conversionError(" ");
        ConversorServicio servicio = servicioConSesion(conversor);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.convertir("Longitud", 1, "m", "ft"));

        assertEquals("El servidor respondió con un error", error.getMessage());
    }

    @Test
    void usesGenericMessageWhenSoapFaultHasNoText() throws SOAPException {
        // SOAP 1.1 requires a faultstring, so a real server sends it at least blank.
        SOAPFault fault = SOAPFactory.newInstance().createFault(" ",
                new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server"));
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> {
                    throw new SOAPFaultException(fault);
                }, new ConversorFalso(), sesion);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.iniciarSesion("user", "1234"));

        assertEquals("El servidor respondió con un error", error.getMessage());
    }

    @Test
    void failedReloginClearsPreviousSession() {
        sesion.iniciar("anterior", "tok-viejo");
        ConversorServicio servicio = new ConversorServicio(
                (usuario, clave) -> loginError("Credenciales incorrectas"), new ConversorFalso(), sesion);

        assertThrows(ServicioException.class, () -> servicio.iniciarSesion("otro", "mala"));

        assertFalse(sesion.estaActiva());
    }

    @Test
    void returnsEmptyCatalogWhenServerSendsNoUnits() throws ServicioException {
        ConversorFalso conversor = new ConversorFalso();
        CatalogDTO vacio = new CatalogDTO();
        vacio.setEstado(ec.edu.monster.ws.conversor.EstadosEnum.OK);
        conversor.respuestaCatalogo = vacio;
        ConversorServicio servicio = servicioConSesion(conversor);

        assertEquals(List.of(), servicio.catalogo("Longitud"));
    }

    @Test
    void throwsServerMessageWhenCatalogFails() {
        ConversorFalso conversor = new ConversorFalso();
        CatalogDTO fallido = new CatalogDTO();
        fallido.setEstado(ec.edu.monster.ws.conversor.EstadosEnum.ERROR);
        fallido.setMensaje("Magnitud no soportada");
        conversor.respuestaCatalogo = fallido;
        ConversorServicio servicio = servicioConSesion(conversor);

        ServicioException error = assertThrows(ServicioException.class,
                () -> servicio.catalogo("Energia"));

        assertEquals("Magnitud no soportada", error.getMessage());
    }

    @Test
    void closesSession() throws ServicioException {
        ConversorServicio servicio = servicioConSesion(new ConversorFalso());

        servicio.cerrarSesion();

        assertFalse(sesion.estaActiva());
    }

    private ConversorServicio servicioConSesion(ConversorWebService conversor) {
        sesion.iniciar("user", "tok-1");
        return new ConversorServicio((usuario, clave) -> loginOk("tok-1"), conversor, sesion);
    }

    private static LoginDTO loginOk(String token) {
        LoginDTO dto = new LoginDTO();
        dto.setEstado(ec.edu.monster.ws.login.EstadosEnum.OK);
        dto.setToken(token);
        return dto;
    }

    private static LoginDTO loginError(String mensaje) {
        LoginDTO dto = new LoginDTO();
        dto.setEstado(ec.edu.monster.ws.login.EstadosEnum.ERROR);
        dto.setMensaje(mensaje);
        return dto;
    }

    private static ConverterDTO conversionOk(double resultado) {
        ConverterDTO dto = new ConverterDTO();
        dto.setEstado(ec.edu.monster.ws.conversor.EstadosEnum.OK);
        dto.setResultado(resultado);
        return dto;
    }

    private static ConverterDTO conversionError(String mensaje) {
        ConverterDTO dto = new ConverterDTO();
        dto.setEstado(ec.edu.monster.ws.conversor.EstadosEnum.ERROR);
        dto.setMensaje(mensaje);
        return dto;
    }

    private static CatalogDTO catalogoOk(String... unidades) {
        CatalogDTO dto = new CatalogDTO();
        dto.setEstado(ec.edu.monster.ws.conversor.EstadosEnum.OK);
        CatalogDTO.Resultado resultado = new CatalogDTO.Resultado();
        resultado.getUnidad().addAll(List.of(unidades));
        dto.setResultado(resultado);
        return dto;
    }

    private static final class ConversorFalso implements ConversorWebService {

        private ConverterDTO respuestaConversion = conversionOk(0);
        private CatalogDTO respuestaCatalogo = catalogoOk();
        private String ultimoToken;

        @Override
        public ConverterDTO convert(String token, String type, double value, String from, String to) {
            ultimoToken = token;
            return respuestaConversion;
        }

        @Override
        public CatalogDTO catalog(String token, String type) {
            ultimoToken = token;
            return respuestaCatalogo;
        }
    }
}
