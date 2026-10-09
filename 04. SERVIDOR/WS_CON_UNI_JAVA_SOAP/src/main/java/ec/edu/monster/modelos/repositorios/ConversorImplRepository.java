/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.modelos.interfaces.ConversorRepository;
import ec.edu.monster.modelos.interfaces.DataConverter;
import java.util.Map;

/**
 *
 * @author Mateo Sosa
 */
public class ConversorImplRepository implements ConversorRepository {
  private DataConverter converter;
  
  public ConversorImplRepository(DataConverter converter) {
    this.converter = converter;
  }

  @Override
  public double convert(double value, String from, String to) {
    if (this.converter == null) return 0;

    Map<String, Double> map = this.converter.toMap();
    
    if (!map.containsKey(from) || !map.containsKey(to)) return 0;
    
    double valueFrom = map.get(from);
    double valueTo = map.get(to);
    
    return value * valueFrom / valueTo;
  }
  
}
