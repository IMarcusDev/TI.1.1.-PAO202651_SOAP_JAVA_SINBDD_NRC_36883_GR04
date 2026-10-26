package ec.edu.monster.modelos.data;

import java.util.Collections;
import java.util.List;

public class CatalogoCompletoResultado {
    private final boolean exito;
    private final String mensaje;
    private final List<GrupoUnidades> grupos;
    private final boolean sesionExpirada;

    public CatalogoCompletoResultado(boolean exito, String mensaje, List<GrupoUnidades> grupos, boolean sesionExpirada) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.grupos = Collections.unmodifiableList(grupos);
        this.sesionExpirada = sesionExpirada;
    }

    public static CatalogoCompletoResultado ok(List<GrupoUnidades> grupos) {
        return new CatalogoCompletoResultado(true, "", grupos, false);
    }

    public static CatalogoCompletoResultado error(String mensaje) {
        return new CatalogoCompletoResultado(false, mensaje, Collections.emptyList(), false);
    }

    public static CatalogoCompletoResultado expirada(String mensaje) {
        return new CatalogoCompletoResultado(false, mensaje, Collections.emptyList(), true);
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<GrupoUnidades> getGrupos() {
        return grupos;
    }

    public boolean isSesionExpirada() {
        return sesionExpirada;
    }
}
