package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.config.SoapConfig;
import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.modelos.interfaces.LoginRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.modelos.soap.SoapConstructorPeticion;
import ec.edu.monster.modelos.soap.SoapLectorRespuesta;
import ec.edu.monster.modelos.soap.SoapTransporte;

public class LoginSoapRepositorio implements LoginRepositorio {

    private final String url;
    private final SoapTransporte transporte;
    private final VerificadorRed red;

    public LoginSoapRepositorio(String urlBase, SoapTransporte transporte, VerificadorRed red) {
        this.url = urlBase + SoapConfig.SERVICIO_LOGIN;
        this.transporte = transporte;
        this.red = red;
    }

    @Override
    public LoginResultado iniciarSesion(String usuario, String clave) {
        if (red.sinRed()) {
            return LoginResultado.error(SoapErrores.SIN_CONEXION);
        }

        try {
            String peticion = new SoapConstructorPeticion(SoapConfig.NAMESPACE, "login")
                    .parametro("username", usuario)
                    .parametro("password", clave)
                    .construir();

            SoapLectorRespuesta respuesta = new SoapLectorRespuesta(transporte.enviar(url, peticion));
            if (respuesta.estado() == null) {
                return LoginResultado.error(SoapErrores.RESPUESTA_INCOMPLETA);
            }

            if (!respuesta.estadoOk()) {
                return LoginResultado.error(
                        SoapErrores.mensajeServidor(respuesta.texto("mensaje"), "Usuario o contraseña incorrectos"));
            }

            String token = respuesta.texto("token");
            if (token == null || token.isEmpty()) {
                return LoginResultado.error(SoapErrores.RESPUESTA_INCOMPLETA);
            }

            return new LoginResultado(true, textoOVacio(respuesta.texto("mensaje")), token);
        } catch (Exception e) {
            return LoginResultado.error(SoapErrores.mensaje(e));
        }
    }

    private static String textoOVacio(String texto) {
        return texto != null ? texto : "";
    }
}
