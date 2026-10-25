package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.config.SoapConfig;
import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.interfaces.ConversionRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.modelos.soap.SoapConstructorPeticion;
import ec.edu.monster.modelos.soap.SoapLectorRespuesta;
import ec.edu.monster.modelos.soap.SoapTransporte;

public class ConversionSoapRepositorio implements ConversionRepositorio {

    private final String url;
    private final SoapTransporte transporte;
    private final VerificadorRed red;

    public ConversionSoapRepositorio(String urlBase, SoapTransporte transporte, VerificadorRed red) {
        this.url = urlBase + SoapConfig.SERVICIO_CONVERSOR;
        this.transporte = transporte;
        this.red = red;
    }

    @Override
    public ConversionResultado convertir(String token, Conversion conversion) {
        if (red.sinRed()) {
            return ConversionResultado.error(SoapErrores.SIN_CONEXION);
        }

        try {
            String peticion = new SoapConstructorPeticion(SoapConfig.NAMESPACE, "convert")
                    .parametro("token", token)
                    .parametro("type", conversion.getMagnitud().getCodigoServidor())
                    .parametro("value", conversion.getValor())
                    .parametro("from", conversion.getUnidadOrigen())
                    .parametro("to", conversion.getUnidadDestino())
                    .construir();

            SoapLectorRespuesta respuesta = new SoapLectorRespuesta(transporte.enviar(url, peticion));
            if (respuesta.estado() == null) {
                return ConversionResultado.error(SoapErrores.RESPUESTA_INCOMPLETA);
            }

            if (!respuesta.estadoOk()) {
                String mensaje = respuesta.texto("mensaje");
                return new ConversionResultado(false,
                        SoapErrores.mensajeServidor(mensaje, "No se pudo hacer la conversión"),
                        0,
                        SoapErrores.esTokenInvalido(mensaje),
                        null);
            }

            String texto = respuesta.texto("resultado");
            if (texto == null || texto.isEmpty()) {
                return ConversionResultado.error(SoapErrores.RESPUESTA_INCOMPLETA);
            }

            double resultado;
            try {
                resultado = Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                return ConversionResultado.error(SoapErrores.RESULTADO_NO_VALIDO);
            }
            if (!Double.isFinite(resultado)) {
                return ConversionResultado.error(SoapErrores.RESULTADO_NO_VALIDO);
            }

            String mensaje = respuesta.texto("mensaje");
            return ConversionResultado.ok(mensaje != null ? mensaje : "", resultado);
        } catch (Exception e) {
            return ConversionResultado.error(SoapErrores.mensaje(e));
        }
    }
}
