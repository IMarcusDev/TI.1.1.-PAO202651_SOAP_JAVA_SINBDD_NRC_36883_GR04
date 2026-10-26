package ec.edu.monster.vista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import ec.edu.monster.R;
import ec.edu.monster.modelos.data.GrupoUnidades;
import ec.edu.monster.modelos.data.UnidadItem;

final class HojaUnidades {

    private static final int ALTO_FILA_DP = 60;

    private HojaUnidades() { }

    static BottomSheetDialog mostrar(Context contexto, GrupoUnidades grupo) {
        BottomSheetDialog hoja = new BottomSheetDialog(contexto);
        LayoutInflater inflador = LayoutInflater.from(contexto);
        View vista = inflador.inflate(R.layout.hoja_unidades, null, false);

        ((ImageView) vista.findViewById(R.id.hojaIcono)).setImageResource(grupo.getIcono());
        ((TextView) vista.findViewById(R.id.hojaTitulo)).setText(grupo.getNombre());
        ((TextView) vista.findViewById(R.id.hojaDescripcion)).setText(grupo.getDescripcion());
        ((TextView) vista.findViewById(R.id.hojaCantidad))
                .setText(contexto.getString(R.string.cat_cantidad_disponibles, grupo.getCantidad()));

        LinearLayout lista = vista.findViewById(R.id.hojaUnidades);
        for (UnidadItem unidad : grupo.getUnidades()) {
            View fila = inflador.inflate(R.layout.item_unidad_hoja, lista, false);
            ((TextView) fila.findViewById(R.id.lblCodigo)).setText(unidad.getCodigo());
            ((TextView) fila.findViewById(R.id.lblNombreUnidad)).setText(unidad.getNombre());
            lista.addView(fila);
        }

        float densidad = contexto.getResources().getDisplayMetrics().density;
        int altoFilas = Math.round(grupo.getCantidad() * ALTO_FILA_DP * densidad);
        int mitadPantalla = contexto.getResources().getDisplayMetrics().heightPixels / 2;
        View desplazable = vista.findViewById(R.id.hojaDesplazable);
        ViewGroup.LayoutParams medidas = desplazable.getLayoutParams();
        medidas.height = Math.min(altoFilas, mitadPantalla);
        desplazable.setLayoutParams(medidas);

        vista.findViewById(R.id.btnCerrarHoja).setOnClickListener(v -> hoja.dismiss());

        hoja.setContentView(vista);
        hoja.setDismissWithAnimation(true);
        BottomSheetBehavior<?> comportamiento = hoja.getBehavior();
        comportamiento.setSkipCollapsed(true);
        comportamiento.setState(BottomSheetBehavior.STATE_EXPANDED);
        hoja.show();
        return hoja;
    }
}
