/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/WebService.java to edit this template
 */
package ec.edu.monster.ws;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import ec.edu.monster.controlador.ConverterController;
import ec.edu.monster.controlador.LoginController;
import ec.edu.monster.modelos.dto.CatalogDTO;
import ec.edu.monster.modelos.dto.ConverterDTO;
import ec.edu.monster.modelos.enums.EstadosEnum;
import java.util.Collections;

/**
 *
 * @author Mateo Sosa
 */
@WebService(serviceName = "ConversorWebService")
public class ConversorWebService {
  @WebMethod(operationName = "convert")
  public ConverterDTO convert(
    @WebParam(name = "token") String token,
    @WebParam(name = "type") String type,
    @WebParam(name = "value") double value,
    @WebParam(name = "from") String from,
    @WebParam(name = "to") String to
  ) {
    if (!LoginController.validateUser(token))
      return new ConverterDTO(EstadosEnum.ERROR, "Invalid Token", 0);
    
    return new ConverterDTO(
      EstadosEnum.OK,
      "Conversión Exitosa",
      ConverterController.convert(type, value, from, to)
    );
  }
  
  @WebMethod(operationName = "catalog")
  public CatalogDTO getCatalog(
    @WebParam(name = "token") String token,
    @WebParam(name = "type") String type
  ) {
    if (!LoginController.validateUser(token))
      return new CatalogDTO(
        EstadosEnum.ERROR,
        "Token Inválido",
        Collections.emptySet()
      );
    
    try {
      return new CatalogDTO(
        EstadosEnum.OK,
        "Obtención de Catálogo Exitosa",
        ConverterController.getConverter(type).getUnits()
      );
    } catch (Exception e) {
      return new CatalogDTO(
        EstadosEnum.ERROR,
        "Tipo de Catálogo Inexistente",
        Collections.emptySet()
      );
    }
  }
}
