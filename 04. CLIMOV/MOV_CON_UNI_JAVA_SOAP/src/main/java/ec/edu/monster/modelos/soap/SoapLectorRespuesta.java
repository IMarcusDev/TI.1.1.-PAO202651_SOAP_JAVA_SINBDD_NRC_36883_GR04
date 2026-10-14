package ec.edu.monster.modelos.soap;

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

import ec.edu.monster.modelos.excepciones.SoapException;

public class SoapLectorRespuesta {

    private static final String ESTADO_OK = "OK";

    private final Document documento;

    public SoapLectorRespuesta(String xml) throws SoapException {
        this.documento = parsear(xml);

        String falla = texto("faultstring");
        if (falla != null) {
            throw new SoapException("Error del servicio: " + falla);
        }
    }

    public SoapLectorRespuesta verificarEstado() throws SoapException {
        if (!ESTADO_OK.equals(texto("estado"))) {
            String mensaje = texto("mensaje");
            throw new SoapException(mensaje != null && !mensaje.isEmpty()
                    ? mensaje : "El servicio no indicó el estado de la respuesta");
        }
        return this;
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

    private static Document parsear(String xml) throws SoapException {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            return fabrica.newDocumentBuilder()
                          .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SoapException("La respuesta del servicio no es un XML válido", e);
        }
    }
}
