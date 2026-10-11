package ec.edu.monster.pruebas.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelo.Conversion;
import ec.edu.monster.modelo.Magnitud;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.soap.ConvertidorSoapCliente;
import ec.edu.monster.modelo.soap.SoapTransporte;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ConvertidorSoapClienteTest {

    private static final String URL = "http://servidor/ConversorWebService";
    private static final String TOKEN = "token-de-prueba";

    /** Transporte que no usa la red: guarda la petición y devuelve una respuesta fija. */
    private static class TransporteFalso extends SoapTransporte {
        private final String respuesta;
        String urlRecibida;
        String peticionRecibida;

        TransporteFalso(String respuesta) {
            this.respuesta = respuesta;
        }

        @Override
        public String enviar(String url, String sobre) {
            this.urlRecibida = url;
            this.peticionRecibida = sobre;
            return respuesta;
        }
    }

    private static String respuesta(String operacion, String contenido) {
        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body>"
             + "<ns2:" + operacion + "Response xmlns:ns2=\"http://ws.monster.edu.ec/\">"
             + "<return>" + contenido + "</return>"
             + "</ns2:" + operacion + "Response></S:Body></S:Envelope>";
    }

    @Test
    public void convertir_respuestaOk_asignaElResultadoYEnviaLosParametros() throws ServicioException {
        TransporteFalso transporte = new TransporteFalso(respuesta("convert",
                "<estado>OK</estado><mensaje>Conversión Exitosa</mensaje><resultado>0.25</resultado>"));
        ConvertidorSoapCliente cliente = new ConvertidorSoapCliente(URL, TOKEN, transporte);
        Conversion conversion = new Conversion(Magnitud.AREA, 2500, "cm2", "m2");

        cliente.convertir(conversion);

        assertEquals(0.25, conversion.getResultado(), 1e-9);
        assertEquals(URL, transporte.urlRecibida);
        assertTrue(transporte.peticionRecibida.contains(
                "<token>" + TOKEN + "</token><type>Area</type><value>2500.0</value>"
              + "<from>cm2</from><to>m2</to>"));
    }

    @Test
    public void convertir_estadoError_lanzaExcepcionConElMensajeDelServidor() {
        TransporteFalso transporte = new TransporteFalso(respuesta("convert",
                "<estado>ERROR</estado><mensaje>Invalid Token</mensaje><resultado>0.0</resultado>"));
        ConvertidorSoapCliente cliente = new ConvertidorSoapCliente(URL, TOKEN, transporte);
        Conversion conversion = new Conversion(Magnitud.AREA, 2500, "cm2", "m2");

        ServicioException error = assertThrows(ServicioException.class,
                () -> cliente.convertir(conversion));

        assertEquals("Invalid Token", error.getMessage());
        assertFalse(conversion.tieneResultado());
    }

    @Test
    public void convertir_resultadoNoNumerico_lanzaExcepcion() {
        TransporteFalso transporte = new TransporteFalso(respuesta("convert",
                "<estado>OK</estado><resultado>abc</resultado>"));
        ConvertidorSoapCliente cliente = new ConvertidorSoapCliente(URL, TOKEN, transporte);

        assertThrows(ServicioException.class,
                () -> cliente.convertir(new Conversion(Magnitud.AREA, 1, "cm2", "m2")));
    }

    @Test
    public void obtenerUnidades_respuestaOk_devuelveLasUnidades() throws ServicioException {
        TransporteFalso transporte = new TransporteFalso(respuesta("catalog",
                "<estado>OK</estado><resultado><unidad>m3</unidad><unidad>gal</unidad></resultado>"));
        ConvertidorSoapCliente cliente = new ConvertidorSoapCliente(URL, TOKEN, transporte);

        List<String> unidades = cliente.obtenerUnidades(Magnitud.VOLUMEN);

        assertEquals(Arrays.asList("m3", "gal"), unidades);
        assertTrue(transporte.peticionRecibida.contains("<type>Volumen</type>"));
    }

    @Test
    public void obtenerUnidades_listaDevuelta_noSePuedeModificar() throws ServicioException {
        TransporteFalso transporte = new TransporteFalso(respuesta("catalog",
                "<estado>OK</estado><resultado><unidad>m3</unidad></resultado>"));
        ConvertidorSoapCliente cliente = new ConvertidorSoapCliente(URL, TOKEN, transporte);

        List<String> unidades = cliente.obtenerUnidades(Magnitud.VOLUMEN);

        assertThrows(UnsupportedOperationException.class, () -> unidades.add("gal"));
    }
}