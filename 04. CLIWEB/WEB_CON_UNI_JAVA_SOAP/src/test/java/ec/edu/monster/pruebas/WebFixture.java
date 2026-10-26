package ec.edu.monster.pruebas;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ec.edu.monster.controlador.FabricaServicios;
import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.ws.conversor.CatalogDTO;
import ec.edu.monster.ws.conversor.ConversorWebService;
import ec.edu.monster.ws.conversor.ConverterDTO;
import ec.edu.monster.ws.conversor.EstadosEnum;
import ec.edu.monster.ws.login.LoginWebService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One HTTP exchange against a servlet or filter: Mockito request/response
 * backed by real attribute maps, a stateful HttpSession and a fake SOAP server
 * reached through the real ConversorServicio.
 */
final class WebFixture {

    static final String CONTEXT_PATH = "/app";
    static final String FACTORY_ATTRIBUTE = FabricaServicios.class.getName();
    // Attribute names used by SesionUsuario (package-private in the controller package).
    static final String SESSION_ATTRIBUTE = "sesion";
    static final String SERVICE_ATTRIBUTE = "servicio";

    final HttpServletRequest request = mock(HttpServletRequest.class);
    final HttpServletResponse response = mock(HttpServletResponse.class);
    final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
    final ServletContext context = mock(ServletContext.class);
    final Map<String, Object> requestAttributes = new HashMap<>();
    final Map<String, String> parameters = new HashMap<>();
    final List<FakeSession> sessions = new ArrayList<>();
    final FakeServer server = new FakeServer();
    String forwardedTo;
    private FakeSession current;

    WebFixture() {
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(request.getParameter(anyString())).thenAnswer(i -> parameters.get(i.<String>getArgument(0)));
        when(request.getAttribute(anyString())).thenAnswer(i -> requestAttributes.get(i.<String>getArgument(0)));
        doAnswer(i -> {
            requestAttributes.put(i.getArgument(0), i.getArgument(1));
            return null;
        }).when(request).setAttribute(anyString(), any());
        when(request.getSession(false)).thenAnswer(i -> current == null ? null : current.mock);
        when(request.getSession(true)).thenAnswer(i -> openSession().mock);
        when(request.getSession()).thenAnswer(i -> openSession().mock);
        when(request.getRequestDispatcher(anyString())).thenAnswer(i -> {
            forwardedTo = i.getArgument(0);
            return dispatcher;
        });
    }

    WebFixture param(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    /** Starts an HttpSession holding a logged-in user, as LoginServlet leaves it. */
    Sesion logIn() {
        Sesion sesion = new Sesion();
        sesion.iniciar("user", "tok-1");
        FakeSession session = openSession();
        session.attributes.put(SESSION_ATTRIBUTE, sesion);
        session.attributes.put(SERVICE_ATTRIBUTE, server.service(sesion));
        return sesion;
    }

    FakeSession openSession() {
        if (current == null) {
            current = new FakeSession();
            sessions.add(current);
        }
        return current;
    }

    FakeSession currentSession() {
        return current;
    }

    /** Runs a full HTTP exchange through the servlet's public entry point. */
    void call(HttpServlet servlet, String method) throws ServletException, IOException {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getServletContext()).thenReturn(context);
        servlet.init(config);
        when(request.getMethod()).thenReturn(method);
        servlet.service(request, response);
    }

    private static HttpSession mockSession() {
        return mock(HttpSession.class);
    }

    /** HttpSession whose attributes become unreadable once invalidated, as in a container. */
    final class FakeSession {

        final HttpSession mock = WebFixture.mockSession();
        final Map<String, Object> attributes = new HashMap<>();
        boolean invalidated;

        FakeSession() {
            when(mock.getAttribute(anyString())).thenAnswer(i -> {
                checkValid();
                return attributes.get(i.<String>getArgument(0));
            });
            doAnswer(i -> {
                checkValid();
                attributes.put(i.getArgument(0), i.getArgument(1));
                return null;
            }).when(mock).setAttribute(anyString(), any());
            doAnswer(i -> {
                checkValid();
                invalidated = true;
                current = null;
                return null;
            }).when(mock).invalidate();
        }

        private void checkValid() {
            if (invalidated) {
                throw new IllegalStateException("session invalidated");
            }
        }
    }

    /** Fake SOAP server: catalogs per magnitude, a fixed conversion result and call counters. */
    static final class FakeServer implements ConversorWebService {

        final Map<String, List<String>> catalogs = new HashMap<>(Map.of(
                "Longitud", List.of("m", "ft", "km"),
                "Masa", List.of("kg", "g", "lb")));
        LoginWebService login = (usuario, clave) -> {
            throw new WebServiceException("no login configured");
        };
        double conversionResult;
        String conversionError;
        boolean catalogUnreachable;
        int conversions;
        String lastCatalogRequest;

        ConversorServicio service(Sesion sesion) {
            return new ConversorServicio((usuario, clave) -> login.login(usuario, clave), this, sesion);
        }

        @Override
        public ConverterDTO convert(String token, String type, double value, String from, String to) {
            conversions++;
            ConverterDTO dto = new ConverterDTO();
            if (conversionError != null) {
                dto.setEstado(EstadosEnum.ERROR);
                dto.setMensaje(conversionError);
            } else {
                dto.setEstado(EstadosEnum.OK);
                dto.setResultado(conversionResult);
            }
            return dto;
        }

        @Override
        public CatalogDTO catalog(String token, String type) {
            lastCatalogRequest = type;
            if (catalogUnreachable) {
                throw new WebServiceException("connection refused");
            }
            CatalogDTO dto = new CatalogDTO();
            dto.setEstado(EstadosEnum.OK);
            CatalogDTO.Resultado resultado = new CatalogDTO.Resultado();
            resultado.getUnidad().addAll(catalogs.getOrDefault(type, List.of()));
            dto.setResultado(resultado);
            return dto;
        }
    }
}
