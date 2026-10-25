package ec.edu.monster.modelos.data;

public class LoginResultado {
    private final boolean exito;
    private final String mensaje;
    private final String token;

    public LoginResultado(boolean exito, String mensaje, String token) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.token = token;
    }

    public static LoginResultado error(String mensaje) {
        return new LoginResultado(false, mensaje, null);
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getToken() {
        return token;
    }
}
