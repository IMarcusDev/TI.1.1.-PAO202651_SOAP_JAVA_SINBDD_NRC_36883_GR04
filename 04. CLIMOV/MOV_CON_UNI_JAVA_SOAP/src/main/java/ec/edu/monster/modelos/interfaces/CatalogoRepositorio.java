package ec.edu.monster.modelos.interfaces;

import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.enums.Magnitud;

public interface CatalogoRepositorio {
    CatalogoResultado obtenerCatalogo(String token, Magnitud magnitud);
}
