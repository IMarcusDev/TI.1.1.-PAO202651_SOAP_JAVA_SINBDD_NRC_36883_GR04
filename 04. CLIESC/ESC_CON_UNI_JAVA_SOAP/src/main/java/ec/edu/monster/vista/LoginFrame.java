package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * Login window. Navigation to the main window is delegated to the
 * {@code alAbrirMenu} callback supplied by the composition root.
 */
public class LoginFrame extends JFrame implements LoginVista {

    private final Runnable alAbrirMenu;
    private final JTextField campoUsuario = Estilos.entrada(new JTextField());
    private final JPasswordField campoClave = Estilos.entrada(new JPasswordField());
    private final JButton botonIngresar = Estilos.botonPrimario("Ingresar");

    private Runnable accionIngresar = () -> { };
    // Snapshots taken on the EDT so the controller can read them from a worker thread.
    private volatile String usuarioActual;
    private volatile String claveActual;

    public LoginFrame(Runnable alAbrirMenu) {
        super(Estilos.TITULO_VENTANA);
        this.alAbrirMenu = alAbrirMenu;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        setContentPane(construirContenido());
        getRootPane().setDefaultButton(botonIngresar);
        botonIngresar.addActionListener(e -> ingresar());
    }

    private JPanel construirContenido() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilos.FONDO);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Estilos.BLANCO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.insets = new Insets(0, 0, 20, 0);
        tarjeta.add(Estilos.titulo("Iniciar sesión"), c);
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(Estilos.etiqueta("Usuario"), c);
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(campoUsuario, c);
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(Estilos.etiqueta("Contraseña"), c);
        c.insets = new Insets(0, 0, 20, 0);
        tarjeta.add(campoClave, c);

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaBoton.setOpaque(false);
        filaBoton.add(botonIngresar);
        c.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(filaBoton, c);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints ct = new GridBagConstraints();
        ct.ipadx = 160;
        centro.add(tarjeta, ct);

        JPanel estado = Estilos.estadoServicio("Servicio disponible");
        estado.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));

        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(estado, BorderLayout.SOUTH);
        return raiz;
    }

    private void ingresar() {
        usuarioActual = campoUsuario.getText();
        claveActual = new String(campoClave.getPassword());
        Estilos.enSegundoPlano(accionIngresar, this::mostrarError, botonIngresar);
    }

    @Override
    public String getUsuario() {
        return usuarioActual;
    }

    @Override
    public String getClave() {
        return claveActual;
    }

    @Override
    public void mostrarError(String mensaje) {
        Estilos.enEdt(() -> JOptionPane.showOptionDialog(this, mensaje, Estilos.TITULO_VENTANA,
                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
                new Object[] {"Aceptar"}, "Aceptar"));
    }

    @Override
    public void abrirMenu() {
        Estilos.enEdt(() -> {
            claveActual = null;
            alAbrirMenu.run();
            dispose();
        });
    }

    @Override
    public void alIngresar(Runnable accion) {
        this.accionIngresar = accion;
    }
}
