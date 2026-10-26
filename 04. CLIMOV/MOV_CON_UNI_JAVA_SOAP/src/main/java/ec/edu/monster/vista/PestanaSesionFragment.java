package ec.edu.monster.vista;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import ec.edu.monster.AplicacionMonster;
import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorConSesion;
import ec.edu.monster.servicios.EjecutorTareas;

public abstract class PestanaSesionFragment extends Fragment {

    protected abstract ControladorConSesion controlador();

    protected abstract void alMostrar();

    protected abstract void mostrarErrorInesperado();

    protected EjecutorTareas tareas() {
        return AplicacionMonster.de(requireContext()).getTareas();
    }

    protected void configurarBarra(MaterialToolbar barra, @StringRes int titulo) {
        barra.setTitle(titulo);
        barra.inflateMenu(R.menu.menu_sesion);
        barra.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.accion_cerrar_sesion) {
                confirmarCierreSesion();
                return true;
            }
            return false;
        });
        AjusteBordes.barraSuperior(barra);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            verificarSesionYMostrar();
        }
    }

    @Override
    public void onHiddenChanged(boolean oculto) {
        super.onHiddenChanged(oculto);
        if (!oculto && isResumed()) {
            verificarSesionYMostrar();
        }
    }

    private void verificarSesionYMostrar() {
        try {
            if (!controlador().haySesion()) {
                volverAlLogin(ControladorConSesion.MENSAJE_SESION_EXPIRADA);
                return;
            }
            alMostrar();
        } catch (RuntimeException e) {
            mostrarErrorInesperado();
        }
    }

    private void confirmarCierreSesion() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.cerrar_sesion)
                .setMessage(R.string.cerrar_sesion_pregunta)
                .setPositiveButton(R.string.si, (dialogo, boton) -> {
                    controlador().cerrarSesion();
                    volverAlLogin(null);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    protected void volverAlLogin(String aviso) {
        NavegacionSesion.volverAlLogin(getActivity(), aviso);
    }

    protected boolean vistaActiva() {
        return isAdded() && getView() != null;
    }
}
