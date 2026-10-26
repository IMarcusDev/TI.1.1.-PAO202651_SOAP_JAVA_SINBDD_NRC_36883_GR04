package ec.edu.monster.pruebas.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.excepciones.RespuestaInvalidaException;
import ec.edu.monster.modelos.excepciones.SoapFallaException;
import ec.edu.monster.modelos.soap.SoapLectorRespuesta;
import java.util.Arrays;
import org.junit.Test;

public class SoapLectorRespuestaTest {

    private static String sobre(String cuerpo) {
        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"
             + "<S:Body>" + cuerpo + "</S:Body></S:Envelope>";
    }

    @Test
    public void texto_respuestaDeConversion_leeLosCampos() throws Exception {
        String xml = sobre(
              "<ns2:convertResponse xmlns:ns2=\"http://ws.monster.edu.ec/\"><return>"
            + "<estado>OK</estado><mensaje>Conversión Exitosa</mensaje><resultado>0.25</resultado>"
            + "</return></ns2:convertResponse>");

        SoapLectorRespuesta lector = new SoapLectorRespuesta(xml);

        assertEquals("OK", lector.estado());
        assertTrue(lector.estadoOk());
        assertEquals("Conversión Exitosa", lector.texto("mensaje"));
        assertEquals("0.25", lector.texto("resultado"));
    }

    @Test
    public void estadoOk_conEstadoError_esFalso() throws Exception {
        SoapLectorRespuesta lector = new SoapLectorRespuesta(sobre("<return><estado>ERROR</estado></return>"));

        assertFalse(lector.estadoOk());
        assertEquals("ERROR", lector.estado());
    }

    @Test
    public void estado_respuestaSinEstado_esNull() throws Exception {
        assertNull(new SoapLectorRespuesta(sobre("<return/>")).estado());
    }

    @Test
    public void textos_respuestaDeCatalogo_devuelveTodasLasUnidadesEnOrden() throws Exception {
        String xml = sobre(
              "<ns2:catalogResponse xmlns:ns2=\"http://ws.monster.edu.ec/\"><return>"
            + "<estado>OK</estado><resultado><unidad>m3</unidad><unidad>L</unidad><unidad>gal</unidad></resultado>"
            + "</return></ns2:catalogResponse>");

        SoapLectorRespuesta lector = new SoapLectorRespuesta(xml);

        assertEquals(Arrays.asList("m3", "L", "gal"), lector.textos("unidad"));
    }

    @Test
    public void texto_elementoInexistente_devuelveNull() throws Exception {
        SoapLectorRespuesta lector = new SoapLectorRespuesta(sobre("<vacio/>"));

        assertNull(lector.texto("resultado"));
    }

    @Test
    public void constructor_respuestaConFault_lanzaFallaConElFaultstring() {
        String xml = sobre("<S:Fault><faultcode>S:Server</faultcode>"
                         + "<faultstring>java.lang.NullPointerException</faultstring></S:Fault>");

        SoapFallaException error = assertThrows(SoapFallaException.class,
                () -> new SoapLectorRespuesta(xml));

        assertEquals("java.lang.NullPointerException", error.getMessage());
    }

    @Test
    public void constructor_textoQueNoEsXml_lanzaRespuestaInvalidaConservandoLaCausa() {
        RespuestaInvalidaException error = assertThrows(RespuestaInvalidaException.class,
                () -> new SoapLectorRespuesta("<html>Not Found"));

        assertNotNull(error.getCause());
    }

    @Test
    public void constructor_cuerpoVacio_lanzaRespuestaInvalida() {
        assertThrows(RespuestaInvalidaException.class, () -> new SoapLectorRespuesta("  "));
        assertThrows(RespuestaInvalidaException.class, () -> new SoapLectorRespuesta(null));
    }
}
