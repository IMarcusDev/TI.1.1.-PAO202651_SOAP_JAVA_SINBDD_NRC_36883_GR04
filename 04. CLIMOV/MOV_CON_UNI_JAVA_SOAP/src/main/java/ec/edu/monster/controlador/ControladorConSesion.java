package ec.edu.monster.controlador;

import ec.edu.monster.servicios.SesionServicio;

public abstract class ControladorConSesion {

    public static final String MENSAJE_SESION_EXPIRADA = "Su sesión expiró o no es válida. Inicie sesión nuevamente.";

    protected final SesionServicio sesion;

    protected ControladorConSesion(SesionServicio sesion) {
        this.sesion = sesion;
    }

    protected String getToken() {
        return sesion.getToken();
    }

    public boolean haySesion() {
        return sesion.estaAutenticado();
    }

    public void cerrarSesion() {
        sesion.limpiar();
    }

    protected void limpiarSiExpiro(boolean sesionExpirada) {
        if (sesionExpirada) {
            sesion.limpiar();
        }
    }
}
