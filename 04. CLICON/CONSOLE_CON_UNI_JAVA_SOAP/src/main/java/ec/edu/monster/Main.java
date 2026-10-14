package ec.edu.monster;


import ec.edu.monster.vista.ConverterView;
import ec.edu.monster.vista.LoginView;
import ec.edu.monster.vista.utils.MessagesUtils;
import java.util.concurrent.TimeUnit;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Mateo Sosa
 */
public class Main {
  public static void main(String[] args) throws InterruptedException {
    // Splash Art
    MessagesUtils.printMonster();
    TimeUnit.SECONDS.sleep(3);
    
    // Login
    boolean login = LoginView.userHasLogin();
    
    if (!login) {
      return;
    }
    
    // Menu
    ConverterView.showMenu();
    
    MessagesUtils.printMessage("Hasta Luego!");
    TimeUnit.SECONDS.sleep(3);
  }
}
