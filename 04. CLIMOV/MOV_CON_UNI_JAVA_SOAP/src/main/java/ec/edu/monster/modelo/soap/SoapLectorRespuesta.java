package ec.edu.monster.modelo.soap;

import ec.edu.monster.modelo.ServicioException;
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
    
    private final Document documento;

    public SoapLectorRespuesta(String xml) throws ServicioException {
        this.documento = parsear(xml);

        String falla = texto("faultstring");
        if (falla != null) {
            throw new ServicioException("Error del servicio: " + falla);
        }
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

    private static Document parsear(String xml) throws ServicioException {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            return fabrica.newDocumentBuilder()
                          .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ServicioException("La respuesta del servicio no es un XML válido", e);
        }
    }
}
