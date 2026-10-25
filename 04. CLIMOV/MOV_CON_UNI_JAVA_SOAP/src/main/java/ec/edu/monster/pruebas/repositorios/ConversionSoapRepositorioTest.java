package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.repositorios.ConversionSoapRepositorio;
import ec.edu.monster.modelos.repositorios.SoapErrores;
import ec.edu.monster.pruebas.apoyo.TransporteFalso;
import java.net.SocketTimeoutException;
import org.junit.Test;

public class ConversionSoapRepositorioTest {

    private static final String URL_BASE = "http://servidor:8080/WS_CON_UNI_JAVA_SOAP/";

    private static ConversionSoapRepositorio repositorio(TransporteFalso transporte) {
        return new ConversionSoapRepositorio(URL_BASE, transporte, () -> false);
    }

    private static Conversion area() {
        return new Conversion(Magnitud.AREA, 2500, "cm2", "m2");
    }

    @Test
    public void convertir_ok_devuelveElResultadoYEnviaLosParametrosEnOrden() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("convert",
                "<estado>OK</estado><mensaje>Conversión Exitosa</mensaje><resultado>0.25</resultado>"));

        ConversionResultado resultado = repositorio(transporte).convertir("tok", area());

        assertTrue(resultado.isExito());
        assertEquals(0.25, resultado.getValor(), 1e-12);
        assertTrue(transporte.ultimoSobre().contains(
                "<token>tok</token><type>Area</type><value>2500.0</value><from>cm2</from><to>m2</to>"));
    }

    @Test
    public void convertir_unidadesUnicode_viajanSinCambios() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("convert",
                "<estado>OK</estado><resultado>80.0</resultado>"));

        repositorio(transporte).convertir("tok", new Conversion(Magnitud.TEMPERATURA, 100, "°C", "°Ré"));

        assertTrue(transporte.ultimoSobre().contains("<from>°C</from><to>°Ré</to>"));
    }

    @Test
    public void convertir_resultadoEnNotacionCientifica_seInterpreta() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("convert",
                "<estado>OK</estado><resultado>6.047999999999994E17</resultado>"));

        assertEquals(6.047999999999994E17, repositorio(transporte).convertir("tok", area()).getValor(), 1);
    }

    @Test
    public void convertir_invalidToken_marcaSesionExpirada() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("convert",
                "<estado>ERROR</estado><mensaje>Invalid Token</mensaje><resultado>0.0</resultado>"));

        ConversionResultado resultado = repositorio(transporte).convertir("vencido", area());

        assertFalse(resultado.isExito());
        assertTrue(resultado.isSesionExpirada());
    }

    @Test
    public void convertir_resultadoNoNumericoOInfinito_noEsValido() {
        for (String texto : new String[] {"abc", "Infinity", "NaN"}) {
            TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("convert",
                    "<estado>OK</estado><resultado>" + texto + "</resultado>"));
            assertEquals(texto, SoapErrores.RESULTADO_NO_VALIDO, repositorio(transporte).convertir("tok", area()).getMensaje());
        }
    }

    @Test
    public void convertir_okSinResultado_esRespuestaIncompleta() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("convert",
                "<estado>OK</estado><mensaje>ok</mensaje>"));

        assertEquals(SoapErrores.RESPUESTA_INCOMPLETA, repositorio(transporte).convertir("tok", area()).getMensaje());
    }

    @Test
    public void convertir_soapFault_muestraElDetalleDelServidor() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.falla("java.lang.NullPointerException"));

        ConversionResultado resultado = repositorio(transporte).convertir("tok", area());

        assertTrue(resultado.getMensaje(), resultado.getMensaje()
                .startsWith("El servidor no pudo procesar la solicitud: java.lang.NullPointerException"));
        assertFalse(resultado.isSesionExpirada());
    }

    @Test
    public void convertir_timeout_avisaLos15Segundos() {
        TransporteFalso transporte = TransporteFalso.lanza(new SocketTimeoutException("Read timed out"));

        assertTrue(repositorio(transporte).convertir("tok", area()).getMensaje()
                .startsWith("El servidor no respondió en 15 segundos. Intente de nuevo."));
    }
}
