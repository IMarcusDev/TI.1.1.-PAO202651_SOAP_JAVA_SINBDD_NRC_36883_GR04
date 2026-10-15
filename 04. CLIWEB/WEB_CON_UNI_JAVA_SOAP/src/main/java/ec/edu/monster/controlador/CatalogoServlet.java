package ec.edu.monster.controlador;

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
 * Catalog page: lists the units available for the selected magnitude.
 */
@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    private static final String VISTA = "/ec/edu/monster/vista/catalogo.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String parametro = request.getParameter("magnitud");
        // Unknown or missing magnitudes fall back to the first one.
        String magnitud = parametro != null && Magnitudes.TODAS.contains(parametro)
                ? parametro : Magnitudes.TODAS.get(0);
        List<String> unidades = List.of();
        try {
            unidades = SesionUsuario.servicio(request).catalogo(magnitud);
        } catch (ServicioException e) {
            // Never show units of another magnitude next to the error.
            request.setAttribute("error", e.getMessage());
        }
        request.setAttribute("magnitudes", Opcion.magnitudes());
        request.setAttribute("magnitud", magnitud);
        request.setAttribute("unidades", Opcion.unidades(unidades));
        request.getRequestDispatcher(VISTA).forward(request, response);
    }
}
