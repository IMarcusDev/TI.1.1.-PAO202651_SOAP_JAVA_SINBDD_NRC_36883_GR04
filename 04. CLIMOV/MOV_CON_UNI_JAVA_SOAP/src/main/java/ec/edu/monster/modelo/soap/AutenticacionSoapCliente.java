package ec.edu.monster.modelo.soap;

import ec.edu.monster.modelo.AutenticacionServicio;
import ec.edu.monster.modelo.ServicioException;
public class AutenticacionSoapCliente implements AutenticacionServicio {
    
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    private final String url;
    private final SoapTransporte transporte;

    public AutenticacionSoapCliente(String url, SoapTransporte transporte) {
        this.url = url;
        this.transporte = transporte;
    }

    @Override
    public String iniciarSesion(String usuario, String clave) throws ServicioException {
        String peticion = new SoapConstructorPeticion(NAMESPACE, "login")
                .parametro("username", usuario)
                .parametro("password", clave)
                .construir();

        SoapLectorRespuesta respuesta = new SoapLectorRespuesta(transporte.enviar(url, peticion));

        String token = respuesta.texto("return");
        if (token == null || token.isEmpty()) {
            throw new ServicioException("Usuario o contraseña incorrectos");
        }
        return token;
    }
}
