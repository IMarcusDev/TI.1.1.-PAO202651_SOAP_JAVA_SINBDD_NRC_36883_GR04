package ec.edu.monster.pruebas.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.excepciones.SoapException;
import ec.edu.monster.modelos.soap.SoapLectorRespuesta;
import java.util.Arrays;
import org.junit.Test;

public class SoapLectorRespuestaTest {

    private static String sobre(String cuerpo) {
        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">"
             + "<S:Body>" + cuerpo + "</S:Body></S:Envelope>";
    }

    @Test
    public void texto_respuestaDeConversion_leeLosCampos() throws SoapException {
        String xml = sobre(
              "<ns2:convertResponse xmlns:ns2=\"http://ws.monster.edu.ec/\"><return>"
            + "<estado>OK</estado><mensaje>Conversión Exitosa</mensaje><resultado>0.25</resultado>"
            + "</return></ns2:convertResponse>");

        SoapLectorRespuesta lector = new SoapLectorRespuesta(xml);

        assertEquals("OK", lector.texto("estado"));
        assertEquals("Conversión Exitosa", lector.texto("mensaje"));
        assertEquals("0.25", lector.texto("resultado"));
    }

    @Test
    public void textos_respuestaDeCatalogo_devuelveTodasLasUnidadesEnOrden() throws SoapException {
        String xml = sobre(
              "<ns2:catalogResponse xmlns:ns2=\"http://ws.monster.edu.ec/\"><return>"
            + "<estado>OK</estado><resultado><unidad>m3</unidad><unidad>L</unidad><unidad>gal</unidad></resultado>"
            + "</return></ns2:catalogResponse>");

        SoapLectorRespuesta lector = new SoapLectorRespuesta(xml);

        assertEquals(Arrays.asList("m3", "L", "gal"), lector.textos("unidad"));
    }

    @Test
    public void texto_elementoInexistente_devuelveNull() throws SoapException {
        SoapLectorRespuesta lector = new SoapLectorRespuesta(sobre("<vacio/>"));

        assertNull(lector.texto("resultado"));
    }

    @Test
    public void constructor_respuestaConFault_lanzaExcepcionConElMensaje() {
        String xml = sobre("<S:Fault><faultcode>S:Server</faultcode>"
                         + "<faultstring>Error interno del servidor</faultstring></S:Fault>");

        SoapException error = assertThrows(SoapException.class,
                () -> new SoapLectorRespuesta(xml));

        assertTrue(error.getMessage().contains("Error interno del servidor"));
    }

    @Test
    public void constructor_textoQueNoEsXml_lanzaExcepcionConservandoLaCausa() {
        SoapException error = assertThrows(SoapException.class,
                () -> new SoapLectorRespuesta("Not Found"));

        assertNotNull(error.getCause());
    }
}