/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.data;

import java.util.HashMap;
import java.util.Map;

import ec.edu.monster.modelos.interfaces.DataConverter;

/**
 *
 * @author Mateo Sosa
 */
public class TemperaturaModel implements DataConverter {
  @Override
  public Map<String, Double> toMap() {
    Map<String, Double> data = new HashMap<>();

    data.put("°C", 1.0);
    data.put("°F", 1.0);
    data.put("K", 1.0);
    data.put("°N", 1.0);
    data.put("°Ré", 1.0);

    return data;
  }

  @Override
  public double convert(double value, String from, String to) {
    if (!toMap().containsKey(from) || !toMap().containsKey(to))
      return 0;

    // Celsius First
    double celsius;

    switch (from) {
      case "°C":
        celsius = value;
        break;
      case "°F":
        celsius = (value - 32) * 5.0 / 9.0;
        break;
      case "K":
        celsius = value - 273.15;
        break;
      case "°N":
        celsius = value * 100.0 / 33.0;
        break;
      case "°Ré":
        celsius = value * 5.0 / 4.0;
        break;
      default:
        return 0;
    }

    switch (to) {
      case "°C":
        return celsius;
      case "°F":
        return celsius * 9.0 / 5.0 + 32;
      case "K":
        return celsius + 273.15;
      case "°N":
        return celsius * 33.0 / 100.0;
      case "°Ré":
        return celsius * 4.0 / 5.0;
      default:
        return 0;
    }
  }
}
