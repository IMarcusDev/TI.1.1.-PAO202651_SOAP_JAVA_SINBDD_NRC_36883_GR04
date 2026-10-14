package ec.edu.monster.modelo;

import ec.edu.monster.ws.conversor.CatalogDTO;
import ec.edu.monster.ws.conversor.ConversorWebService;
import ec.edu.monster.ws.conversor.ConversorWebService_Service;
import ec.edu.monster.ws.conversor.ConverterDTO;
import ec.edu.monster.ws.login.LoginDTO;
import ec.edu.monster.ws.login.LoginWebService;
import ec.edu.monster.ws.login.LoginWebService_Service;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Single entry point to the SOAP server. Controllers depend on this class,
 * never on the generated wsimport stubs.
 */
public class ConversorServicio {

    private static final String SIN_CONEXION = "No se pudo conectar con el servidor";
    private static final String SIN_SESION = "La sesión no está activa";
    private static final String SIN_RESPUESTA = "El servidor no devolvió una respuesta";
    private static final String SIN_TOKEN = "El servidor no devolvió un token de sesión";
    private static final String ERROR_GENERICO = "El servidor respondió con un error";
    private static final String CONNECT_TIMEOUT = "com.sun.xml.ws.connect.timeout";
    private static final String REQUEST_TIMEOUT = "com.sun.xml.ws.request.timeout";
    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int REQUEST_TIMEOUT_MS = 15_000;

    private final LoginWebService login;
    private final ConversorWebService conversor;
    private final Sesion sesion;

    public ConversorServicio(LoginWebService login, ConversorWebService conversor, Sesion sesion) {
        this.login = login;
        this.conversor = conversor;
        this.sesion = sesion;
    }

    public static ConversorServicio crear(Sesion sesion) {
        return crear(sesion, new LoginWebService_Service(), new ConversorWebService_Service());
    }

    // Reuses services built once per application; ports stay per caller.
    public static ConversorServicio crear(Sesion sesion, LoginWebService_Service loginServicio,
            ConversorWebService_Service conversorServicio) {
        return new ConversorServicio(
                conTimeouts(loginServicio.getLoginWebServicePort()),
                conTimeouts(conversorServicio.getConversorWebServicePort()),
                sesion);
    }

    // The server is shared by several clients and may stall: fail instead of waiting forever.
    private static <T> T conTimeouts(T puerto) {
        var contexto = ((BindingProvider) puerto).getRequestContext();
        contexto.put(CONNECT_TIMEOUT, CONNECT_TIMEOUT_MS);
        contexto.put(REQUEST_TIMEOUT, REQUEST_TIMEOUT_MS);
        return puerto;
    }

    public void iniciarSesion(String usuario, String clave) throws ServicioException {
        // A new login attempt replaces the previous session, even if it fails.
        sesion.cerrar();
        LoginDTO respuesta = llamar(() -> login.login(usuario, clave));
        if (respuesta == null) {
            throw new ServicioException(SIN_RESPUESTA);
        }
        if (respuesta.getEstado() != ec.edu.monster.ws.login.EstadosEnum.OK) {
            throw new ServicioException(mensajeOGenerico(respuesta.getMensaje()));
        }
        if (respuesta.getToken() == null || respuesta.getToken().isBlank()) {
            throw new ServicioException(SIN_TOKEN);
        }
        sesion.iniciar(usuario, respuesta.getToken());
    }

    public double convertir(String magnitud, double valor, String desde, String hacia) throws ServicioException {
        String token = tokenActivo();
        ConverterDTO respuesta = llamar(() -> conversor.convert(token, magnitud, valor, desde, hacia));
        validar(respuesta);
        return respuesta.getResultado();
    }

    public List<String> catalogo(String magnitud) throws ServicioException {
        String token = tokenActivo();
        CatalogDTO respuesta = llamar(() -> conversor.catalog(token, magnitud));
        validar(respuesta);
        if (respuesta.getResultado() == null) {
            return List.of();
        }
        return List.copyOf(respuesta.getResultado().getUnidad());
    }

    public void cerrarSesion() {
        sesion.cerrar();
    }

    private String tokenActivo() throws ServicioException {
        if (!sesion.estaActiva()) {
            throw new ServicioException(SIN_SESION);
        }
        return sesion.getToken();
    }

    private static void validar(ec.edu.monster.ws.conversor.ResponseDTO respuesta) throws ServicioException {
        if (respuesta == null) {
            throw new ServicioException(SIN_RESPUESTA);
        }
        if (respuesta.getEstado() != ec.edu.monster.ws.conversor.EstadosEnum.OK) {
            throw new ServicioException(mensajeOGenerico(respuesta.getMensaje()));
        }
    }

    private static <T> T llamar(Supplier<T> llamada) throws ServicioException {
        try {
            return llamada.get();
        } catch (SOAPFaultException e) {
            // The server answered with a fault: show its message, not a connection error.
            throw new ServicioException(mensajeOGenerico(e.getFault().getFaultString()), e);
        } catch (WebServiceException e) {
            throw new ServicioException(SIN_CONEXION, e);
        }
    }

    private static String mensajeOGenerico(String mensaje) {
        return mensaje == null || mensaje.isBlank() ? ERROR_GENERICO : mensaje;
    }
}
