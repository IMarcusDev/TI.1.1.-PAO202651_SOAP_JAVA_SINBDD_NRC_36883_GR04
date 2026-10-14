/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.vista;

import ec.edu.monster.controlador.LoginController;
import ec.edu.monster.vista.utils.MessagesUtils;
import ec.edu.monster.ws.EstadosEnum;
import ec.edu.monster.ws.ResponseDTO;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Mateo Sosa
 */
public class LoginView {
  public static boolean userHasLogin() throws InterruptedException {
    LoginController ctrl = new LoginController();
    
    Scanner scanner = new Scanner(System.in);
    
    String username;
    String password;
    
    // Username
    MessagesUtils.printMessage("Username: ");
    username = scanner.nextLine();
    
    // Password
    MessagesUtils.printMessage("Password: ");
    password = scanner.nextLine();
    
    ResponseDTO res = ctrl.login(username, password);
    
    MessagesUtils.printMessage(res.getMensaje());
    TimeUnit.SECONDS.sleep(3);

    return res.getEstado() == EstadosEnum.OK;
  }
  
}
