package ec.edu.monster.vista;

import java.util.List;

/**
 * Contract between the catalog screen and its controller.
 */
public interface CatalogoVista {

    void mostrarMagnitudes(List<String> magnitudes);

    String getMagnitud();

    void mostrarUnidades(List<String> unidades);

    void mostrarError(String mensaje);

    /** Hides any previous error before loading a new catalog. */
    void limpiarMensajes();

    void alCambiarMagnitud(Runnable accion);
}
