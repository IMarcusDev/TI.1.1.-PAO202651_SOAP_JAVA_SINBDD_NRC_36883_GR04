/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.http;

import ec.edu.monster.ws.CatalogDTO;
import ec.edu.monster.ws.ConversorWebService_Service;
import ec.edu.monster.ws.ConversorWebService;
import ec.edu.monster.ws.ConverterDTO;

/**
 *
 * @author Mateo Sosa
 */
public class ConversorHTTP {
  private ConversorWebService_Service service = new ConversorWebService_Service();
  private ConversorWebService port = service.getConversorWebServicePort();
  
  public ConverterDTO convert(String token, String type, double value, String from, String to) {
    return port.convert(token, type, value, from, to);
  }
  
  public CatalogDTO getCatalog(String token, String type) {
    return port.catalog(token, type);
  }
}
