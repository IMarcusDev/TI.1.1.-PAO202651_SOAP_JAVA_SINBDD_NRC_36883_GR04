package ec.edu.monster.pruebas;

import static ec.edu.monster.pruebas.ConversionPanelTest.buscarTexto;
import static ec.edu.monster.pruebas.ConversionPanelTest.enEdt;
import static ec.edu.monster.pruebas.ConversionPanelTest.esperar;
import static ec.edu.monster.pruebas.ConversionPanelTest.esperarHasta;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ec.edu.monster.vista.CatalogoPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Headless checks of the catalog view threading contract.
 */
class CatalogoPanelTest {

    private static final String ERROR_INESPERADO = "Ocurrió un error inesperado";

    private CatalogoPanel panel;
    private JList<?> listaMagnitudes;
    private JTable tablaUnidades;

    @BeforeEach
    void crearPanel() throws Exception {
        panel = enEdt(CatalogoPanel::new);
        listaMagnitudes = ConversionPanelTest.<JList<?>>buscar(panel, JList.class).getFirst();
        tablaUnidades = ConversionPanelTest.<JTable>buscar(panel, JTable.class).getFirst();
    }

    @Test
    void exposesAndSelectsTheFirstMagnitudeRightAfterShowingMagnitudes() throws Exception {
        enEdt(() -> panel.mostrarMagnitudes(List.of("Area", "Masa")));

        assertEquals("Area", panel.getMagnitud());
        assertEquals("Area", enEdt(listaMagnitudes::getSelectedValue));
    }

    @Test
    void keepsShowingTheOriginalMagnitudesWhenTheCallerMutatesTheList() throws Exception {
        List<String> magnitudes = new ArrayList<>(List.of("Area", "Masa"));

        // Hold the EDT so the model update is still queued while the list changes.
        CountDownLatch edtRetenido = new CountDownLatch(1);
        CountDownLatch soltarEdt = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            edtRetenido.countDown();
            esperar(soltarEdt);
        });
        esperar(edtRetenido);
        panel.mostrarMagnitudes(magnitudes);
        magnitudes.set(0, "Cambiada");
        magnitudes.add("Extra");
        soltarEdt.countDown();

        assertEquals("Area", panel.getMagnitud());
        assertEquals(List.of("Area", "Masa"), enEdt(this::elementos));
    }

    @Test
    void programmaticFillsDoNotFireTheMagnitudeAction() throws Exception {
        AtomicInteger disparos = new AtomicInteger();
        panel.alCambiarMagnitud(disparos::incrementAndGet);

        enEdt(() -> {
            panel.mostrarMagnitudes(List.of("Area", "Masa"));
            panel.mostrarUnidades(List.of("m2", "ha"));
        });

        assertTrue(enEdt(listaMagnitudes::isEnabled), "no background action must have started");
        assertEquals(0, disparos.get());
        assertEquals(2, enEdt(tablaUnidades::getRowCount));
    }

    @Test
    void locksTheMagnitudeListWhileUnitsLoadAndUnlocksAfterwards() throws Exception {
        enEdt(() -> panel.mostrarMagnitudes(List.of("Area", "Masa")));
        CountDownLatch iniciada = new CountDownLatch(1);
        CountDownLatch soltar = new CountDownLatch(1);
        panel.alCambiarMagnitud(() -> {
            iniciada.countDown();
            esperar(soltar);
        });

        enEdt(() -> listaMagnitudes.setSelectedIndex(1));
        esperar(iniciada);

        assertEquals("Masa", panel.getMagnitud());
        assertFalse(enEdt(listaMagnitudes::isEnabled));
        soltar.countDown();
        esperarHasta(listaMagnitudes::isEnabled);
    }

    @Test
    void reportsAGenericErrorAndUnlocksWhenTheActionFails() throws Exception {
        enEdt(() -> panel.mostrarMagnitudes(List.of("Area", "Masa")));
        panel.alCambiarMagnitud(() -> {
            throw new IllegalStateException("boom");
        });

        enEdt(() -> listaMagnitudes.setSelectedIndex(1));

        esperarHasta(() -> listaMagnitudes.isEnabled() && !buscarTexto(panel, ERROR_INESPERADO).isEmpty());
    }

    private List<Object> elementos() {
        List<Object> elementos = new ArrayList<>();
        for (int i = 0; i < listaMagnitudes.getModel().getSize(); i++) {
            elementos.add(listaMagnitudes.getModel().getElementAt(i));
        }
        return elementos;
    }
}
