package ec.edu.monster.servicios;

public class SesionServicio {

    private volatile String token;
    private volatile String usuario;

    public String getToken() {
        return token;
    }

    public String getUsuario() {
        return usuario;
    }

    public boolean estaAutenticado() {
        String actual = token;
        return actual != null && !actual.trim().isEmpty();
    }

    public void iniciar(String usuario, String token) {
        this.usuario = usuario;
        this.token = token;
    }

    public void limpiar() {
        usuario = null;
        token = null;
    }
}
