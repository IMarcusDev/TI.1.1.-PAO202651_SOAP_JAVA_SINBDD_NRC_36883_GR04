package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.ws.conversor.ConversorWebService_Service;
import ec.edu.monster.ws.login.LoginWebService_Service;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Application-scoped holder of the WSDL-backed services. They are expensive,
 * so they are built once and shared; every user gets its own ports.
 */
public class FabricaServicios {

    static final String ATRIBUTO = FabricaServicios.class.getName();
    private static final String SIN_CONEXION = "No se pudo conectar con el servidor";
    private static final URI WSDL_CONVERSOR =
            URI.create("http://localhost:8080/WS_CON_UNI_JAVA_SOAP/ConversorWebService?wsdl");
    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    private final URI wsdl;
    private final HttpClient cliente = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private LoginWebService_Service loginServicio;
    private ConversorWebService_Service conversorServicio;

    public FabricaServicios() {
        this(WSDL_CONVERSOR);
    }

    /** Points the availability check at the given WSDL address. */
    public FabricaServicios(URI wsdl) {
        this.wsdl = wsdl;
    }

    public static FabricaServicios de(ServletContext contexto) {
        return (FabricaServicios) contexto.getAttribute(ATRIBUTO);
    }

    /** Builds a new per-user service bound to the given session. */
    public ConversorServicio crearServicio(Sesion sesion) throws ServicioException {
        cargarServicios();
        try {
            return ConversorServicio.crear(sesion, loginServicio, conversorServicio);
        } catch (RuntimeException e) {
            throw new ServicioException(SIN_CONEXION, e);
        }
    }

    /**
     * True when the server answers its WSDL right now. It asks the server on every call,
     * because the services once built stay in memory even after the server goes down.
     */
    public boolean disponible() {
        HttpRequest peticion = HttpRequest.newBuilder(wsdl).timeout(TIMEOUT).GET().build();
        try {
            HttpResponse<Void> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.discarding());
            return respuesta.statusCode() == 200;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // A failed attempt leaves the fields empty so a later request retries.
    private synchronized void cargarServicios() throws ServicioException {
        if (loginServicio != null && conversorServicio != null) {
            return;
        }
        try {
            LoginWebService_Service login = new LoginWebService_Service();
            ConversorWebService_Service conversor = new ConversorWebService_Service();
            loginServicio = login;
            conversorServicio = conversor;
        } catch (RuntimeException e) {
            throw new ServicioException(SIN_CONEXION, e);
        }
    }
}
