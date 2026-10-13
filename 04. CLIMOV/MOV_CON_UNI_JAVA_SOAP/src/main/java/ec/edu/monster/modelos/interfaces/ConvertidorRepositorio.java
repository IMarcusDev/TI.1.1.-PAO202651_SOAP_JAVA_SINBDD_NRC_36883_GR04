package ec.edu.monster.modelos.interfaces;

import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.excepciones.SoapException;
import java.util.List;

public interface ConvertidorRepositorio {
    Conversion convertir(Conversion conversion) throws SoapException;
    
    List<String> obtenerUnidades(Magnitud magnitud) throws SoapException;
}
