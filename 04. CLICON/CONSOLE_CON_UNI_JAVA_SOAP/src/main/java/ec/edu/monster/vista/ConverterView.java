/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.vista;

import ec.edu.monster.controlador.ConversorController;
import ec.edu.monster.modelos.enums.TypesEnum;
import ec.edu.monster.vista.utils.MessagesUtils;
import ec.edu.monster.ws.CatalogDTO;
import ec.edu.monster.ws.ConverterDTO;
import ec.edu.monster.ws.EstadosEnum;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Mateo Sosa
 */
public class ConverterView {
  private static final Scanner scanner = new Scanner(System.in);
  
  public static void showTypes() {
    MessagesUtils.printMessage("1. Área");
    MessagesUtils.printMessage("2. Longitud");
    MessagesUtils.printMessage("3. Masa / Peso");
    MessagesUtils.printMessage("4. Temperatura");
    MessagesUtils.printMessage("5. Tiempo");
    MessagesUtils.printMessage("6. Volumen");
  }
  
  public static TypesEnum selectType() {
    MessagesUtils.printMessage("Seleccione el tipo de conversión a realizar: ");
    
    ConverterView.showTypes();

    MessagesUtils.printMessage("Opción: ");
    String option = scanner.nextLine().trim().toLowerCase(Locale.ENGLISH);

    switch (option) {
      case "1", "area":
        return TypesEnum.Area;
      case "2", "longitud":
        return TypesEnum.Longitud;
      case "3", "masa", "peso":
        return TypesEnum.Masa;
      case "4", "temperatura":
        return TypesEnum.Temperatura;
      case "5", "tiempo":
        return TypesEnum.Tiempo;
      case "6", "volumen":
        return TypesEnum.Volumen;
      default:
        MessagesUtils.printMessage("Opción no válida. Saliendo.");
        return TypesEnum.None;
    }
  }
  
  public static void showCatalog() {
    TypesEnum type = ConverterView.selectType();
    
    ConversorController ctrl = new ConversorController(type);
    
    CatalogDTO dto = ctrl.getCatalog();
    
    if (dto.getEstado() == EstadosEnum.ERROR) {
      return;
    }
    
    MessagesUtils.printMessage(dto.getMensaje());
    
    List<String> res = dto.getResultado().getUnidad();
    
    for (String s : res) {
      MessagesUtils.printMessage(s);
    }
  }
  
  public static void showConversor() {
    TypesEnum type = ConverterView.selectType();
    
    ConversorController ctrl = new ConversorController(type);
    
    CatalogDTO catalogDto = ctrl.getCatalog();
    
    if (catalogDto.getEstado() == EstadosEnum.ERROR) {
      return;
    }
    
    MessagesUtils.printMessage(catalogDto.getMensaje());
    
    List<String> res = catalogDto.getResultado().getUnidad();
    
    for (String s : res) {
      MessagesUtils.printMessage(s);
    }
    
    String from;
    String to;
    double value;
    
    try {
      MessagesUtils.printMessage("Ingresa el valor a convertir: ");
      
      value = Double.parseDouble(scanner.nextLine().trim());
    } catch (NumberFormatException e) {
      MessagesUtils.printMessage("Error: ingresa un valor numérico válido.");
      return;
    }

    MessagesUtils.printMessage("Ingresa la unidad inicial: ");
    from = scanner.nextLine();
    
    MessagesUtils.printMessage("Ingresa la unidad final: ");
    to = scanner.nextLine();
    
    ConverterDTO dto = ctrl.makeConversion(value, from, to);
    
    MessagesUtils.printMessage(dto.getEstado() == EstadosEnum.OK ? dto.getMensaje() : "ERROR: " + dto.getMensaje());
    MessagesUtils.printMessage("" + dto.getResultado());
  }
  
  public static void showMenu() {
    boolean keepInMenu = true;
    
    while (keepInMenu) {
      MessagesUtils.printTitle("Menú");

      MessagesUtils.printMessage("¿Qué desea realizar?");

      MessagesUtils.printMessage("1. Conversión de unidades");
      MessagesUtils.printMessage("2. Ver Catálogo");
      MessagesUtils.printMessage("3. Salir");

      int option = 3;
      
      try {
        option = Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        MessagesUtils.printMessage("Error: ingresa un valor entero válido.");
        return;
      }
    
      switch (option) {
        case 1:
          ConverterView.showConversor();
          break;
        case 2:
          ConverterView.showCatalog();
          break;
        case 3:
          keepInMenu = false;
          break;
        default:
          MessagesUtils.printMessage("Opción Inválida!");
      } 
    }
  }
}
