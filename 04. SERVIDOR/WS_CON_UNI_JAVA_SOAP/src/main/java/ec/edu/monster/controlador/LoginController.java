/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelos.interfaces.LoginRepository;
import ec.edu.monster.modelos.repositorios.LoginImplRepository;

/**
 *
 * @author Mateo Sosa
 */
public class LoginController {
  public static String login(String username, String password) {
    try {
      LoginRepository repository = new LoginImplRepository();
      
      return repository.validateCredentials(username, password);
    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
