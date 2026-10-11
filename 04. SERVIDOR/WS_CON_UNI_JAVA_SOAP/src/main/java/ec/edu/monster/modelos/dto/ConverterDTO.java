/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.dto;

import ec.edu.monster.modelos.enums.EstadosEnum;

/**
 *
 * @author Mateo Sosa
 */
public class ConverterDTO extends ResponseDTO {
  private double resultado;
  
  public ConverterDTO(EstadosEnum estado, String mensaje, double resultado) {
    super(estado, mensaje);
    
    this.resultado = resultado;
  }
  
  public void setResultado(double resultado) {
    this.resultado = resultado;
  }
  
  public double getResultado() {
    return this.resultado;
  }
}
