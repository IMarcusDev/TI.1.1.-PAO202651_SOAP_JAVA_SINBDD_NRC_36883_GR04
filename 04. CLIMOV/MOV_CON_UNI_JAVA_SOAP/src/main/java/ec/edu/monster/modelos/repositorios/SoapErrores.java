package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.BuildConfig;
import ec.edu.monster.config.SoapConfig;
import ec.edu.monster.modelos.excepciones.HttpEstadoException;
import ec.edu.monster.modelos.excepciones.RespuestaInvalidaException;
import ec.edu.monster.modelos.excepciones.SoapFallaException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;

public final class SoapErrores {

    public static final String SIN_CONEXION = "Sin conexión a internet. Revise el Wi-Fi o los datos móviles.";
    public static final String RESPUESTA_INCOMPLETA = "El servidor envió una respuesta vacía o incompleta.";
    public static final String SESION_EXPIRADA = "Su sesión expiró o no es válida. Inicie sesión nuevamente.";
    public static final String RESULTADO_NO_VALIDO = "El servidor devolvió un resultado no válido.";

    private SoapErrores() { }

    public static String mensaje(Throwable ex) {
        String mensaje = mensajeAmigable(ex);
        if (BuildConfig.DEBUG) {
            Throwable causa = causaRaiz(ex);
            mensaje += "\n[Debug] " + causa.getClass().getSimpleName() + ": " + causa.getMessage();
        }
        return mensaje;
    }

    private static String mensajeAmigable(Throwable ex) {
        if (ex instanceof SoapFallaException) {
            return "El servidor no pudo procesar la solicitud: " + ex.getMessage();
        }
        if (ex instanceof ConnectException || ex instanceof UnknownHostException
                || ex instanceof NoRouteToHostException
                || (ex instanceof HttpEstadoException && ((HttpEstadoException) ex).getCodigo() == 404)) {
            return "No se encontró el servidor en " + SoapConfig.URL_BASE
                    + ". Verifique que esté encendido y que la IP sea correcta.";
        }
        if (ex instanceof SocketTimeoutException) {
            return "El servidor no respondió en " + SoapConfig.TIEMPO_ESPERA_SEGUNDOS + " segundos. Intente de nuevo.";
        }
        if (ex instanceof RespuestaInvalidaException) {
            return RESPUESTA_INCOMPLETA;
        }
        if (ex instanceof MalformedURLException) {
            return "La dirección del servidor está mal configurada.";
        }
        if (ex instanceof IOException) {
            return "No se pudo comunicar con el servidor. Verifique que esté encendido y en la misma red.";
        }
        return "Ocurrió un error inesperado al contactar al servidor.";
    }

    private static Throwable causaRaiz(Throwable ex) {
        Throwable causa = ex;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        return causa;
    }

    public static String mensajeServidor(String mensaje, String porDefecto) {
        return mensaje == null || mensaje.trim().isEmpty() ? porDefecto : mensaje.trim();
    }

    public static boolean esTokenInvalido(String mensaje) {
        return mensaje != null && mensaje.toLowerCase(Locale.ROOT).contains("token");
    }
}
