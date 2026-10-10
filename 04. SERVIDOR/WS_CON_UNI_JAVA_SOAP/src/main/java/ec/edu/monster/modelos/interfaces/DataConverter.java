/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.interfaces;

import java.util.Map;

/**
 *
 * @author Mateo
 */
public interface DataConverter {
  public abstract Map<String, Double> toMap();

  default double convert(double value, String from, String to) {
    Map<String, Double> map = this.toMap();

    if (!map.containsKey(from) || !map.containsKey(to))
      return 0;

    double valueFrom = map.get(from);
    double valueTo = map.get(to);

    return value * valueFrom / valueTo;
  }
}
