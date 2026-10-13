package ec.edu.monster.modelo;

import java.util.List;

public interface ConvertidorServicio {
    Conversion convertir(Conversion conversion) throws ServicioException;
    
    List<String> obtenerUnidades(Magnitud magnitud) throws ServicioException;
}
