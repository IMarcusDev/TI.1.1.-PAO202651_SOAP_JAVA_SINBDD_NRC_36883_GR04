package ec.edu.monster.modelos.soap;

import ec.edu.monster.modelos.excepciones.RespuestaInvalidaException;
import ec.edu.monster.modelos.excepciones.SoapFallaException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SoapLectorRespuesta {

    public static final String ESTADO_OK = "OK";

    private final Document documento;

    public SoapLectorRespuesta(String xml) throws RespuestaInvalidaException, SoapFallaException {
        this.documento = parsear(xml);

        String falla = texto("faultstring");
        if (falla != null) {
            throw new SoapFallaException(falla);
        }
    }

    public String estado() {
        return texto("estado");
    }

    public boolean estadoOk() {
        return ESTADO_OK.equals(estado());
    }

    public String texto(String nombre) {
        NodeList nodos = documento.getElementsByTagNameNS("*", nombre);
        return nodos.getLength() > 0 ? nodos.item(0).getTextContent().trim() : null;
    }

    public List<String> textos(String nombre) {
        NodeList nodos = documento.getElementsByTagNameNS("*", nombre);
        List<String> valores = new ArrayList<>();
        for (int i = 0; i < nodos.getLength(); i++) {
            valores.add(nodos.item(i).getTextContent().trim());
        }
        return valores;
    }

    private static Document parsear(String xml) throws RespuestaInvalidaException {
        if (xml == null || xml.trim().isEmpty()) {
            throw new RespuestaInvalidaException("Respuesta vacía", null);
        }
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            return fabrica.newDocumentBuilder()
                          .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RespuestaInvalidaException("La respuesta no es un XML válido", e);
        }
    }
}
