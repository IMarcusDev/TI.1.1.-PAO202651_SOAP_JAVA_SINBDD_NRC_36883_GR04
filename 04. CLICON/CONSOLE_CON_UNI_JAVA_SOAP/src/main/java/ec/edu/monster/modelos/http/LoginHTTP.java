/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.http;

import ec.edu.monster.ws.LoginDTO;
import ec.edu.monster.ws.LoginWebService;
import ec.edu.monster.ws.LoginWebService_Service;

/**
 *
 * @author Mateo Sosa
 */
public class LoginHTTP {
  private LoginWebService_Service service = new LoginWebService_Service();
  private LoginWebService port = service.getLoginWebServicePort();
  
  public LoginDTO login(String username, String password) {
    return port.login(username, password);
  }
}
