/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

package ec.edu.monster.modelo;

/**
 *
 * @author IMarcusDev
 */
public enum Magnitud {
    AREA("Area"),
    LONGITUD("Longitud"),
    MASA("Masa"),
    TEMPERATURA("Temperatura"),
    TIEMPO("Tiempo"),
    VOLUMEN("Volumen");
    
    private final String codigo;
    
    Magnitud(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }
}
