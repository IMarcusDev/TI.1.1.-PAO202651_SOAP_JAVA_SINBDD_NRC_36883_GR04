package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

/**
 * Shared palette, fonts and small widget factories for the Swing views.
 */
final class Estilos {

    static final String TITULO_VENTANA = "Conversión de Unidades Monster";
    static final String ERROR_INESPERADO = "Ocurrió un error inesperado";

    static final Color PRIMARIO = new Color(0x1F4E79);
    static final Color FONDO = new Color(0xF4F6F8);
    static final Color TEXTO = new Color(0x1F2937);
    static final Color TEXTO_SECUNDARIO = new Color(0x4B5563);
    static final Color BORDE = new Color(0xD1D5DB);
    static final Color BORDE_ENTRADA = new Color(0x6B7280);
    static final Color FONDO_RESULTADO = new Color(0xE8F0F8);
    static final Color EXITO = new Color(0x15803D);
    static final Color ERROR = new Color(0xB91C1C);
    static final Color BLANCO = Color.WHITE;

    static final Font FUENTE = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    static final Font FUENTE_ETIQUETA = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    static final Font FUENTE_TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    static final Font FUENTE_RESULTADO = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    private Estilos() {
    }

    static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_TITULO);
        etiqueta.setForeground(PRIMARIO);
        return etiqueta;
    }

    static JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_ETIQUETA);
        etiqueta.setForeground(TEXTO_SECUNDARIO);
        return etiqueta;
    }

    static JLabel mensajeError() {
        JLabel etiqueta = new JLabel(" ");
        etiqueta.setFont(FUENTE);
        etiqueta.setForeground(ERROR);
        return etiqueta;
    }

    static JButton botonPrimario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_ETIQUETA);
        boton.setForeground(BLANCO);
        boton.setBackground(PRIMARIO);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    static Border bordeEntrada() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_ENTRADA),
                BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    static <T extends JComponent> T entrada(T componente) {
        componente.setFont(FUENTE);
        componente.setForeground(TEXTO);
        componente.setBorder(bordeEntrada());
        componente.setPreferredSize(new Dimension(220, 32));
        return componente;
    }

    /** Green "dot + text" line used to show that the service is reachable. */
    static JPanel estadoServicio(String texto) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.setOpaque(false);
        JLabel etiqueta = new JLabel(texto, new Punto(EXITO, 10), JLabel.LEFT);
        etiqueta.setIconTextGap(6);
        etiqueta.setFont(FUENTE);
        etiqueta.setForeground(EXITO);
        panel.add(etiqueta);
        return panel;
    }

    /** Runs the UI update on the EDT, immediately when already there. */
    static void enEdt(Runnable accion) {
        if (SwingUtilities.isEventDispatchThread()) {
            accion.run();
        } else {
            SwingUtilities.invokeLater(accion);
        }
    }

    /**
     * Runs a controller action off the EDT while the given controls stay
     * disabled. Unexpected failures are reported through {@code alFallar}.
     */
    static void enSegundoPlano(Runnable accion, Consumer<String> alFallar, JComponent... controles) {
        for (JComponent control : controles) {
            control.setEnabled(false);
        }
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                accion.run();
                return null;
            }

            @Override
            protected void done() {
                for (JComponent control : controles) {
                    control.setEnabled(true);
                }
                try {
                    get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    alFallar.accept(ERROR_INESPERADO);
                }
            }
        }.execute();
    }

    private record Punto(Color color, int tamano) implements Icon {

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x, y, tamano, tamano);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return tamano;
        }

        @Override
        public int getIconHeight() {
            return tamano;
        }
    }
}
