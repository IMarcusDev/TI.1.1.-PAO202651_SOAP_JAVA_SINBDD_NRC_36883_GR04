package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpServer;
import ec.edu.monster.controlador.EstadoServlet;
import ec.edu.monster.controlador.FabricaServicios;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** The page header polls GET /estado to show whether the SOAP server is still up. */
class EstadoServletTest {

    private HttpServer servidor;

    @AfterEach
    void apagarServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void reportsServerGoingDownWithoutCaching() throws IOException, ServletException {
        URI wsdl = arrancar();

        assertEquals("{\"disponible\":true}", consultar(wsdl));

        servidor.stop(0);

        assertEquals("{\"disponible\":false}", consultar(wsdl));
    }

    private String consultar(URI wsdl) throws IOException, ServletException {
        WebFixture web = new WebFixture();
        when(web.context.getAttribute(WebFixture.FACTORY_ATTRIBUTE)).thenReturn(new FabricaServicios(wsdl));
        StringWriter cuerpo = new StringWriter();
        when(web.response.getWriter()).thenReturn(new PrintWriter(cuerpo));

        web.call(new EstadoServlet(), "GET");

        verify(web.response).setContentType("application/json");
        verify(web.response).setCharacterEncoding("UTF-8");
        verify(web.response).setHeader("Cache-Control", "no-store");
        return cuerpo.toString();
    }

    private URI arrancar() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            intercambio.sendResponseHeaders(200, -1);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.flush();
            }
        });
        servidor.start();
        return URI.create("http://localhost:" + servidor.getAddress().getPort() + "/ConversorWebService?wsdl");
    }
}
