package ec.edu.monster.pruebas;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ec.edu.monster.controlador.AutenticacionFilter;
import ec.edu.monster.modelo.Sesion;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class AutenticacionFilterTest {

    private final WebFixture web = new WebFixture();
    private final FilterChain chain = mock(FilterChain.class);

    @Test
    void redirectsToLoginWhenThereIsNoSession() throws IOException, ServletException {
        new AutenticacionFilter().doFilter(web.request, web.response, chain);

        verify(web.response).sendRedirect("/app/login");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void redirectsToLoginWhenSessionHasNoLoggedInUser() throws IOException, ServletException {
        web.openSession().attributes.put(WebFixture.SESSION_ATTRIBUTE, new Sesion());

        new AutenticacionFilter().doFilter(web.request, web.response, chain);

        verify(web.response).sendRedirect("/app/login");
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void redirectsToLoginAfterUserSessionWasClosed() throws IOException, ServletException {
        web.logIn().cerrar();

        new AutenticacionFilter().doFilter(web.request, web.response, chain);

        verify(web.response).sendRedirect("/app/login");
        verify(chain, never()).doFilter(any(), any());
        verify(web.response, never()).setHeader(anyString(), anyString());
    }

    @Test
    void letsActiveSessionThroughWithoutBrowserCaching() throws IOException, ServletException {
        web.logIn();

        new AutenticacionFilter().doFilter(web.request, web.response, chain);

        verify(chain).doFilter(web.request, web.response);
        verify(web.response).setHeader("Cache-Control", "no-store");
        verify(web.response, never()).sendRedirect(anyString());
    }
}
