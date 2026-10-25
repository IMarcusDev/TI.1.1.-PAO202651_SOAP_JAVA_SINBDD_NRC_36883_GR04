package ec.edu.monster.modelos.data;

import ec.edu.monster.modelos.enums.Magnitud;
import java.util.Collections;
import java.util.List;

public class GrupoUnidades {
    private final Magnitud magnitud;
    private final List<UnidadItem> unidades;

    public GrupoUnidades(Magnitud magnitud, List<UnidadItem> unidades) {
        this.magnitud = magnitud;
        this.unidades = Collections.unmodifiableList(unidades);
    }

    public Magnitud getMagnitud() {
        return magnitud;
    }

    public String getNombre() {
        return magnitud.getNombre();
    }

    public String getDescripcion() {
        return magnitud.getDescripcion();
    }

    public int getIcono() {
        return magnitud.getIcono();
    }

    public List<UnidadItem> getUnidades() {
        return unidades;
    }

    public int getCantidad() {
        return unidades.size();
    }
}
