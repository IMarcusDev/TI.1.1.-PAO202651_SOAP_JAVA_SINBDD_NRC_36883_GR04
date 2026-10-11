/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.dto;

import ec.edu.monster.modelos.enums.EstadosEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.Set;

/**
 *
 * @author Mateo Sosa
 */
public class CatalogDTO extends ResponseDTO {
  private Set<String> resultado;
  
  public CatalogDTO(EstadosEnum estado, String mensaje, Set<String> resultado) {
    super(estado, mensaje);
    
    this.resultado = resultado;
  }
  
  @XmlElementWrapper(name = "resultado")
  @XmlElement(name = "unidad")
  public void setResultado(Set<String> resultado) {
    this.resultado = resultado;
  }
  
  public Set<String> getResultado() {
    return this.resultado;
  }
}
