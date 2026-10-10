/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/WebService.java to edit this template
 */
package ec.edu.monster.ws;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import ec.edu.monster.controlador.ConverterController;

/**
 *
 * @author Mateo Sosa
 */
@WebService(serviceName = "ConversorWebService")
public class ConversorWebService {
  @WebMethod(operationName = "convert")
  public String convert(
    @WebParam(name = "type") String type,
    @WebParam(name = "value") double value,
    @WebParam(name = "from") String from,
    @WebParam(name = "to") String to
  ) {
    return ConverterController.convert(type, value, from, to);
  }
}
