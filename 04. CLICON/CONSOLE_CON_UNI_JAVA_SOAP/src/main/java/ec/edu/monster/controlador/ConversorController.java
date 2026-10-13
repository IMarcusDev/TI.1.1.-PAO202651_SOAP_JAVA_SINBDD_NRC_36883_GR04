/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelos.enums.TypesEnum;
import ec.edu.monster.modelos.http.ConfigHTTP;
import ec.edu.monster.modelos.http.ConversorHTTP;
import ec.edu.monster.ws.CatalogDTO;
import ec.edu.monster.ws.ConverterDTO;

/**
 *
 * @author Mateo Sosa
 */
public class ConversorController {
  private TypesEnum type;
  private ConversorHTTP http;
  
  public ConversorController(TypesEnum type) {
    this.type = type;
    this.http = new ConversorHTTP();
  }

  public ConverterDTO makeConversion(double value, String from, String to) {
    ConverterDTO dto = this.http.convert(
      ConfigHTTP.token,
      this.type.toString(),
      value,
      from,
      to
    );
    
    return dto;
  }
  
  public CatalogDTO getCatalog() {
    CatalogDTO dto = this.http.getCatalog(
      ConfigHTTP.token,
      this.type.toString()
    );
    
    return dto;
  }
}
