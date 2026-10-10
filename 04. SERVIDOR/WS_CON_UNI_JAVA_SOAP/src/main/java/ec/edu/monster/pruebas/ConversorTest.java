/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.pruebas;

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
public class ConversorTest {
  public static void main(String[] args) {
    DataConverter longitudConverter = new LongitudModel();
    DataConverter areaConverter = new AreaModel();
    DataConverter masapesoConverter = new MasaPesoModel();
    DataConverter tiempoConverter = new TiempoModel();
    DataConverter volumenConverter = new VolumenModel();
    DataConverter temperaturaConverter = new TemperaturaModel();
   
    System.out.println(longitudConverter.convert(1000, "m", "mi"));
    System.out.println(areaConverter.convert(1000, "m2", "cm2"));
    System.out.println(masapesoConverter.convert(1000, "g", "kg"));
    System.out.println(tiempoConverter.convert(1000, "s", "h"));
    System.out.println(volumenConverter.convert(1000, "mL", "L"));
    System.out.println(temperaturaConverter.convert(1000, "K", "°C"));
  }
}
