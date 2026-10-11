package ec.edu.monster.controlador;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ec.edu.monster.R;
import ec.edu.monster.modelo.AutenticacionServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.soap.AutenticacionSoapCliente;
import ec.edu.monster.modelo.soap.SoapTransporte;
import ec.edu.monster.vista.LoginVista;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends Activity {

    public static final String EXTRA_TOKEN = "ec.edu.monster.TOKEN";

    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private LoginVista vista;
    private AutenticacionServicio servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vista = new LoginVista(this);
        String urlBase = getString(R.string.url_servidor);
        servicio = new AutenticacionSoapCliente(urlBase + "/LoginWebService", new SoapTransporte());

        vista.alPresionarIngresar(this::iniciarSesion);
    }

    private void iniciarSesion() {
        String usuario = vista.getUsuario();
        String clave = vista.getClave();

        if (usuario.isEmpty() || clave.isEmpty()) {
            vista.mostrarError(getString(R.string.error_campos_vacios));
            return;
        }

        vista.ocultarError();
        vista.mostrarCargando(true);

        ejecutor.execute(() -> {
            try {
                String token = servicio.iniciarSesion(usuario, clave);
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        abrirConvertidor(token);
                    }
                });
            } catch (ServicioException e) {
                runOnUiThread(() -> {
                    if (!isFinishing()) {
                        vista.mostrarCargando(false);
                        vista.mostrarError(e.getMessage());
                    }
                });
            }
        });
    }

    private void abrirConvertidor(String token) {
        Intent intent = new Intent(this, ConvertidorActivity.class);
        intent.putExtra(EXTRA_TOKEN, token);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        ejecutor.shutdownNow();
        super.onDestroy();
    }
}