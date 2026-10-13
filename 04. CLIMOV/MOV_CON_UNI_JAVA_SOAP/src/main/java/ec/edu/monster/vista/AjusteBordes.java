package ec.edu.monster.vista;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import ec.edu.monster.R;

final class AjusteBordes {

    private AjusteBordes() { }

    static void aplicar(Activity actividad) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return;
        }
        View barra = actividad.findViewById(R.id.barraSuperior);
        View contenido = actividad.findViewById(R.id.contenido);
        View raiz = (View) barra.getParent();
        int arribaBase = barra.getPaddingTop();
        int abajoBase = contenido.getPaddingBottom();

        raiz.setOnApplyWindowInsetsListener((vista, bordes) -> {
            Insets barras = bordes.getInsets(WindowInsets.Type.systemBars());
            Insets teclado = bordes.getInsets(WindowInsets.Type.ime());
            barra.setPadding(barra.getPaddingLeft(), arribaBase + barras.top,
                    barra.getPaddingRight(), barra.getPaddingBottom());
            contenido.setPadding(contenido.getPaddingLeft(), contenido.getPaddingTop(),
                    contenido.getPaddingRight(), abajoBase + Math.max(barras.bottom, teclado.bottom));
            return WindowInsets.CONSUMED;
        });
    }
}
