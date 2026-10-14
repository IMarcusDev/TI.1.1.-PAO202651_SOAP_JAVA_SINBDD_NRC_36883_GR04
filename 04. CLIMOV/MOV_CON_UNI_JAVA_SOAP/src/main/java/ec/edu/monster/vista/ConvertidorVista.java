package ec.edu.monster.vista;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import ec.edu.monster.R;
import ec.edu.monster.modelos.enums.Magnitud;
import java.util.List;
import java.util.function.Consumer;

public class ConvertidorVista {

    private final Activity actividad;
    private final RadioGroup grpMagnitud;
    private final Spinner spnOrigen;
    private final Spinner spnDestino;
    private final EditText txtValor;
    private final ImageButton btnIntercambiar;
    private final Button btnConvertir;
    private final Button btnCerrarSesion;
    private final ProgressBar prgCargando;
    private final TextView lblResultado;
    private final TextView lblDetalle;
    private final TextView lblError;

    public ConvertidorVista(Activity actividad) {
        this.actividad = actividad;
        grpMagnitud = actividad.findViewById(R.id.grpMagnitud);
        spnOrigen = actividad.findViewById(R.id.spnOrigen);
        spnDestino = actividad.findViewById(R.id.spnDestino);
        txtValor = actividad.findViewById(R.id.txtValor);
        btnIntercambiar = actividad.findViewById(R.id.btnIntercambiar);
        btnConvertir = actividad.findViewById(R.id.btnConvertir);
        btnCerrarSesion = actividad.findViewById(R.id.btnCerrarSesion);
        prgCargando = actividad.findViewById(R.id.prgCargando);
        lblResultado = actividad.findViewById(R.id.lblResultado);
        lblDetalle = actividad.findViewById(R.id.lblDetalle);
        lblError = actividad.findViewById(R.id.lblError);

        btnIntercambiar.setOnClickListener(v -> intercambiarUnidades());
        AjusteBordes.aplicar(actividad);
    }

    public void mostrarMagnitudes(List<Magnitud> magnitudes) {
        grpMagnitud.removeAllViews();
        LayoutInflater inflador = LayoutInflater.from(actividad);
        for (Magnitud magnitud : magnitudes) {
            RadioButton chip = (RadioButton) inflador.inflate(R.layout.item_magnitud, grpMagnitud, false);
            chip.setId(View.generateViewId());
            chip.setTag(magnitud);
            chip.setText(nombreDe(magnitud));
            grpMagnitud.addView(chip);
        }
        if (grpMagnitud.getChildCount() > 0) {
            grpMagnitud.check(grpMagnitud.getChildAt(0).getId());
        }
    }

    public void mostrarUnidades(List<String> unidades) {
        spnOrigen.setAdapter(crearAdaptador(unidades, R.layout.item_unidad));
        spnDestino.setAdapter(crearAdaptador(unidades, R.layout.item_unidad_resultado));
        if (unidades.size() > 1) {
            spnDestino.setSelection(1);
        }
    }

    private ArrayAdapter<String> crearAdaptador(List<String> elementos, int diseno) {
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(actividad, diseno, elementos);
        adaptador.setDropDownViewResource(R.layout.item_unidad_desplegable);
        return adaptador;
    }

    private String nombreDe(Magnitud magnitud) {
        switch (magnitud) {
            case AREA:        return actividad.getString(R.string.mag_area);
            case LONGITUD:    return actividad.getString(R.string.mag_longitud);
            case MASA:        return actividad.getString(R.string.mag_masa);
            case TEMPERATURA: return actividad.getString(R.string.mag_temperatura);
            case TIEMPO:      return actividad.getString(R.string.mag_tiempo);
            case VOLUMEN:     return actividad.getString(R.string.mag_volumen);
            default:          return magnitud.toString();
        }
    }

    public Magnitud getMagnitud() {
        View seleccion = grpMagnitud.findViewById(grpMagnitud.getCheckedRadioButtonId());
        return seleccion != null ? (Magnitud) seleccion.getTag() : null;
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

    public void alCambiarMagnitud(Consumer<Magnitud> accion) {
        grpMagnitud.setOnCheckedChangeListener((grupo, idMarcado) -> {
            View chip = grupo.findViewById(idMarcado);
            if (chip != null) {
                accion.accept((Magnitud) chip.getTag());
            }
        });
    }

    public void alPresionarConvertir(Runnable accion) {
        btnConvertir.setOnClickListener(v -> accion.run());
        txtValor.setOnEditorActionListener((v, idAccion, evento) -> {
            if (idAccion == EditorInfo.IME_ACTION_DONE && btnConvertir.isEnabled()) {
                accion.run();
                return true;
            }
            return false;
        });
    }

    public void alPresionarCerrarSesion(Runnable accion) {
        btnCerrarSesion.setOnClickListener(v -> accion.run());
    }

    private void intercambiarUnidades() {
        if (spnOrigen.getCount() == 0) {
            return;
        }
        int origen = spnOrigen.getSelectedItemPosition();
        spnOrigen.setSelection(spnDestino.getSelectedItemPosition());
        spnDestino.setSelection(origen);
        btnIntercambiar.animate().rotationBy(180f).setDuration(220).start();
        ocultarResultado();
    }

    public void mostrarCargando(boolean cargando) {
        prgCargando.setVisibility(cargando ? View.VISIBLE : View.INVISIBLE);
        btnConvertir.setEnabled(!cargando);
        btnIntercambiar.setEnabled(!cargando);
        spnOrigen.setEnabled(!cargando);
        spnDestino.setEnabled(!cargando);
        for (int i = 0; i < grpMagnitud.getChildCount(); i++) {
            grpMagnitud.getChildAt(i).setEnabled(!cargando);
        }
    }

    public void mostrarResultado(String valor, String detalle) {
        lblResultado.setText(valor);
        lblDetalle.setText(detalle);
        lblResultado.setAlpha(0f);
        lblResultado.setTranslationX(-lblResultado.getWidth() * 0.15f);
        lblResultado.animate().alpha(1f).translationX(0f).setDuration(280).start();
    }

    public void ocultarResultado() {
        lblResultado.animate().cancel();
        lblResultado.setAlpha(1f);
        lblResultado.setTranslationX(0f);
        lblResultado.setText(R.string.conv_sin_resultado);
        lblDetalle.setText(R.string.conv_instruccion);
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisibility(View.VISIBLE);
    }

    public void ocultarError() {
        lblError.setVisibility(View.GONE);
    }
}
