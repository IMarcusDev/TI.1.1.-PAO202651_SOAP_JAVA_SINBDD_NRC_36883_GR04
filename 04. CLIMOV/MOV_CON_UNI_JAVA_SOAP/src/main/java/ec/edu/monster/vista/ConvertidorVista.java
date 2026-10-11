package ec.edu.monster.vista;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import ec.edu.monster.R;
import ec.edu.monster.modelo.Magnitud;
import java.util.List;
import java.util.function.Consumer;

public class ConvertidorVista {

    private final Activity actividad;
    private final Spinner spnMagnitud;
    private final Spinner spnOrigen;
    private final Spinner spnDestino;
    private final EditText txtValor;
    private final Button btnConvertir;
    private final Button btnCerrarSesion;
    private final ProgressBar prgCargando;
    private final TextView lblResultado;
    private final TextView lblError;

    public ConvertidorVista(Activity actividad) {
        this.actividad = actividad;
        spnMagnitud = actividad.findViewById(R.id.spnMagnitud);
        spnOrigen = actividad.findViewById(R.id.spnOrigen);
        spnDestino = actividad.findViewById(R.id.spnDestino);
        txtValor = actividad.findViewById(R.id.txtValor);
        btnConvertir = actividad.findViewById(R.id.btnConvertir);
        btnCerrarSesion = actividad.findViewById(R.id.btnCerrarSesion);
        prgCargando = actividad.findViewById(R.id.prgCargando);
        lblResultado = actividad.findViewById(R.id.lblResultado);
        lblError = actividad.findViewById(R.id.lblError);
    }

    // ----- Datos que la vista muestra -----

    public void mostrarMagnitudes(List<Magnitud> magnitudes) {
        spnMagnitud.setAdapter(crearAdaptador(magnitudes));
    }

    public void mostrarUnidades(List<String> unidades) {
        spnOrigen.setAdapter(crearAdaptador(unidades));
        spnDestino.setAdapter(crearAdaptador(unidades));
        if (unidades.size() > 1) {
            spnDestino.setSelection(1);
        }
    }

    private <T> ArrayAdapter<T> crearAdaptador(List<T> elementos) {
        ArrayAdapter<T> adaptador =
                new ArrayAdapter<>(actividad, android.R.layout.simple_spinner_item, elementos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adaptador;
    }

    // ----- Datos que el usuario ingresa -----

    public Magnitud getMagnitud() {
        return (Magnitud) spnMagnitud.getSelectedItem();
    }

    public String getUnidadOrigen() {
        Object seleccion = spnOrigen.getSelectedItem();
        return seleccion != null ? seleccion.toString() : null;
    }

    public String getUnidadDestino() {
        Object seleccion = spnDestino.getSelectedItem();
        return seleccion != null ? seleccion.toString() : null;
    }

    public String getValor() {
        return txtValor.getText().toString().trim();
    }

    // ----- Eventos -----

    public void alCambiarMagnitud(Consumer<Magnitud> accion) {
        spnMagnitud.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> padre, View vista, int posicion, long id) {
                accion.accept((Magnitud) padre.getItemAtPosition(posicion));
            }

            @Override
            public void onNothingSelected(AdapterView<?> padre) { }
        });
    }

    public void alPresionarConvertir(Runnable accion) {
        btnConvertir.setOnClickListener(v -> accion.run());
    }

    public void alPresionarCerrarSesion(Runnable accion) {
        btnCerrarSesion.setOnClickListener(v -> accion.run());
    }

    // ----- Estados de la pantalla -----

    public void mostrarCargando(boolean cargando) {
        prgCargando.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnConvertir.setEnabled(!cargando);
        spnMagnitud.setEnabled(!cargando);
        spnOrigen.setEnabled(!cargando);
        spnDestino.setEnabled(!cargando);
    }

    public void mostrarResultado(String texto) {
        lblResultado.setText(texto);
        lblResultado.setVisibility(View.VISIBLE);
    }

    public void ocultarResultado() {
        lblResultado.setVisibility(View.GONE);
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisibility(View.VISIBLE);
    }

    public void ocultarError() {
        lblError.setVisibility(View.GONE);
    }
}