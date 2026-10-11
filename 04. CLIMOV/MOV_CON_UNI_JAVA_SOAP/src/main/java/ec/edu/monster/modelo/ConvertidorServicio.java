/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package ec.edu.monster.modelo;

import java.util.List;

/**
 *
 * @author IMarcusDev
 */
public interface ConvertidorServicio {
    Conversion convertir(Conversion conversion) throws ServicioException;
    
    List<String> obtenerUnidades(Magnitud magnitud) throws ServicioException;
}
