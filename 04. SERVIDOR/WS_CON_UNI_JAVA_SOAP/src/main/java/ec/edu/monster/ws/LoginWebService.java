/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/WebService.java to edit this template
 */
package ec.edu.monster.ws;

import ec.edu.monster.controlador.LoginController;
import ec.edu.monster.modelos.dto.LoginDTO;
import ec.edu.monster.modelos.enums.EstadosEnum;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

/**
 *
 * @author Mateo Sosa
 */
@WebService(serviceName = "LoginWebService")
public class LoginWebService {
  @WebMethod(operationName = "login")
  public LoginDTO login(
    @WebParam(name = "username") String username,
    @WebParam(name = "password") String password
  ) {
    try {
      String token = LoginController.login(username, password);
      
      return new LoginDTO(
        EstadosEnum.OK,
        "Ingreso de Sesión Exitoso!",
        token
      );
    } catch (Exception e) {
      return new LoginDTO(
        EstadosEnum.ERROR,
        "Credenciales Incorrectas",
        ""
      );
    }
  }
}
