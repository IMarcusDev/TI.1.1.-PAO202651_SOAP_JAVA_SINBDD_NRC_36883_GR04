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
public class AreaModel implements DataConverter {
  private final double squareCentimeter = 0.0001;
  private final double squareMeter = 1;
  private final double squareKilometer = 1000000;
  private final double hectare = 10000;
  private final double squareFeet = 0.092903;
  private final double acre = 4046.86;

  @Override
  public Map<String, Double> toMap() {
    Map<String, Double> data = new HashMap<>();

    data.put("cm2", this.squareCentimeter);
    data.put("m2", this.squareMeter);
    data.put("km2", this.squareKilometer);
    data.put("ha", this.hectare);
    data.put("ft2", this.squareFeet);
    data.put("ac", this.acre);

    return data;
  }
}
