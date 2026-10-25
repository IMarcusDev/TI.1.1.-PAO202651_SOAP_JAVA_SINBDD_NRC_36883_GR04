package ec.edu.monster.pruebas.controlador;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.controlador.CatalogoControlador;
import ec.edu.monster.controlador.ControladorConSesion;
import ec.edu.monster.modelos.data.CatalogoCompletoResultado;
import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.data.GrupoUnidades;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.servicios.SesionServicio;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CatalogoControladorTest {

    private ExecutorService hilos;
    private SesionServicio sesion;

    @Before
    public void preparar() {
        hilos = Executors.newCachedThreadPool();
        sesion = new SesionServicio();
        sesion.iniciar("user", "token-1");
    }

    @After
    public void liberar() {
        hilos.shutdownNow();
    }

    private CatalogoControlador controlador(Function<Magnitud, CatalogoResultado> respuesta) {
        CatalogoRepositorio repositorio = (token, magnitud) -> respuesta.apply(magnitud);
        return new CatalogoControlador(repositorio, sesion, hilos);
    }

    @Test
    public void obtenerCatalogo_todasOk_gruposEnOrdenConNombresEnEspanol() {
        CatalogoCompletoResultado resultado = controlador(m -> CatalogoResultado.ok("ok", Arrays.asList("m", "cm")))
                .obtenerCatalogo();

        assertTrue(resultado.isExito());
        assertEquals(6, resultado.getGrupos().size());
        GrupoUnidades primero = resultado.getGrupos().get(0);
        assertEquals(Magnitud.AREA, primero.getMagnitud());
        assertEquals("Área", primero.getNombre());
        assertEquals("Extensión de una superficie.", primero.getDescripcion());
        assertEquals(2, primero.getCantidad());
        assertEquals("Metro", primero.getUnidades().get(0).getNombre());
        assertEquals("Centímetro", primero.getUnidades().get(1).getNombre());
    }

    @Test
    public void obtenerCatalogo_consultaLasSeisMagnitudesAlMismoTiempo() throws Exception {
        CountDownLatch todasIniciadas = new CountDownLatch(6);
        AtomicInteger llamadas = new AtomicInteger();
        CatalogoControlador controlador = controlador(m -> {
            llamadas.incrementAndGet();
            todasIniciadas.countDown();
            try {
                todasIniciadas.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return CatalogoResultado.ok("ok", Collections.singletonList("u"));
        });

        long inicio = System.nanoTime();
        assertTrue(controlador.obtenerCatalogo().isExito());
        assertTrue("las consultas no fueron en paralelo", System.nanoTime() - inicio < TimeUnit.SECONDS.toNanos(4));
        assertEquals(6, llamadas.get());
    }

    @Test
    public void obtenerCatalogo_unaFalla_noHayCatalogoAMedias() {
        CatalogoCompletoResultado resultado = controlador(m -> m == Magnitud.MASA
                ? CatalogoResultado.error("Tipo de Catálogo Inexistente")
                : CatalogoResultado.ok("ok", Collections.singletonList("u"))).obtenerCatalogo();

        assertFalse(resultado.isExito());
        assertTrue(resultado.getGrupos().isEmpty());
        assertEquals("Tipo de Catálogo Inexistente", resultado.getMensaje());
    }

    @Test
    public void obtenerCatalogo_tokenRechazado_limpiaLaSesion() {
        CatalogoCompletoResultado resultado = controlador(m -> CatalogoResultado.expirada("Token Inválido"))
                .obtenerCatalogo();

        assertTrue(resultado.isSesionExpirada());
        assertEquals(ControladorConSesion.MENSAJE_SESION_EXPIRADA, resultado.getMensaje());
        assertFalse(sesion.estaAutenticado());
    }

    @Test
    public void obtenerCatalogo_sinSesion_noLlamaAlServidor() {
        sesion.limpiar();
        AtomicInteger llamadas = new AtomicInteger();

        CatalogoCompletoResultado resultado = controlador(m -> {
            llamadas.incrementAndGet();
            return CatalogoResultado.ok("ok", Collections.singletonList("u"));
        }).obtenerCatalogo();

        assertTrue(resultado.isSesionExpirada());
        assertEquals(0, llamadas.get());
    }
}
