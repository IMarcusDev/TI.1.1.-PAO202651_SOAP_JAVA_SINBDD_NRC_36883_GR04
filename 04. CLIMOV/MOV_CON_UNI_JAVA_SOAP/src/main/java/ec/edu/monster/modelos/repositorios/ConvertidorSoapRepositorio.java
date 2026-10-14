package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.excepciones.SoapException;
import ec.edu.monster.modelos.interfaces.ConvertidorRepositorio;
import ec.edu.monster.modelos.soap.SoapConstructorPeticion;
import ec.edu.monster.modelos.soap.SoapLectorRespuesta;
import ec.edu.monster.modelos.soap.SoapTransporte;
import java.util.Collections;
import java.util.List;
public class ConvertidorSoapRepositorio implements ConvertidorRepositorio {

    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    private final String url;
    private final String token;
    private final SoapTransporte transporte;

    public ConvertidorSoapRepositorio(String url, String token, SoapTransporte transporte) {
        this.url = url;
        this.token = token;
        this.transporte = transporte;
    }

    @Override
    public Conversion convertir(Conversion conversion) throws SoapException {
        String peticion = new SoapConstructorPeticion(NAMESPACE, "convert")
                .parametro("token", token)
                .parametro("type", conversion.getMagnitud().getCodigo())
                .parametro("value", conversion.getValor())
                .parametro("from", conversion.getUnidadOrigen())
                .parametro("to", conversion.getUnidadDestino())
                .construir();

        SoapLectorRespuesta respuesta = enviar(peticion);

        String resultado = respuesta.texto("resultado");
        try {
            conversion.setResultado(Double.parseDouble(resultado));
        } catch (NullPointerException | NumberFormatException e) {
            throw new SoapException("El servicio devolvió un resultado inválido: " + resultado, e);
        }
        return conversion;
    }

    @Override
    public List<String> obtenerUnidades(Magnitud magnitud) throws SoapException {
        String peticion = new SoapConstructorPeticion(NAMESPACE, "catalog")
                .parametro("token", token)
                .parametro("type", magnitud.getCodigo())
                .construir();

        SoapLectorRespuesta respuesta = enviar(peticion);
        return Collections.unmodifiableList(respuesta.textos("unidad"));
    }

    private SoapLectorRespuesta enviar(String peticion) throws SoapException {
        return new SoapLectorRespuesta(transporte.enviar(url, peticion)).verificarEstado();
    }
}
