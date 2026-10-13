package ec.edu.monster.modelo;

public interface AutenticacionServicio {
    String iniciarSesion(String usuario, String clave) throws ServicioException;
}
