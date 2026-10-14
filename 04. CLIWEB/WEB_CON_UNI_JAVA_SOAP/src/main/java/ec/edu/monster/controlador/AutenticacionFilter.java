package ec.edu.monster.controlador;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Protected pages require an active session; otherwise the user is sent to
 * the login page.
 */
@WebFilter(urlPatterns = {"/menu", "/conversion", "/catalogo", "/logout"})
public class AutenticacionFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain cadena)
            throws IOException, ServletException {
        if (!SesionUsuario.activa(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // Protected pages must not be served from the browser cache after logout.
        response.setHeader("Cache-Control", "no-store");
        cadena.doFilter(request, response);
    }
}
