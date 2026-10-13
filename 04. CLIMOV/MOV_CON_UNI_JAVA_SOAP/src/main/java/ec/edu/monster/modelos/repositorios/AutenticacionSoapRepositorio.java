package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.modelos.excepciones.SoapException;
import ec.edu.monster.modelos.interfaces.AutenticacionRepositorio;
import ec.edu.monster.modelos.soap.SoapConstructorPeticion;
import ec.edu.monster.modelos.soap.SoapLectorRespuesta;
import ec.edu.monster.modelos.soap.SoapTransporte;

public class AutenticacionSoapRepositorio implements AutenticacionRepositorio {
    
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    private final String url;
    private final SoapTransporte transporte;

    public AutenticacionSoapRepositorio(String url, SoapTransporte transporte) {
        this.url = url;
        this.transporte = transporte;
    }

    @Override
    public String iniciarSesion(String usuario, String clave) throws SoapException {
        String peticion = new SoapConstructorPeticion(NAMESPACE, "login")
                .parametro("username", usuario)
                .parametro("password", clave)
                .construir();

        SoapLectorRespuesta respuesta = new SoapLectorRespuesta(transporte.enviar(url, peticion))
                .verificarEstado();

        String token = respuesta.texto("token");
        if (token == null || token.isEmpty()) {
            throw new SoapException("El servicio no devolvió un token de sesión");
        }
        return token;
    }
}
