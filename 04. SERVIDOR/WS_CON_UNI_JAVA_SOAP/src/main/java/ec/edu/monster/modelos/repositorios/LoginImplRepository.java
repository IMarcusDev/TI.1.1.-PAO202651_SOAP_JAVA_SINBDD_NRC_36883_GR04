/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.modelos.interfaces.LoginRepository;
import ec.edu.monster.servicios.TokenService;

/**
 *
 * @author Mateo Sosa
 */
public class LoginImplRepository implements LoginRepository {

  @Override
  public String validateCredentials(String username, String password) throws Exception {
    // No DB
    if (!"user".equals(username) || !"1234".equals(password)) {
      throw new Exception("Invalid User");
    }
    
    return TokenService.generateToken();
  }
  
}
