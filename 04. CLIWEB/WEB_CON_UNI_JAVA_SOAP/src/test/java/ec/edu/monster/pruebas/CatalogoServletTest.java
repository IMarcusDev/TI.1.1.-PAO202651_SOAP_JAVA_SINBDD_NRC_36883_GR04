package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import ec.edu.monster.controlador.CatalogoServlet;
import ec.edu.monster.controlador.Opcion;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogoServletTest {

    private static final String VIEW = "/ec/edu/monster/vista/catalogo.jsp";

    private final WebFixture web = new WebFixture();

    @BeforeEach
    void logIn() {
        web.logIn();
    }

    @Test
    void listsUnitsOfRequestedMagnitude() throws ServletException, IOException {
        web.param("magnitud", "Masa");

        web.call(new CatalogoServlet(), "GET");

        assertEquals(VIEW, web.forwardedTo);
        verify(web.dispatcher).forward(web.request, web.response);
        assertEquals("Masa", web.server.lastCatalogRequest);
        assertEquals("Masa", web.requestAttributes.get("magnitud"));
        assertEquals(List.of("kg", "g", "lb"), values("unidades"));
        assertEquals(List.of("Kilogramo", "Gramo", "Libra"), texts("unidades"));
        assertEquals(6, values("magnitudes").size());
        assertNull(web.requestAttributes.get("error"));
    }

    @Test
    void missingMagnitudeFallsBackToFirst() throws ServletException, IOException {
        web.call(new CatalogoServlet(), "GET");

        assertEquals("Longitud", web.requestAttributes.get("magnitud"));
        assertEquals(List.of("m", "ft", "km"), values("unidades"));
    }

    @Test
    void unknownMagnitudeFallsBackToFirstWithoutAskingServer() throws ServletException, IOException {
        web.param("magnitud", "Energia");

        web.call(new CatalogoServlet(), "GET");

        assertEquals("Longitud", web.requestAttributes.get("magnitud"));
        assertEquals("Longitud", web.server.lastCatalogRequest);
    }

    @Test
    void serviceErrorShowsMessageAndNoUnits() throws ServletException, IOException {
        web.server.catalogUnreachable = true;
        web.param("magnitud", "Masa");

        web.call(new CatalogoServlet(), "GET");

        assertEquals("No se pudo conectar con el servidor", web.requestAttributes.get("error"));
        assertEquals(List.of(), values("unidades"));
        assertEquals("Masa", web.requestAttributes.get("magnitud"));
        assertEquals(VIEW, web.forwardedTo);
    }

    @SuppressWarnings("unchecked")
    private List<String> values(String attribute) {
        return ((List<Opcion>) web.requestAttributes.get(attribute)).stream().map(Opcion::getValor).toList();
    }

    @SuppressWarnings("unchecked")
    private List<String> texts(String attribute) {
        return ((List<Opcion>) web.requestAttributes.get(attribute)).stream().map(Opcion::getTexto).toList();
    }
}
