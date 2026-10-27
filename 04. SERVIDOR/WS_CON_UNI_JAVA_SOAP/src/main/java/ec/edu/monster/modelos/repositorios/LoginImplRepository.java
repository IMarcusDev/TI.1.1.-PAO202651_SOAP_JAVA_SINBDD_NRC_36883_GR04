/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.repositorios;

import java.util.HashMap;

import ec.edu.monster.modelos.interfaces.LoginRepository;
import ec.edu.monster.servicios.TokenService;

/**
 *
 * @author Mateo Sosa
 */
public class LoginImplRepository implements LoginRepository {
  private HashMap<String, String> users;  // HashMap<username, password>
  
  public LoginImplRepository() {
    this.users = new HashMap<String, String>();

    // Credentials
    this.users.put("user", "1234");
    this.users.put("monster", "monster9");
    this.users.put("marcos", "mamita");
    this.users.put("carlos", "carlos9");
    this.users.put("msosa", "msosa");
  }

  @Override
  public String validateCredentials(String username, String password) throws Exception {
    // No DB
    String localUsername = username;
    String localPassword = this.users.get(localUsername);

    if (!localUsername.equals(username) || !localPassword.equals(password)) {
      throw new Exception("Invalid User");
    }
    
    return TokenService.generateToken();
  }
  
}
