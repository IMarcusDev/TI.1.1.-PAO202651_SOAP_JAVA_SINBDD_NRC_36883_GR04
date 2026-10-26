package ec.edu.monster.vista;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import ec.edu.monster.AplicacionMonster;
import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorConSesion;
import ec.edu.monster.controlador.ConversionControlador;
import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.utils.FormatoNumero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ConversionFragment extends PestanaSesionFragment {

    private ConversionControlador controlador;

    private boolean ocupado;
    private boolean filtrando;
    private int solicitudUnidades;

    private ChipGroup grupoMagnitudes;
    private final List<Chip> chips = new ArrayList<>();
    private TextView lblMagnitudActual;
    private EditText txtValor;
    private Spinner spnOrigen;
    private Spinner spnDestino;
    private ImageButton btnInvertir;
    private TextView lblResultado;
    private TextView lblDetalle;
    private TextView lblError;
    private MaterialButton btnReintentarUnidades;
    private MaterialButton btnConvertir;
    private CircularProgressIndicator prgCargando;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controlador = AplicacionMonster.de(requireContext()).crearConversionControlador();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, @Nullable ViewGroup contenedor,
                             @Nullable Bundle savedInstanceState) {
        return inflador.inflate(R.layout.fragment_conversion, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);
        configurarBarra(vista.findViewById(R.id.barraSuperior), R.string.app_titulo);

        grupoMagnitudes = vista.findViewById(R.id.grupoMagnitudes);
        lblMagnitudActual = vista.findViewById(R.id.lblMagnitudActual);
        txtValor = vista.findViewById(R.id.txtValor);
        spnOrigen = vista.findViewById(R.id.spnOrigen);
        spnDestino = vista.findViewById(R.id.spnDestino);
        btnInvertir = vista.findViewById(R.id.btnInvertir);
        lblResultado = vista.findViewById(R.id.lblResultado);
        lblDetalle = vista.findViewById(R.id.lblDetalle);
        lblError = vista.findViewById(R.id.lblError);
        btnReintentarUnidades = vista.findViewById(R.id.btnReintentarUnidades);
        btnConvertir = vista.findViewById(R.id.btnConvertir);
        prgCargando = vista.findViewById(R.id.prgCargando);

        crearChips(LayoutInflater.from(vista.getContext()));
        mostrarMagnitudActual(null);
        limpiarUnidades();

        grupoMagnitudes.setOnCheckedStateChangeListener((grupo, ids) -> alCambiarMagnitud());
        txtValor.addTextChangedListener(new FiltroValor());
        txtValor.setOnEditorActionListener((v, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_DONE || esEnter(evento)) {
                alConvertir();
                return true;
            }
            return false;
        });
        AdapterView.OnItemSelectedListener alCambiarUnidad = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> padre, View elemento, int posicion, long id) {
                String actual = unidadDe((Spinner) padre);
                if (Objects.equals(actual, padre.getTag())) {
                    return;
                }
                padre.setTag(actual);
                ocultarError();
                limpiarResultado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> padre) { }
        };
        spnOrigen.setOnItemSelectedListener(alCambiarUnidad);
        spnDestino.setOnItemSelectedListener(alCambiarUnidad);
        btnInvertir.setOnClickListener(v -> alInvertir());
        btnReintentarUnidades.setOnClickListener(v -> alReintentarUnidades());
        btnConvertir.setOnClickListener(v -> alConvertir());
    }

    private void crearChips(LayoutInflater inflador) {
        chips.clear();
        for (Magnitud magnitud : controlador.getMagnitudes()) {
            Chip chip = (Chip) inflador.inflate(R.layout.item_chip_magnitud, grupoMagnitudes, false);
            chip.setId(View.generateViewId());
            chip.setText(magnitud.getNombre());
            chip.setTag(magnitud);
            grupoMagnitudes.addView(chip);
            chips.add(chip);
        }
    }

    @Override
    protected ControladorConSesion controlador() {
        return controlador;
    }

    @Override
    protected void alMostrar() {
        if (grupoMagnitudes.getCheckedChipId() == View.NO_ID && !chips.isEmpty()) {
            chips.get(0).setChecked(true);
        }
    }

    @Override
    protected void mostrarErrorInesperado() {
        if (vistaActiva()) {
            mostrarError(getString(R.string.error_inesperado));
        }
    }

    private Magnitud magnitudSeleccionada() {
        Chip chip = grupoMagnitudes.findViewById(grupoMagnitudes.getCheckedChipId());
        return chip != null ? (Magnitud) chip.getTag() : null;
    }

    private void alCambiarMagnitud() {
        try {
            Magnitud magnitud = magnitudSeleccionada();
            mostrarMagnitudActual(magnitud);

            ocultarError();
            limpiarUnidades();
            limpiarResultado();
            cargarUnidades(magnitud);
        } catch (RuntimeException e) {
            mostrarErrorInesperado();
        }
    }

    private void alReintentarUnidades() {
        try {
            ocultarError();
            cargarUnidades(magnitudSeleccionada());
        } catch (RuntimeException e) {
            mostrarErrorInesperado();
        }
    }

    private void cargarUnidades(Magnitud magnitud) {
        if (magnitud == null) {
            return;
        }

        int solicitud = ++solicitudUnidades;
        establecerOcupado(true);
        lblDetalle.setText(R.string.conv_cargando_unidades);

        tareas().ejecutar(() -> controlador.obtenerUnidades(magnitud),
                resultado -> alCargarUnidades(solicitud, resultado),
                error -> {
                    if (vistaActiva() && solicitud == solicitudUnidades) {
                        establecerOcupado(false);
                        mostrarErrorInesperado();
                    }
                });
    }

    private void alCargarUnidades(int solicitud, CatalogoResultado resultado) {
        if (!vistaActiva() || solicitud != solicitudUnidades) {
            return;
        }

        try {
            if (resultado.isSesionExpirada()) {
                volverAlLogin(resultado.getMensaje());
                return;
            }

            if (!resultado.isExito()) {
                mostrarError(resultado.getMensaje());
                btnReintentarUnidades.setVisibility(View.VISIBLE);
                lblDetalle.setText(R.string.conv_unidades_fallaron);
                return;
            }

            mostrarUnidades(resultado.getUnidades());
            lblDetalle.setText(R.string.conv_instruccion);
        } catch (RuntimeException e) {
            mostrarErrorInesperado();
        } finally {
            establecerOcupado(false);
        }
    }

    private void mostrarUnidades(List<String> unidades) {
        Context contexto = requireContext();
        spnOrigen.setAdapter(new UnidadesAdaptador(contexto, unidades,
                contexto.getColor(R.color.texto_oscuro), contexto.getColor(R.color.borde_campo)));
        spnDestino.setAdapter(new UnidadesAdaptador(contexto, unidades,
                contexto.getColor(R.color.blanco), contexto.getColor(R.color.texto_sobre_primario)));
        spnOrigen.setTag(null);
        spnDestino.setTag(null);
    }

    private void limpiarUnidades() {
        mostrarUnidades(Collections.emptyList());
    }

    private static String unidadDe(Spinner selector) {
        UnidadesAdaptador adaptador = (UnidadesAdaptador) selector.getAdapter();
        return adaptador != null ? adaptador.unidadEn(selector.getSelectedItemPosition()) : null;
    }

    private void alInvertir() {
        if (ocupado) {
            return;
        }
        int origen = spnOrigen.getSelectedItemPosition();
        spnOrigen.setSelection(spnDestino.getSelectedItemPosition());
        spnDestino.setSelection(origen);
    }

    private class FiltroValor implements TextWatcher {
        private String anterior = "";

        @Override
        public void beforeTextChanged(CharSequence texto, int inicio, int cantidad, int despues) {
            if (!filtrando) {
                anterior = texto.toString();
            }
        }

        @Override
        public void onTextChanged(CharSequence texto, int inicio, int antes, int cantidad) { }

        @Override
        public void afterTextChanged(Editable texto) {
            if (filtrando) {
                return;
            }
            try {
                String original = texto.toString();
                String filtrado = controlador.filtrarValor(original, magnitudSeleccionada());

                if (!filtrado.equals(original)) {
                    filtrando = true;
                    txtValor.setText(filtrado);
                    txtValor.setSelection(filtrado.length());
                }

                if (!filtrado.equals(anterior)) {
                    limpiarResultado();
                }
            } catch (RuntimeException e) {
            } finally {
                filtrando = false;
            }
        }
    }

    private void alConvertir() {
        if (ocupado) {
            return;
        }

        try {
            ocultarTeclado();
            ocultarError();

            Magnitud magnitud = magnitudSeleccionada();
            String valor = txtValor.getText().toString();
            String origen = unidadDe(spnOrigen);
            String destino = unidadDe(spnDestino);
            establecerOcupado(true);

            tareas().ejecutar(() -> controlador.convertir(magnitud, valor, origen, destino),
                    this::alTerminarConversion,
                    error -> {
                        if (vistaActiva()) {
                            limpiarResultado();
                            mostrarErrorInesperado();
                            establecerOcupado(false);
                        }
                    });
        } catch (RuntimeException e) {
            limpiarResultado();
            mostrarErrorInesperado();
            establecerOcupado(false);
        }
    }

    private void alTerminarConversion(ConversionResultado resultado) {
        if (!vistaActiva()) {
            return;
        }
        try {
            if (resultado.isSesionExpirada()) {
                volverAlLogin(resultado.getMensaje());
                return;
            }

            if (resultado.isExito()) {
                mostrarResultado(resultado);
            } else {
                limpiarResultado();
                mostrarError(resultado.getMensaje());
            }
        } catch (RuntimeException e) {
            limpiarResultado();
            mostrarErrorInesperado();
        } finally {
            establecerOcupado(false);
        }
    }

    private void mostrarResultado(ConversionResultado resultado) {
        String texto = FormatoNumero.formatear(resultado.getValor());
        lblResultado.setText(texto);
        lblResultado.setTextSize(TypedValue.COMPLEX_UNIT_SP, texto.length() > 12 ? 30 : 44);

        Conversion conversion = resultado.getConversion();
        if (conversion != null) {
            String detalle = getString(R.string.conv_detalle,
                    FormatoNumero.formatear(conversion.getValor()), conversion.getUnidadOrigen(),
                    texto, conversion.getUnidadDestino());
            if (conversion.getUnidadOrigen().equals(conversion.getUnidadDestino())) {
                detalle += "\n" + resultado.getMensaje();
            }
            lblDetalle.setText(detalle);
        }
    }

    private void limpiarResultado() {
        lblResultado.setText(R.string.conv_sin_resultado);
        lblResultado.setTextSize(TypedValue.COMPLEX_UNIT_SP, 44);
        lblDetalle.setText(R.string.conv_instruccion);
    }

    private void mostrarMagnitudActual(Magnitud magnitud) {
        SpannableStringBuilder texto = new SpannableStringBuilder(getString(R.string.conv_convirtiendo));
        int inicio = texto.length();
        texto.append(magnitud != null ? magnitud.getNombre() : "—");
        texto.setSpan(new StyleSpan(Typeface.BOLD), inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        texto.setSpan(new ForegroundColorSpan(requireContext().getColor(R.color.monster_primary)),
                inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lblMagnitudActual.setText(texto);
    }

    private void establecerOcupado(boolean estaOcupado) {
        ocupado = estaOcupado;
        btnConvertir.setEnabled(!estaOcupado);
        btnInvertir.setEnabled(!estaOcupado);
        btnReintentarUnidades.setEnabled(!estaOcupado);
        for (Chip chip : chips) {
            chip.setClickable(!estaOcupado);
        }
        txtValor.setEnabled(!estaOcupado);
        spnOrigen.setEnabled(!estaOcupado);
        spnDestino.setEnabled(!estaOcupado);
        prgCargando.setVisibility(estaOcupado ? View.VISIBLE : View.GONE);
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisibility(View.VISIBLE);
    }

    private void ocultarError() {
        lblError.setVisibility(View.GONE);
        btnReintentarUnidades.setVisibility(View.GONE);
    }

    private void ocultarTeclado() {
        txtValor.clearFocus();
        InputMethodManager teclado = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (teclado != null) {
            teclado.hideSoftInputFromWindow(txtValor.getWindowToken(), 0);
        }
    }

    private static boolean esEnter(KeyEvent evento) {
        return evento != null && evento.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && evento.getAction() == KeyEvent.ACTION_DOWN;
    }
}
