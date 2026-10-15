package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.Sesion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Login page: validates the input, asks the model to log in and, on success,
 * stores the user's own service in a fresh HttpSession.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String CREDENCIALES_VACIAS = "Ingrese usuario y contraseña";
    private static final String VISTA = "/ec/edu/monster/vista/login.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SesionUsuario.activa(request)) {
            response.sendRedirect(request.getContextPath() + "/menu");
            return;
        }
        mostrar(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");
        if (estaVacio(usuario) || estaVacio(clave)) {
            mostrar(request, response, usuario, CREDENCIALES_VACIAS);
            return;
        }
        Sesion sesion = new Sesion();
        try {
            ConversorServicio servicio = FabricaServicios.de(getServletContext()).crearServicio(sesion);
            servicio.iniciarSesion(usuario.trim(), clave);
            HttpSession httpSesion = nuevaSesion(request);
            SesionUsuario.guardar(httpSesion, sesion, servicio);
            response.sendRedirect(request.getContextPath() + "/menu");
        } catch (ServicioException e) {
            mostrar(request, response, usuario, e.getMessage());
        }
    }

    // Discards any previous session so its id and state are never reused (session fixation).
    private static HttpSession nuevaSesion(HttpServletRequest request) {
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            try {
                anterior.invalidate();
            } catch (IllegalStateException e) {
                // Already invalidated.
            }
        }
        return request.getSession(true);
    }

    private void mostrar(HttpServletRequest request, HttpServletResponse response, String usuario, String error)
            throws ServletException, IOException {
        // The password is never sent back to the page.
        request.setAttribute("usuario", usuario == null ? "" : usuario);
        request.setAttribute("error", error);
        request.setAttribute("servicioDisponible", FabricaServicios.de(getServletContext()).disponible());
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
