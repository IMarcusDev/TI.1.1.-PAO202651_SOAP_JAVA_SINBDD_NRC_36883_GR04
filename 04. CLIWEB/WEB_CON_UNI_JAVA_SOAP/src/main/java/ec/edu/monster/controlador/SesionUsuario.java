package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.Sesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Per-user state kept in the HttpSession: each user has its own Sesion and
 * ConversorServicio. Nothing here is shared between users.
 */
final class SesionUsuario {

    static final String SESION = "sesion";
    static final String SERVICIO = "servicio";

    private SesionUsuario() {
    }

    static void guardar(HttpSession httpSesion, Sesion sesion, ConversorServicio servicio) {
        httpSesion.setAttribute(SESION, sesion);
        httpSesion.setAttribute(SERVICIO, servicio);
    }

    static boolean activa(HttpServletRequest request) {
        HttpSession httpSesion = request.getSession(false);
        if (httpSesion == null) {
            return false;
        }
        try {
            Sesion sesion = (Sesion) httpSesion.getAttribute(SESION);
            return sesion != null && sesion.estaActiva() && httpSesion.getAttribute(SERVICIO) != null;
        } catch (IllegalStateException e) {
            // The session was invalidated concurrently.
            return false;
        }
    }

    static ConversorServicio servicio(HttpServletRequest request) {
        HttpSession httpSesion = request.getSession(false);
        return httpSesion == null ? null : (ConversorServicio) httpSesion.getAttribute(SERVICIO);
    }
}
