package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.monster.controlador.FabricaServicios;
import ec.edu.monster.controlador.LoginServlet;
import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.ws.login.EstadosEnum;
import ec.edu.monster.ws.login.LoginDTO;
import jakarta.servlet.ServletException;
import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginServletTest {

    private static final String VIEW = "/ec/edu/monster/vista/login.jsp";

    private final WebFixture web = new WebFixture();
    private final FabricaServicios factory = mock(FabricaServicios.class);
    private final List<String> loginCalls = new ArrayList<>();

    @BeforeEach
    void registerFactory() throws ServicioException {
        when(web.context.getAttribute(WebFixture.FACTORY_ATTRIBUTE)).thenReturn(factory);
        when(factory.disponible()).thenReturn(true);
        when(factory.crearServicio(any(Sesion.class)))
                .thenAnswer(i -> web.server.service(i.getArgument(0)));
    }

    @Test
    void getShowsEmptyLoginForm() throws ServletException, IOException {
        web.call(new LoginServlet(), "GET");

        assertEquals(VIEW, web.forwardedTo);
        verify(web.dispatcher).forward(web.request, web.response);
        assertEquals("", web.requestAttributes.get("usuario"));
        assertNull(web.requestAttributes.get("error"));
        assertEquals(true, web.requestAttributes.get("servicioDisponible"));
    }

    @Test
    void getReportsServiceUnavailableToTheView() throws ServletException, IOException {
        when(factory.disponible()).thenReturn(false);

        web.call(new LoginServlet(), "GET");

        assertEquals(false, web.requestAttributes.get("servicioDisponible"));
    }

    @Test
    void getRedirectsLoggedInUserToMenu() throws ServletException, IOException {
        web.logIn();

        web.call(new LoginServlet(), "GET");

        verify(web.response).sendRedirect("/app/menu");
        assertNull(web.forwardedTo);
    }

    @Test
    void postWithBlankCredentialsShowsErrorWithoutCallingServer() throws ServletException, IOException, ServicioException {
        web.param("usuario", "user").param("clave", "  ");

        web.call(new LoginServlet(), "POST");

        assertEquals(VIEW, web.forwardedTo);
        assertEquals("Ingrese usuario y contraseña", web.requestAttributes.get("error"));
        assertEquals("user", web.requestAttributes.get("usuario"));
        verify(factory, never()).crearServicio(any());
        verify(web.response, never()).sendRedirect(anyString());
    }

    @Test
    void postWithMissingUserShowsError() throws ServletException, IOException {
        web.param("clave", "1234");

        web.call(new LoginServlet(), "POST");

        assertEquals("Ingrese usuario y contraseña", web.requestAttributes.get("error"));
        assertEquals("", web.requestAttributes.get("usuario"));
    }

    @Test
    void postWithWrongCredentialsShowsServerMessageAndKeepsOnlyUsername() throws ServletException, IOException {
        web.server.login = (usuario, clave) -> loginError("Credenciales incorrectas");
        web.param("usuario", "user").param("clave", "mala");

        web.call(new LoginServlet(), "POST");

        assertEquals(VIEW, web.forwardedTo);
        assertEquals("Credenciales incorrectas", web.requestAttributes.get("error"));
        assertEquals("user", web.requestAttributes.get("usuario"));
        assertFalse(web.requestAttributes.containsValue("mala"), "the password must never reach the view");
        assertNull(web.currentSession(), "no session is created for a failed login");
        verify(web.response, never()).sendRedirect(anyString());
    }

    @Test
    void postWithValidCredentialsStartsFreshSessionAndRedirectsToMenu() throws ServletException, IOException {
        web.server.login = (usuario, clave) -> {
            loginCalls.add(usuario + "/" + clave);
            return loginOk("tok-9");
        };
        WebFixture.FakeSession previous = web.openSession();
        previous.attributes.put("leftover", "state of an earlier visit");
        web.param("usuario", "  user ").param("clave", "1234");

        web.call(new LoginServlet(), "POST");

        assertTrue(previous.invalidated, "the previous session is discarded (session fixation)");
        WebFixture.FakeSession fresh = web.currentSession();
        assertNotNull(fresh);
        assertFalse(fresh.attributes.containsKey("leftover"));
        Sesion sesion = (Sesion) fresh.attributes.get(WebFixture.SESSION_ATTRIBUTE);
        assertTrue(sesion.estaActiva());
        assertEquals("user", sesion.getUsuario());
        assertEquals("tok-9", sesion.getToken());
        assertNotNull(fresh.attributes.get(WebFixture.SERVICE_ATTRIBUTE));
        assertEquals(List.of("user/1234"), loginCalls, "the username is trimmed, the password is not");
        verify(web.response).sendRedirect("/app/menu");
        assertNull(web.forwardedTo, "POST/Redirect/GET: the view is not rendered by the POST");
    }

    @Test
    void postWithValidCredentialsStoresServiceBoundToStoredSession() throws ServletException, IOException,
            ServicioException {
        web.server.login = (usuario, clave) -> loginOk("tok-9");
        web.server.catalogs.put("Masa", List.of("kg"));
        web.param("usuario", "user").param("clave", "1234");

        web.call(new LoginServlet(), "POST");

        ConversorServicio servicio = (ConversorServicio) web.currentSession().attributes
                .get(WebFixture.SERVICE_ATTRIBUTE);
        assertEquals(List.of("kg"), servicio.catalogo("Masa"), "the stored service uses the stored session's token");
        Sesion sesion = (Sesion) web.currentSession().attributes.get(WebFixture.SESSION_ATTRIBUTE);
        servicio.cerrarSesion();
        assertFalse(sesion.estaActiva());
    }

    @Test
    void postWhenServerIsUnreachableShowsConnectionError() throws ServletException, IOException {
        web.server.login = (usuario, clave) -> {
            throw new WebServiceException("connection refused");
        };
        web.param("usuario", "user").param("clave", "1234");

        web.call(new LoginServlet(), "POST");

        assertEquals(VIEW, web.forwardedTo);
        assertEquals("No se pudo conectar con el servidor", web.requestAttributes.get("error"));
        assertNull(web.currentSession());
    }

    @Test
    void postWhenServicesCannotBeBuiltShowsFactoryError() throws ServletException, IOException,
            ServicioException {
        ServicioException unavailable = new ServicioException("No se pudo conectar con el servidor");
        when(factory.crearServicio(any(Sesion.class))).thenThrow(unavailable);
        web.param("usuario", "user").param("clave", "1234");

        web.call(new LoginServlet(), "POST");

        assertSame(unavailable.getMessage(), web.requestAttributes.get("error"));
        verify(web.response, never()).sendRedirect(anyString());
    }

    private static LoginDTO loginOk(String token) {
        LoginDTO dto = new LoginDTO();
        dto.setEstado(EstadosEnum.OK);
        dto.setToken(token);
        return dto;
    }

    private static LoginDTO loginError(String mensaje) {
        LoginDTO dto = new LoginDTO();
        dto.setEstado(EstadosEnum.ERROR);
        dto.setMensaje(mensaje);
        return dto;
    }
}
