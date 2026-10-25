package ec.edu.monster.modelos.data;

import java.util.Collections;
import java.util.List;

public class CatalogoResultado {
    private final boolean exito;
    private final String mensaje;
    private final List<String> unidades;
    private final boolean sesionExpirada;

    public CatalogoResultado(boolean exito, String mensaje, List<String> unidades, boolean sesionExpirada) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.unidades = Collections.unmodifiableList(unidades);
        this.sesionExpirada = sesionExpirada;
    }

    public static CatalogoResultado ok(String mensaje, List<String> unidades) {
        return new CatalogoResultado(true, mensaje, unidades, false);
    }

    public static CatalogoResultado error(String mensaje) {
        return new CatalogoResultado(false, mensaje, Collections.emptyList(), false);
    }

    public static CatalogoResultado expirada(String mensaje) {
        return new CatalogoResultado(false, mensaje, Collections.emptyList(), true);
    }

    public CatalogoResultado conMensaje(String nuevoMensaje) {
        return new CatalogoResultado(exito, nuevoMensaje, unidades, sesionExpirada);
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<String> getUnidades() {
        return unidades;
    }

    public boolean isSesionExpirada() {
        return sesionExpirada;
    }
}
