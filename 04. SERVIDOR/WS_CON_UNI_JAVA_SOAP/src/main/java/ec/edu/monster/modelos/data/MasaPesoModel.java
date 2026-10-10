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
public class MasaPesoModel implements DataConverter {
  private final double gram = 0.001;
  private final double kilogram = 1;
  private final double ounce = 0.0283495;
  private final double pound = 0.453592;
  private final double quintal = 100;

  @Override
  public Map<String, Double> toMap() {
    Map<String, Double> data = new HashMap<>();

    data.put("g", this.gram);
    data.put("kg", this.kilogram);
    data.put("oz", this.ounce);
    data.put("lb", this.pound);
    data.put("qq", this.quintal);

    return data;
  }
}
