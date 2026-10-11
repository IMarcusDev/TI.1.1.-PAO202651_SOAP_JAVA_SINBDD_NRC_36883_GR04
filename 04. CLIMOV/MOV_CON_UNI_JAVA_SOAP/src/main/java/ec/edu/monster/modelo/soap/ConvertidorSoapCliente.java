/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo.soap;

import ec.edu.monster.modelo.Conversion;
import ec.edu.monster.modelo.ConvertidorServicio;
import ec.edu.monster.modelo.Magnitud;
import ec.edu.monster.modelo.ServicioException;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author IMarcusDev
 */
public class ConvertidorSoapCliente implements ConvertidorServicio {

    private static final String NAMESPACE = "http://ws.monster.edu.ec/";
    private static final String ESTADO_OK = "OK";

    private final String url;
    private final String token;
    private final SoapTransporte transporte;

    public ConvertidorSoapCliente(String url, String token, SoapTransporte transporte) {
        this.url = url;
        this.token = token;
        this.transporte = transporte;
    }

    @Override
    public Conversion convertir(Conversion conversion) throws ServicioException {
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
            throw new ServicioException("El servicio devolvió un resultado inválido: " + resultado, e);
        }
        return conversion;
    }

    @Override
    public List<String> obtenerUnidades(Magnitud magnitud) throws ServicioException {
        String peticion = new SoapConstructorPeticion(NAMESPACE, "catalog")
                .parametro("token", token)
                .parametro("type", magnitud.getCodigo())
                .construir();

        SoapLectorRespuesta respuesta = enviar(peticion);
        return Collections.unmodifiableList(respuesta.textos("unidad"));
    }

    private SoapLectorRespuesta enviar(String peticion) throws ServicioException {
        SoapLectorRespuesta respuesta = new SoapLectorRespuesta(transporte.enviar(url, peticion));

        if (!ESTADO_OK.equals(respuesta.texto("estado"))) {
            String mensaje = respuesta.texto("mensaje");
            throw new ServicioException(mensaje != null ? mensaje : "El servicio no indicó el estado de la respuesta");
        }
        return respuesta;
    }
}
