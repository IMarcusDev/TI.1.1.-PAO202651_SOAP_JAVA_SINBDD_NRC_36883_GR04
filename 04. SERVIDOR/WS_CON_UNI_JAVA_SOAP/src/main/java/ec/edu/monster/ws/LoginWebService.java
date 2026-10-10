/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/WebService.java to edit this template
 */
package ec.edu.monster.ws;

import ec.edu.monster.controlador.LoginController;
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
  public String login(
    @WebParam(name = "username") String username,
    @WebParam(name = "password") String password
  ) {
    return LoginController.login(username, password);
  }
}
