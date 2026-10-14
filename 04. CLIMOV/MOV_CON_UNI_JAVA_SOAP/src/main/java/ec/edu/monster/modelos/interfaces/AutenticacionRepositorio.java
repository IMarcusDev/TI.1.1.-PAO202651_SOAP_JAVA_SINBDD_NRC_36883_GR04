package ec.edu.monster.modelos.interfaces;

import ec.edu.monster.modelos.excepciones.SoapException;

public interface AutenticacionRepositorio {
    String iniciarSesion(String usuario, String clave) throws SoapException;
}
