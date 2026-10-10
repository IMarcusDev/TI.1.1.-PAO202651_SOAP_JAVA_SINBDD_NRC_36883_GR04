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
public class TiempoModel implements DataConverter {
  private final double millisecond = 0.001;
  private final double second = 1;
  private final double minute = 60;
  private final double hour = 60 * 60;
  private final double day = 60 * 60 * 24;
  private final double week = 60 * 60 * 24 * 7;

  @Override
  public Map<String, Double> toMap() {
    Map<String, Double> data = new HashMap<>();

    data.put("ms" , this.millisecond);
    data.put("s" , this.second);
    data.put("min" , this.minute);
    data.put("h" , this.hour);
    data.put("d" , this.day);
    data.put("w" , this.week);

    return data;
  }
}
