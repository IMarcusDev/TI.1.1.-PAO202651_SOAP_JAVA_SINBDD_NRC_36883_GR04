package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.ws.conversor.ConversorWebService_Service;
import ec.edu.monster.ws.login.LoginWebService_Service;
import jakarta.servlet.ServletContext;

/**
 * Application-scoped holder of the WSDL-backed services. They are expensive,
 * so they are built once and shared; every user gets its own ports.
 */
public class FabricaServicios {

    static final String ATRIBUTO = FabricaServicios.class.getName();
    private static final String SIN_CONEXION = "No se pudo conectar con el servidor";

    private LoginWebService_Service loginServicio;
    private ConversorWebService_Service conversorServicio;

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

    /** True when the services could be built; tries again if a previous attempt failed. */
    public boolean disponible() {
        try {
            cargarServicios();
            return true;
        } catch (ServicioException e) {
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
