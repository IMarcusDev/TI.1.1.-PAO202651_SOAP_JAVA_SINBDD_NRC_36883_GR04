/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelos.http.ConfigHTTP;
import ec.edu.monster.modelos.http.LoginHTTP;
import ec.edu.monster.ws.EstadosEnum;
import ec.edu.monster.ws.LoginDTO;

/**
 *
 * @author Mateo Sosa
 */
public class LoginController {
  public LoginDTO login(String username, String password) {
    LoginHTTP http = new LoginHTTP();
    
    LoginDTO dto = http.login(username, password);
    
    // Save Token
    if (dto.getEstado() == EstadosEnum.OK) {
      ConfigHTTP.token = dto.getToken();
    }
    
    return dto;
  }
}
