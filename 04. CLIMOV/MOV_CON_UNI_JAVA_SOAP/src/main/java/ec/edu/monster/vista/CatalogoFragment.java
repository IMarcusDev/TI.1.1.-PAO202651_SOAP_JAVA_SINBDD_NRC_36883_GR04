package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import ec.edu.monster.AplicacionMonster;
import ec.edu.monster.R;
import ec.edu.monster.controlador.CatalogoControlador;
import ec.edu.monster.controlador.ControladorConSesion;
import ec.edu.monster.modelos.data.CatalogoCompletoResultado;
import ec.edu.monster.modelos.data.GrupoUnidades;

public class CatalogoFragment extends PestanaSesionFragment {

    private CatalogoControlador controlador;

    private boolean ocupado;
    private boolean cargado;
    private BottomSheetDialog hoja;

    private SwipeRefreshLayout refresco;
    private RecyclerView listaMagnitudes;
    private CatalogoAdaptador adaptador;
    private View panelVacio;
    private View panelCargando;
    private View panelError;
    private TextView lblError;
    private MaterialButton btnReintentar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controlador = AplicacionMonster.de(requireContext()).crearCatalogoControlador();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, @Nullable ViewGroup contenedor,
                             @Nullable Bundle savedInstanceState) {
        return inflador.inflate(R.layout.fragment_catalogo, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(vista, savedInstanceState);
        configurarBarra(vista.findViewById(R.id.barraSuperior), R.string.catalogo_titulo);

        refresco = vista.findViewById(R.id.refresco);
        listaMagnitudes = vista.findViewById(R.id.listaMagnitudes);
        panelVacio = vista.findViewById(R.id.panelVacio);
        panelCargando = vista.findViewById(R.id.panelCargando);
        panelError = vista.findViewById(R.id.panelError);
        lblError = vista.findViewById(R.id.lblError);
        btnReintentar = vista.findViewById(R.id.btnReintentar);

        adaptador = new CatalogoAdaptador(this::alTocarMagnitud);
        listaMagnitudes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        listaMagnitudes.setAdapter(adaptador);

        refresco.setColorSchemeColors(requireContext().getColor(R.color.monster_primary));
        refresco.setOnChildScrollUpCallback((padre, hijo) -> listaMagnitudes.canScrollVertically(-1));
        refresco.setOnRefreshListener(() -> {
            try {
                cargar(true);
            } catch (RuntimeException e) {
                mostrarErrorInesperado();
            }
        });
        btnReintentar.setOnClickListener(v -> {
            try {
                cargar(false);
            } catch (RuntimeException e) {
                mostrarErrorInesperado();
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (hoja != null) {
            hoja.dismiss();
            hoja = null;
        }
        super.onDestroyView();
    }

    @Override
    protected ControladorConSesion controlador() {
        return controlador;
    }

    @Override
    protected void alMostrar() {
        if (!cargado) {
            cargar(false);
        }
    }

    @Override
    protected void mostrarErrorInesperado() {
        if (vistaActiva()) {
            mostrarError(getString(R.string.error_inesperado));
        }
    }

    private void cargar(boolean desdeGesto) {
        if (ocupado) {
            refresco.setRefreshing(false);
            return;
        }

        ocupado = true;
        panelError.setVisibility(View.GONE);
        btnReintentar.setEnabled(false);
        mostrarCargando(!desdeGesto);

        tareas().ejecutar(controlador::obtenerCatalogo,
                this::alCargarCatalogo,
                error -> {
                    if (vistaActiva()) {
                        terminarCarga();
                        mostrarErrorInesperado();
                    }
                });
    }

    private void alCargarCatalogo(CatalogoCompletoResultado resultado) {
        if (!vistaActiva()) {
            return;
        }
        try {
            if (resultado.isSesionExpirada()) {
                volverAlLogin(resultado.getMensaje());
                return;
            }

            if (resultado.isExito()) {
                adaptador.mostrar(resultado.getGrupos());
                cargado = true;
            } else {
                mostrarError(resultado.getMensaje());
            }
        } catch (RuntimeException e) {
            mostrarErrorInesperado();
        } finally {
            terminarCarga();
        }
    }

    private void terminarCarga() {
        ocupado = false;
        refresco.setRefreshing(false);
        btnReintentar.setEnabled(true);
        mostrarCargando(false);
    }

    private void alTocarMagnitud(GrupoUnidades grupo) {
        try {
            if (hoja != null && hoja.isShowing()) {
                return;
            }
            hoja = HojaUnidades.mostrar(requireContext(), grupo);
        } catch (RuntimeException e) {
            mostrarErrorInesperado();
        }
    }

    private void mostrarCargando(boolean cargando) {
        panelCargando.setVisibility(cargando ? View.VISIBLE : View.GONE);
        actualizarVacio();
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        panelError.setVisibility(View.VISIBLE);
        actualizarVacio();
    }

    private void actualizarVacio() {
        boolean vacio = adaptador.getItemCount() == 0
                && panelCargando.getVisibility() != View.VISIBLE
                && panelError.getVisibility() != View.VISIBLE;
        panelVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
    }
}
