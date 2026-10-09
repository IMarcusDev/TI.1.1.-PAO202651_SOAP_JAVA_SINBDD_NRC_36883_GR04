/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.interfaces;

import ec.edu.monster.modelos.interfaces.DataConverter;

/**
 *
 * @author Mateo Sosa
 */
public interface ConversorRepository {  
  public abstract double convert(double value, String from, String to);
}
