package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import ec.edu.monster.controlador.LogoutServlet;
import ec.edu.monster.modelo.Sesion;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class LogoutServletTest {

    private final WebFixture web = new WebFixture();

    @Test
    void postClosesUserSessionInvalidatesHttpSessionAndRedirectsToLogin() throws ServletException, IOException {
        Sesion sesion = web.logIn();
        WebFixture.FakeSession session = web.currentSession();

        web.call(new LogoutServlet(), "POST");

        assertFalse(sesion.estaActiva());
        assertTrue(session.invalidated);
        assertNull(web.currentSession());
        verify(web.response).sendRedirect("/app/login");
    }

    @Test
    void postWithoutSessionStillRedirectsToLogin() throws ServletException, IOException {
        web.call(new LogoutServlet(), "POST");

        verify(web.response).sendRedirect("/app/login");
    }

    @Test
    void getDoesNotLogOut() throws ServletException, IOException {
        Sesion sesion = web.logIn();
        WebFixture.FakeSession session = web.currentSession();

        web.call(new LogoutServlet(), "GET");

        assertTrue(sesion.estaActiva());
        assertFalse(session.invalidated);
        verify(web.response).sendRedirect("/app/menu");
    }
}
