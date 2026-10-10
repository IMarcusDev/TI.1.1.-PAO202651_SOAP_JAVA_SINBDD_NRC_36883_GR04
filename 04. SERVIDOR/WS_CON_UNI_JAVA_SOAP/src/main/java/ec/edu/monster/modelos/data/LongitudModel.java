/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.data;

import ec.edu.monster.modelos.interfaces.DataConverter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Mateo Sosa
 */
public class LongitudModel implements DataConverter {
  // private final double kilometer = 1000;
  // private final double hectometer = 100;
  // private final double decameter = 10;
  private final double meter = 1;
  // private final double decimeter = 0.1;
  private final double centimeter = 0.01;
  //private final double millimeter = 0.001;
  private final double feet = 0.3048;
  private final double yard = 0.9144;
  private final double mile = 1609.344;

  @Override
  public Map<String, Double> toMap() {
     Map<String, Double> data = new HashMap<>();
     
     // data.put("km", this.kilometer);
     // data.put("hm", this.hectometer);
     // data.put("Dm", this.decameter);
     data.put("m", this.meter);
     // data.put("dm", this.decimeter);
     data.put("cm", this.centimeter);
     // data.put("mm", this.millimeter);
     data.put("ft", this.feet);
     data.put("yd", this.yard);
     data.put("mi", this.mile);
     
     return data;
  }
}
