package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Closes the user's session and returns to the login page.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Logging out changes state, so it is only done through POST.
        response.sendRedirect(request.getContextPath() + "/menu");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConversorServicio servicio = SesionUsuario.servicio(request);
        if (servicio != null) {
            servicio.cerrarSesion();
        }
        HttpSession httpSesion = request.getSession(false);
        if (httpSesion != null) {
            try {
                httpSesion.invalidate();
            } catch (IllegalStateException e) {
                // Already invalidated.
            }
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
