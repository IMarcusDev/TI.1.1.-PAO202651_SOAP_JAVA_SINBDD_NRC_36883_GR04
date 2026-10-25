package ec.edu.monster.controlador;

import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.modelos.interfaces.LoginRepositorio;
import ec.edu.monster.servicios.SesionServicio;

public class LoginControlador extends ControladorConSesion {

    public static final int MAX_USUARIO = 50;
    public static final int MAX_CLAVE = 64;

    private final LoginRepositorio repositorio;

    public LoginControlador(LoginRepositorio repositorio, SesionServicio sesion) {
        super(sesion);
        this.repositorio = repositorio;
    }

    public LoginResultado iniciarSesion(String usuarioIngresado, String claveIngresada) {
        String usuario = usuarioIngresado != null ? usuarioIngresado.trim() : "";
        String clave = claveIngresada != null ? claveIngresada.trim() : "";

        if (usuario.isEmpty()) {
            return LoginResultado.error("Ingrese el usuario");
        }
        if (clave.isEmpty()) {
            return LoginResultado.error("Ingrese la contraseña");
        }
        if (usuario.length() > MAX_USUARIO) {
            return LoginResultado.error("El usuario no puede superar " + MAX_USUARIO + " caracteres");
        }
        if (clave.length() > MAX_CLAVE) {
            return LoginResultado.error("La contraseña no puede superar " + MAX_CLAVE + " caracteres");
        }

        LoginResultado resultado = repositorio.iniciarSesion(usuario, clave);

        if (resultado.isExito() && resultado.getToken() != null) {
            sesion.iniciar(usuario, resultado.getToken());
        }
        return resultado;
    }
}
