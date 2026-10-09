/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.pruebas;

import ec.edu.monster.modelos.data.LongitudModel;
import ec.edu.monster.modelos.interfaces.ConversorRepository;
import ec.edu.monster.modelos.interfaces.DataConverter;
import ec.edu.monster.modelos.repositorios.ConversorImplRepository;

/**
 *
 * @author Mateo Sosa
 */
public class ConversorTest {
  public static void main(String[] args) {
    DataConverter model = new LongitudModel();
    ConversorRepository longitudConversor = new ConversorImplRepository(model);
    
    System.out.println(longitudConversor.convert(1000, "m", "mile"));
  }
}
