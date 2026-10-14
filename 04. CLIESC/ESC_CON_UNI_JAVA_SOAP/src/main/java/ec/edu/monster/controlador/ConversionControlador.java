package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.FormatoResultado;
import ec.edu.monster.modelo.Magnitudes;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.vista.ConversionVista;

/**
 * Handles the conversion screen: loads the units of the selected magnitude,
 * validates the input and shows the formatted result.
 */
public class ConversionControlador {

    private static final String VALOR_INVALIDO = "Ingrese un valor numérico válido";
    private static final String SIN_UNIDADES = "Seleccione la unidad de origen y de destino";

    private final ConversionVista vista;
    private final ConversorServicio servicio;

    public ConversionControlador(ConversionVista vista, ConversorServicio servicio) {
        this.vista = vista;
        this.servicio = servicio;
        vista.alCambiarMagnitud(this::cargarUnidades);
        vista.alConvertir(this::convertir);
        vista.mostrarMagnitudes(Magnitudes.TODAS);
        cargarUnidades();
    }

    private void cargarUnidades() {
        String magnitud = vista.getMagnitud();
        if (magnitud == null) {
            return;
        }
        try {
            vista.mostrarUnidades(servicio.catalogo(magnitud));
        } catch (ServicioException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void convertir() {
        Double valor = leerValor(vista.getValor());
        if (valor == null) {
            vista.mostrarError(VALOR_INVALIDO);
            return;
        }
        String desde = vista.getUnidadOrigen();
        String hacia = vista.getUnidadDestino();
        if (desde == null || hacia == null) {
            vista.mostrarError(SIN_UNIDADES);
            return;
        }
        try {
            double resultado = servicio.convertir(vista.getMagnitud(), valor, desde, hacia);
            vista.mostrarResultado(FormatoResultado.formatear(valor, desde, resultado, hacia));
        } catch (ServicioException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // Accepts both "2.5" and "2,5"; returns null for anything that is not a finite number.
    private static Double leerValor(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            double valor = Double.parseDouble(texto.trim().replace(',', '.'));
            return Double.isFinite(valor) ? valor : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
