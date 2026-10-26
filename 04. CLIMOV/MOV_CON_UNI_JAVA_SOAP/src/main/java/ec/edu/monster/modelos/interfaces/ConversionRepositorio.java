package ec.edu.monster.modelos.interfaces;

import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;

public interface ConversionRepositorio {
    ConversionResultado convertir(String token, Conversion conversion);
}
