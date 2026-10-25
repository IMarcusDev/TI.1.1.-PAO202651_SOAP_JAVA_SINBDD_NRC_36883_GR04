package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.config.SoapConfig;
import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.modelos.soap.SoapConstructorPeticion;
import ec.edu.monster.modelos.soap.SoapLectorRespuesta;
import ec.edu.monster.modelos.soap.SoapTransporte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CatalogoSoapRepositorio implements CatalogoRepositorio {

    private final String url;
    private final SoapTransporte transporte;
    private final VerificadorRed red;

    public CatalogoSoapRepositorio(String urlBase, SoapTransporte transporte, VerificadorRed red) {
        this.url = urlBase + SoapConfig.SERVICIO_CONVERSOR;
        this.transporte = transporte;
        this.red = red;
    }

    @Override
    public CatalogoResultado obtenerCatalogo(String token, Magnitud magnitud) {
        if (red.sinRed()) {
            return CatalogoResultado.error(SoapErrores.SIN_CONEXION);
        }

        try {
            String peticion = new SoapConstructorPeticion(SoapConfig.NAMESPACE, "catalog")
                    .parametro("token", token)
                    .parametro("type", magnitud.getCodigoServidor())
                    .construir();

            SoapLectorRespuesta respuesta = new SoapLectorRespuesta(transporte.enviar(url, peticion));
            if (respuesta.estado() == null) {
                return CatalogoResultado.error(SoapErrores.RESPUESTA_INCOMPLETA);
            }

            if (!respuesta.estadoOk()) {
                String mensaje = respuesta.texto("mensaje");
                return new CatalogoResultado(false,
                        SoapErrores.mensajeServidor(mensaje, "No se pudo obtener el catálogo"),
                        Collections.emptyList(),
                        SoapErrores.esTokenInvalido(mensaje));
            }

            Set<String> unicas = new LinkedHashSet<>();
            for (String unidad : respuesta.textos("unidad")) {
                if (!unidad.isEmpty()) {
                    unicas.add(unidad);
                }
            }

            if (unicas.isEmpty()) {
                return CatalogoResultado.error("El servidor no devolvió unidades para " + magnitud.getNombre() + ".");
            }

            String mensaje = respuesta.texto("mensaje");
            return CatalogoResultado.ok(mensaje != null ? mensaje : "", new ArrayList<>(unicas));
        } catch (Exception e) {
            return CatalogoResultado.error(SoapErrores.mensaje(e));
        }
    }
}
