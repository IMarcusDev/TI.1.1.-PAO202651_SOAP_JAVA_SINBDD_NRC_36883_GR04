package ec.edu.monster.vista;

import java.util.List;

/**
 * Contract between the conversion screen and its controller.
 */
public interface ConversionVista {

    void mostrarMagnitudes(List<String> magnitudes);

    void mostrarUnidades(List<String> unidades);

    String getMagnitud();

    String getValor();

    String getUnidadOrigen();

    String getUnidadDestino();

    void mostrarResultado(String texto);

    void mostrarError(String mensaje);

    /** Hides any previous result and error before a new action. */
    void limpiarMensajes();

    void alConvertir(Runnable accion);

    void alCambiarMagnitud(Runnable accion);
}
