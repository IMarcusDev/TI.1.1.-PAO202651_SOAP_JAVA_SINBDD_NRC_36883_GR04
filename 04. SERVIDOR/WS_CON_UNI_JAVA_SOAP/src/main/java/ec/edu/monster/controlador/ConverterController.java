/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelos.data.AreaModel;
import ec.edu.monster.modelos.data.LongitudModel;
import ec.edu.monster.modelos.data.MasaPesoModel;
import ec.edu.monster.modelos.data.TemperaturaModel;
import ec.edu.monster.modelos.data.TiempoModel;
import ec.edu.monster.modelos.data.VolumenModel;
import ec.edu.monster.modelos.interfaces.DataConverter;

/**
 *
 * @author Mateo Sosa
 */
public class ConverterController {
  public static DataConverter getConverter(String type) throws Exception {
    switch (type) {
      case "Area":
        return new AreaModel();
      case "Longitud":
        return new LongitudModel();
      case "Masa":
        return new MasaPesoModel();
      case "Temperatura":
        return new TemperaturaModel();
      case "Tiempo":
        return new TiempoModel();
      case "Volumen":
        return new VolumenModel();
      default:
        throw new Exception("Type Converter not found!");
    }
  }
  
  public static String convert(String type, double value, String from, String to) {
    try {
      DataConverter converter = ConverterController.getConverter(type);
      
      return "" + converter.convert(value, from, to);
    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
