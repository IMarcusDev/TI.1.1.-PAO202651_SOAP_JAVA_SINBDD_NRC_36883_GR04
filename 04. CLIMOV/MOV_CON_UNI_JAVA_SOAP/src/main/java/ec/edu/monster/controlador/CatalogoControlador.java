package ec.edu.monster.controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import ec.edu.monster.modelos.data.CatalogoCompletoResultado;
import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.data.GrupoUnidades;
import ec.edu.monster.modelos.data.NombresUnidades;
import ec.edu.monster.modelos.data.UnidadItem;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.servicios.SesionServicio;

public class CatalogoControlador extends ControladorConSesion {

    private final CatalogoRepositorio repositorio;
    private final Executor paralelo;

    public CatalogoControlador(CatalogoRepositorio repositorio, SesionServicio sesion, Executor paralelo) {
        super(sesion);
        this.repositorio = repositorio;
        this.paralelo = paralelo;
    }

    public CatalogoCompletoResultado obtenerCatalogo() {
        if (!haySesion()) {
            return CatalogoCompletoResultado.expirada(MENSAJE_SESION_EXPIRADA);
        }

        String token = getToken();
        Magnitud[] magnitudes = Magnitud.values();
        List<CompletableFuture<CatalogoResultado>> pendientes = new ArrayList<>();
        for (Magnitud magnitud : magnitudes) {
            pendientes.add(CompletableFuture.supplyAsync(
                    () -> repositorio.obtenerCatalogo(token, magnitud), paralelo));
        }

        List<CatalogoResultado> resultados = new ArrayList<>();
        for (CompletableFuture<CatalogoResultado> pendiente : pendientes) {
            resultados.add(pendiente.join());
        }

        for (CatalogoResultado resultado : resultados) {
            if (resultado.isSesionExpirada()) {
                limpiarSiExpiro(true);
                return CatalogoCompletoResultado.expirada(MENSAJE_SESION_EXPIRADA);
            }
        }

        for (CatalogoResultado resultado : resultados) {
            if (!resultado.isExito()) {
                return CatalogoCompletoResultado.error(resultado.getMensaje());
            }
        }

        List<GrupoUnidades> grupos = new ArrayList<>();
        for (int i = 0; i < magnitudes.length; i++) {
            List<UnidadItem> unidades = new ArrayList<>();
            for (String codigo : resultados.get(i).getUnidades()) {
                unidades.add(new UnidadItem(codigo, NombresUnidades.nombre(codigo)));
            }
            grupos.add(new GrupoUnidades(magnitudes[i], unidades));
        }
        return CatalogoCompletoResultado.ok(grupos);
    }
}
