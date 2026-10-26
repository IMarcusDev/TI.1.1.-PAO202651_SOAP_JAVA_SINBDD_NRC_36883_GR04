package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

import ec.edu.monster.controlador.ConversionServlet;
import ec.edu.monster.controlador.Opcion;
import ec.edu.monster.modelo.FormatoResultado;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversionServletTest {

    private static final String VIEW = "/ec/edu/monster/vista/conversion.jsp";

    private final WebFixture web = new WebFixture();

    @BeforeEach
    void logIn() {
        web.logIn();
    }

    @Test
    void getShowsMagnitudesAndUnitsOfSelectedMagnitude() throws ServletException, IOException {
        web.param("magnitud", "Masa");

        web.call(new ConversionServlet(), "GET");

        assertEquals(VIEW, web.forwardedTo);
        verify(web.dispatcher).forward(web.request, web.response);
        assertEquals(List.of("Longitud", "Masa", "Volumen", "Tiempo", "Temperatura", "Area"),
                values("magnitudes"));
        assertEquals("Masa", web.requestAttributes.get("magnitud"));
        assertEquals(List.of("kg", "g", "lb"), values("unidades"));
        assertEquals("", web.requestAttributes.get("valor"));
        assertNull(web.requestAttributes.get("error"));
    }

    @Test
    void getWithUnknownMagnitudeFallsBackToFirst() throws ServletException, IOException {
        web.param("magnitud", "Energia");

        web.call(new ConversionServlet(), "GET");

        assertEquals("Longitud", web.requestAttributes.get("magnitud"));
        assertEquals(List.of("m", "ft", "km"), values("unidades"));
    }

    @Test
    void postShowsFormattedResultAndKeepsSelections() throws ServletException, IOException {
        web.server.conversionResult = 328.084;
        submit("Longitud", "m", "ft", "100");

        web.call(new ConversionServlet(), "POST");

        assertEquals(FormatoResultado.formatear(100, "m", 328.084, "ft"), web.requestAttributes.get("resultado"));
        assertNull(web.requestAttributes.get("error"));
        assertEquals("m", web.requestAttributes.get("desde"));
        assertEquals("ft", web.requestAttributes.get("hacia"));
        assertEquals("100", web.requestAttributes.get("valor"));
        assertEquals(1, web.server.conversions);
        assertEquals(VIEW, web.forwardedTo);
    }

    @Test
    void postAcceptsDecimalComma() throws ServletException, IOException {
        web.server.conversionResult = 2.5;
        submit("Longitud", "km", "m", "0,0025");

        web.call(new ConversionServlet(), "POST");

        assertEquals(FormatoResultado.formatear(0.0025, "km", 2.5, "m"), web.requestAttributes.get("resultado"));
    }

    @Test
    void postWithInvalidValueShowsValidationMessageWithoutCallingServer() throws ServletException, IOException {
        submit("Longitud", "m", "ft", "abc");

        web.call(new ConversionServlet(), "POST");

        assertEquals("Ingrese un valor numérico válido", web.requestAttributes.get("error"));
        assertEquals("abc", web.requestAttributes.get("valor"));
        assertNull(web.requestAttributes.get("resultado"));
        assertEquals(0, web.server.conversions);
    }

    @Test
    void postWithUnitsOfAnotherMagnitudeAsksToSelectUnits() throws ServletException, IOException {
        submit("Masa", "m", "ft", "100");

        web.call(new ConversionServlet(), "POST");

        assertEquals("Seleccione la unidad de origen y de destino", web.requestAttributes.get("error"));
        assertNull(web.requestAttributes.get("desde"));
        assertNull(web.requestAttributes.get("hacia"));
        assertEquals(List.of("kg", "g", "lb"), values("unidades"));
        assertEquals(0, web.server.conversions);
    }

    @Test
    void postWithoutUnitsAsksToSelectUnits() throws ServletException, IOException {
        submit("Longitud", null, null, "100");

        web.call(new ConversionServlet(), "POST");

        assertEquals("Seleccione la unidad de origen y de destino", web.requestAttributes.get("error"));
        assertEquals(0, web.server.conversions);
    }

    @Test
    void changingMagnitudeReloadsUnitsWithoutConverting() throws ServletException, IOException {
        submit("Masa", "m", "ft", "100");
        web.param("accion", "cambiar");

        web.call(new ConversionServlet(), "POST");

        assertEquals("Masa", web.requestAttributes.get("magnitud"));
        assertEquals(List.of("kg", "g", "lb"), values("unidades"));
        assertNull(web.requestAttributes.get("desde"));
        assertNull(web.requestAttributes.get("hacia"));
        assertEquals("100", web.requestAttributes.get("valor"));
        assertNull(web.requestAttributes.get("error"));
        assertNull(web.requestAttributes.get("resultado"));
        assertEquals(0, web.server.conversions);
    }

    @Test
    void catalogFailureBlanksUnitsAndShowsError() throws ServletException, IOException {
        web.server.catalogUnreachable = true;
        submit("Longitud", "m", "ft", "100");

        web.call(new ConversionServlet(), "POST");

        assertEquals("No se pudo conectar con el servidor", web.requestAttributes.get("error"));
        assertEquals(List.of(), values("unidades"));
        assertNull(web.requestAttributes.get("desde"));
        assertNull(web.requestAttributes.get("hacia"));
        assertEquals(0, web.server.conversions);
    }

    @Test
    void serverConversionErrorIsShown() throws ServletException, IOException {
        web.server.conversionError = "Unidad no soportada";
        submit("Longitud", "m", "ft", "100");

        web.call(new ConversionServlet(), "POST");

        assertEquals("Unidad no soportada", web.requestAttributes.get("error"));
        assertNull(web.requestAttributes.get("resultado"));
    }

    private void submit(String magnitud, String desde, String hacia, String valor) {
        web.param("magnitud", magnitud).param("desde", desde).param("hacia", hacia).param("valor", valor);
    }

    @SuppressWarnings("unchecked")
    private List<String> values(String attribute) {
        return ((List<Opcion>) web.requestAttributes.get(attribute)).stream().map(Opcion::getValor).toList();
    }
}
