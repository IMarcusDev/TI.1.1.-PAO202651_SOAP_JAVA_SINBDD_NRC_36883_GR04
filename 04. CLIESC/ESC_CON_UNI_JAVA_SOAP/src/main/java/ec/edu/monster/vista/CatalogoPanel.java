package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * "Catálogo" tab: magnitudes on the left, units of the selected one on the right.
 * The server only returns unit symbols, so that is all the table shows.
 */
public class CatalogoPanel extends JPanel implements CatalogoVista {

    private final JList<String> listaMagnitudes = new JList<>();
    private final DefaultTableModel modeloUnidades = new DefaultTableModel(new Object[] {"Unidad"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tablaUnidades = new JTable(modeloUnidades);
    private final JLabel etiquetaError = Estilos.mensajeError();

    private Runnable accionCambiarMagnitud = () -> { };
    // True while the list model is filled programmatically, so no user action is fired.
    private boolean actualizando;
    // Snapshot taken on the EDT so the controller can read it from a worker thread.
    private volatile String magnitudActual;

    public CatalogoPanel() {
        super(new BorderLayout(0, 16));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(Estilos.titulo("Consultar catálogo"), BorderLayout.NORTH);
        add(construirCuerpo(), BorderLayout.CENTER);
        add(etiquetaError, BorderLayout.SOUTH);

        listaMagnitudes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cambiarMagnitud();
            }
        });
    }

    private JPanel construirCuerpo() {
        listaMagnitudes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaMagnitudes.setFont(Estilos.FUENTE);
        listaMagnitudes.setForeground(Estilos.TEXTO);
        listaMagnitudes.setSelectionBackground(Estilos.PRIMARIO);
        listaMagnitudes.setSelectionForeground(Estilos.BLANCO);
        listaMagnitudes.setFixedCellHeight(32);
        JScrollPane scrollMagnitudes = new JScrollPane(listaMagnitudes);
        scrollMagnitudes.setBorder(BorderFactory.createLineBorder(Estilos.BORDE));
        scrollMagnitudes.setPreferredSize(new Dimension(200, 0));

        tablaUnidades.setFont(Estilos.FUENTE);
        tablaUnidades.setForeground(Estilos.TEXTO);
        tablaUnidades.setRowHeight(28);
        tablaUnidades.setGridColor(Estilos.BORDE);
        tablaUnidades.setFocusable(false);
        tablaUnidades.setRowSelectionAllowed(false);
        tablaUnidades.getTableHeader().setFont(Estilos.FUENTE_ETIQUETA);
        tablaUnidades.getTableHeader().setForeground(Estilos.PRIMARIO);
        tablaUnidades.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollUnidades = new JScrollPane(tablaUnidades);
        scrollUnidades.setBorder(BorderFactory.createLineBorder(Estilos.BORDE));
        scrollUnidades.getViewport().setBackground(Estilos.BLANCO);

        JPanel cuerpo = new JPanel(new BorderLayout(16, 0));
        cuerpo.setOpaque(false);
        cuerpo.add(scrollMagnitudes, BorderLayout.WEST);
        cuerpo.add(scrollUnidades, BorderLayout.CENTER);
        return cuerpo;
    }

    private void cambiarMagnitud() {
        if (actualizando) {
            return;
        }
        magnitudActual = listaMagnitudes.getSelectedValue();
        Estilos.enSegundoPlano(accionCambiarMagnitud, this::mostrarError, listaMagnitudes);
    }

    @Override
    public void mostrarMagnitudes(List<String> magnitudes) {
        List<String> copia = List.copyOf(magnitudes);
        // Snapshot set synchronously: the controller reads it right after this call.
        magnitudActual = copia.isEmpty() ? null : copia.get(0);
        Estilos.enEdt(() -> {
            actualizando = true;
            listaMagnitudes.setListData(copia.toArray(String[]::new));
            if (!copia.isEmpty()) {
                listaMagnitudes.setSelectedIndex(0);
            }
            actualizando = false;
        });
    }

    @Override
    public String getMagnitud() {
        return magnitudActual;
    }

    @Override
    public void mostrarUnidades(List<String> unidades) {
        List<String> copia = List.copyOf(unidades);
        Estilos.enEdt(() -> {
            modeloUnidades.setRowCount(0);
            for (String unidad : copia) {
                modeloUnidades.addRow(new Object[] {unidad});
            }
        });
    }

    @Override
    public void mostrarError(String mensaje) {
        Estilos.enEdt(() -> etiquetaError.setText(mensaje));
    }

    @Override
    public void limpiarMensajes() {
        Estilos.enEdt(() -> etiquetaError.setText(" "));
    }

    @Override
    public void alCambiarMagnitud(Runnable accion) {
        this.accionCambiarMagnitud = accion;
    }
}
