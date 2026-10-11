package ec.edu.monster.vista;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import ec.edu.monster.R;

public class LoginVista {

    private final EditText txtUsuario;
    private final EditText txtClave;
    private final Button btnIngresar;
    private final ProgressBar prgCargando;
    private final TextView lblError;

    public LoginVista(Activity actividad) {
        txtUsuario = actividad.findViewById(R.id.txtUsuario);
        txtClave = actividad.findViewById(R.id.txtClave);
        btnIngresar = actividad.findViewById(R.id.btnIngresar);
        prgCargando = actividad.findViewById(R.id.prgCargando);
        lblError = actividad.findViewById(R.id.lblError);
    }

    public String getUsuario() {
        return txtUsuario.getText().toString().trim();
    }

    public String getClave() {
        return txtClave.getText().toString();
    }

    public void alPresionarIngresar(Runnable accion) {
        btnIngresar.setOnClickListener(v -> accion.run());
    }

    public void mostrarCargando(boolean cargando) {
        prgCargando.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnIngresar.setEnabled(!cargando);
        txtUsuario.setEnabled(!cargando);
        txtClave.setEnabled(!cargando);
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisibility(View.VISIBLE);
    }

    public void ocultarError() {
        lblError.setVisibility(View.GONE);
    }
}