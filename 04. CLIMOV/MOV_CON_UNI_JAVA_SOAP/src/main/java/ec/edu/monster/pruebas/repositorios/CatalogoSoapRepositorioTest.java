package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.repositorios.CatalogoSoapRepositorio;
import ec.edu.monster.modelos.repositorios.SoapErrores;
import ec.edu.monster.pruebas.apoyo.TransporteFalso;
import java.util.Arrays;
import org.junit.Test;

public class CatalogoSoapRepositorioTest {

    private static final String URL_BASE = "http://servidor:8080/WS_CON_UNI_JAVA_SOAP/";

    private static CatalogoSoapRepositorio repositorio(TransporteFalso transporte) {
        return new CatalogoSoapRepositorio(URL_BASE, transporte, () -> false);
    }

    @Test
    public void obtenerCatalogo_ok_devuelveLasUnidadesEnElOrdenDelServidor() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("catalog",
                "<estado>OK</estado><mensaje>Obtención de Catálogo Exitosa</mensaje>"
              + "<resultado><unidad>°C</unidad><unidad>°Ré</unidad><unidad>°F</unidad><unidad>K</unidad><unidad>°N</unidad></resultado>"));

        CatalogoResultado resultado = repositorio(transporte).obtenerCatalogo("tok", Magnitud.TEMPERATURA);

        assertTrue(resultado.isExito());
        assertEquals(Arrays.asList("°C", "°Ré", "°F", "K", "°N"), resultado.getUnidades());
        assertEquals(URL_BASE + "ConversorWebService", transporte.ultimaUrl());
        assertTrue(transporte.ultimoSobre().contains("<ws:catalog><token>tok</token><type>Temperatura</type></ws:catalog>"));
    }

    @Test
    public void obtenerCatalogo_tokenInvalido_marcaSesionExpirada() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("catalog",
                "<estado>ERROR</estado><mensaje>Token Inválido</mensaje><resultado/>"));

        CatalogoResultado resultado = repositorio(transporte).obtenerCatalogo("vencido", Magnitud.AREA);

        assertFalse(resultado.isExito());
        assertTrue(resultado.isSesionExpirada());
    }

    @Test
    public void obtenerCatalogo_tipoInexistente_esErrorPeroNoSesion() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("catalog",
                "<estado>ERROR</estado><mensaje>Tipo de Catálogo Inexistente</mensaje><resultado/>"));

        CatalogoResultado resultado = repositorio(transporte).obtenerCatalogo("tok", Magnitud.AREA);

        assertEquals("Tipo de Catálogo Inexistente", resultado.getMensaje());
        assertFalse(resultado.isSesionExpirada());
    }

    @Test
    public void obtenerCatalogo_okSinUnidades_avisaConLaMagnitud() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("catalog",
                "<estado>OK</estado><mensaje>ok</mensaje><resultado/>"));

        assertEquals("El servidor no devolvió unidades para Área.",
                repositorio(transporte).obtenerCatalogo("tok", Magnitud.AREA).getMensaje());
    }

    @Test
    public void obtenerCatalogo_unidadesRepetidasOVacias_seDescartan() {
        TransporteFalso transporte = TransporteFalso.responde(TransporteFalso.respuesta("catalog",
                "<estado>OK</estado><resultado><unidad>m</unidad><unidad> </unidad><unidad>m</unidad><unidad>cm</unidad></resultado>"));

        assertEquals(Arrays.asList("m", "cm"), repositorio(transporte).obtenerCatalogo("tok", Magnitud.LONGITUD).getUnidades());
    }

    @Test
    public void obtenerCatalogo_htmlEnLugarDeSoap_esRespuestaIncompleta() {
        TransporteFalso transporte = TransporteFalso.responde("<!DOCTYPE html><html><body>Error</body>");

        String mensaje = repositorio(transporte).obtenerCatalogo("tok", Magnitud.AREA).getMensaje();
        assertTrue(mensaje, mensaje.startsWith(SoapErrores.RESPUESTA_INCOMPLETA));
    }
}
