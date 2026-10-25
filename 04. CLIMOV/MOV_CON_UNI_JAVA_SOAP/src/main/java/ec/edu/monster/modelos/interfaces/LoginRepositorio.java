package ec.edu.monster.modelos.interfaces;

import ec.edu.monster.modelos.data.LoginResultado;

public interface LoginRepositorio {
    LoginResultado iniciarSesion(String usuario, String clave);
}
