/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelos.interfaces.LoginRepository;
import ec.edu.monster.modelos.repositorios.LoginImplRepository;
import ec.edu.monster.servicios.TokenService;

/**
 *
 * @author Mateo Sosa
 */
public class LoginController {
  public static String login(String username, String password) throws Exception {
    LoginRepository repository = new LoginImplRepository();
    
    return repository.validateCredentials(username, password);
  }
  
  public static boolean validateUser(String token) {
    return TokenService.validateToken(token);
  }
}
