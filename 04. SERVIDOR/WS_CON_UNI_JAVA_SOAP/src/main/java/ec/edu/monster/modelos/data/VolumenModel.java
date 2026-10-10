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
public class VolumenModel implements DataConverter {
  private final double milliliter = 0.001;
  private final double liter = 1;
  private final double cubicMeter = 1000;
  private final double cubicFeet = 28.3168;
  private final double galon = 3.78541;

  @Override
  public Map<String, Double> toMap() {
    Map<String, Double> data = new HashMap<>();

    data.put("mL", this.milliliter);
    data.put("L", this.liter);
    data.put("m3", this.cubicMeter);
    data.put("ft3", this.cubicFeet);
    data.put("gal", this.galon);

    return data;
  }
}
