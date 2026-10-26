package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.FormatoResultado;
import ec.edu.monster.modelo.Magnitudes;
import ec.edu.monster.modelo.ServicioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Conversion page: loads the units of the selected magnitude, validates the
 * input and shows the formatted result. The user's selections are kept.
 */
@WebServlet("/conversion")
public class ConversionServlet extends HttpServlet {

    private static final String VISTA = "/ec/edu/monster/vista/conversion.jsp";
    private static final String CAMBIAR_MAGNITUD = "cambiar";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String magnitud = magnitudOPrimera(request.getParameter("magnitud"));
        Formulario formulario = new Formulario(magnitud, null, null, "");
        cargarUnidades(request, formulario);
        mostrar(request, response, formulario);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Formulario formulario = new Formulario(
                request.getParameter("magnitud"),
                request.getParameter("desde"),
                request.getParameter("hacia"),
                request.getParameter("valor"));
        if (CAMBIAR_MAGNITUD.equals(request.getParameter("accion"))) {
            // Only the magnitude changed: reload its units without converting.
            formulario.magnitud = magnitudOPrimera(formulario.magnitud);
            cargarUnidades(request, formulario);
            mostrar(request, response, formulario);
            return;
        }
        List<String> unidades = cargarUnidades(request, formulario);
        if (request.getAttribute("error") == null) {
            convertir(request, formulario, unidades);
        }
        mostrar(request, response, formulario);
    }

    private void convertir(HttpServletRequest request, Formulario formulario, List<String> unidades) {
        String error = ValidadorConversion.validar(
                formulario.valor, formulario.magnitud, formulario.desde, formulario.hacia);
        if (error == null && (!contiene(unidades, formulario.desde) || !contiene(unidades, formulario.hacia))) {
            // Units from another magnitude (e.g. a stale form) are treated as not selected.
            error = ValidadorConversion.SIN_UNIDADES;
        }
        if (error != null) {
            request.setAttribute("error", error);
            return;
        }
        double valor = ValidadorConversion.leerValor(formulario.valor);
        try {
            double resultado = SesionUsuario.servicio(request)
                    .convertir(formulario.magnitud, valor, formulario.desde, formulario.hacia);
            request.setAttribute("resultado",
                    FormatoResultado.formatear(valor, formulario.desde, resultado, formulario.hacia));
        } catch (ServicioException e) {
            request.setAttribute("error", e.getMessage());
        }
    }

    // Loads the units of the form's magnitude; on failure the page shows no units and the error.
    private static List<String> cargarUnidades(HttpServletRequest request, Formulario formulario) {
        List<String> unidades = List.of();
        if (contiene(Magnitudes.TODAS, formulario.magnitud)) {
            try {
                ConversorServicio servicio = SesionUsuario.servicio(request);
                unidades = servicio.catalogo(formulario.magnitud);
            } catch (ServicioException e) {
                request.setAttribute("error", e.getMessage());
            }
        }
        if (!contiene(unidades, formulario.desde)) {
            formulario.desde = null;
        }
        if (!contiene(unidades, formulario.hacia)) {
            formulario.hacia = null;
        }
        request.setAttribute("unidades", Opcion.unidades(unidades));
        return unidades;
    }

    private static void mostrar(HttpServletRequest request, HttpServletResponse response, Formulario formulario)
            throws ServletException, IOException {
        request.setAttribute("magnitudes", Opcion.magnitudes());
        request.setAttribute("magnitud", formulario.magnitud);
        request.setAttribute("desde", formulario.desde);
        request.setAttribute("hacia", formulario.hacia);
        request.setAttribute("valor", formulario.valor == null ? "" : formulario.valor);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    private static String magnitudOPrimera(String magnitud) {
        return contiene(Magnitudes.TODAS, magnitud) ? magnitud : Magnitudes.TODAS.get(0);
    }

    // Immutable lists reject contains(null), so null is checked first.
    private static boolean contiene(List<String> lista, String valor) {
        return valor != null && lista.contains(valor);
    }

    // Values typed or selected by the user, sent back to the page.
    private static final class Formulario {
        String magnitud;
        String desde;
        String hacia;
        final String valor;

        Formulario(String magnitud, String desde, String hacia, String valor) {
            this.magnitud = magnitud;
            this.desde = desde;
            this.hacia = hacia;
            this.valor = valor;
        }
    }
}
