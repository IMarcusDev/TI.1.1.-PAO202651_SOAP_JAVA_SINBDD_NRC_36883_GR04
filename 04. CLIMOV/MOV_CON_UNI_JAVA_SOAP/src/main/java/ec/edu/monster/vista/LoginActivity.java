package ec.edu.monster.vista;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import ec.edu.monster.AplicacionMonster;
import ec.edu.monster.R;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.servicios.EjecutorTareas;

public class LoginActivity extends AppCompatActivity {

    private LoginControlador controlador;
    private EjecutorTareas tareas;

    private EditText txtUsuario;
    private EditText txtClave;
    private MaterialButton btnIngresar;
    private CircularProgressIndicator prgCargando;
    private TextView lblError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        setContentView(R.layout.activity_login);

        AplicacionMonster app = AplicacionMonster.de(this);
        controlador = app.crearLoginControlador();
        tareas = app.getTareas();

        txtUsuario = findViewById(R.id.txtUsuario);
        txtClave = findViewById(R.id.txtClave);
        btnIngresar = findViewById(R.id.btnIngresar);
        prgCargando = findViewById(R.id.prgCargando);
        lblError = findViewById(R.id.lblError);

        txtUsuario.setFilters(new InputFilter[] { new InputFilter.LengthFilter(LoginControlador.MAX_USUARIO) });
        txtClave.setFilters(new InputFilter[] { new InputFilter.LengthFilter(LoginControlador.MAX_CLAVE) });

        txtUsuario.setOnEditorActionListener((v, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_NEXT || esEnter(evento)) {
                txtClave.requestFocus();
                return true;
            }
            return false;
        });
        txtClave.setOnEditorActionListener((v, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_GO || esEnter(evento)) {
                alIngresar();
                return true;
            }
            return false;
        });
        btnIngresar.setOnClickListener(v -> alIngresar());

        AjusteBordes.barraSuperior(findViewById(R.id.barraSuperior));
        AjusteBordes.contenidoInferior(findViewById(R.id.contenido));
    }

    private static boolean esEnter(KeyEvent evento) {
        return evento != null && evento.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && evento.getAction() == KeyEvent.ACTION_DOWN;
    }

    private void alIngresar() {
        if (!btnIngresar.isEnabled()) {
            return;
        }

        String usuario = txtUsuario.getText().toString();
        String clave = txtClave.getText().toString();
        mostrarCargando(true);
        lblError.setVisibility(View.GONE);

        tareas.ejecutar(() -> controlador.iniciarSesion(usuario, clave),
                this::alTerminarLogin,
                error -> {
                    if (sigueAbierta()) {
                        mostrarCargando(false);
                        mostrarError(getString(R.string.error_inesperado));
                    }
                });
    }

    private void alTerminarLogin(LoginResultado resultado) {
        if (!sigueAbierta()) {
            return;
        }
        mostrarCargando(false);
        if (resultado.isExito()) {
            txtClave.setText("");
            startActivity(new Intent(this, PrincipalActivity.class));
            finish();
        } else {
            mostrarError(resultado.getMensaje());
        }
    }

    private boolean sigueAbierta() {
        return !isFinishing() && !isDestroyed();
    }

    private void mostrarCargando(boolean cargando) {
        btnIngresar.setEnabled(!cargando);
        txtUsuario.setEnabled(!cargando);
        txtClave.setEnabled(!cargando);
        prgCargando.setVisibility(cargando ? View.VISIBLE : View.GONE);
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisibility(View.VISIBLE);
    }
}
