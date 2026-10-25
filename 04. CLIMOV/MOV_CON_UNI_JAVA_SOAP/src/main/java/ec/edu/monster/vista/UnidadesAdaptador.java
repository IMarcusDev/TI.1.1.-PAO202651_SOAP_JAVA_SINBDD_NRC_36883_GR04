package ec.edu.monster.vista;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import ec.edu.monster.R;
import java.util.ArrayList;
import java.util.List;

class UnidadesAdaptador extends ArrayAdapter<String> {

    private final int colorTexto;
    private final int colorPista;
    private final int colorDesplegable;
    private final int colorPistaDesplegable;

    UnidadesAdaptador(Context contexto, List<String> unidades, int colorTexto, int colorPista) {
        super(contexto, R.layout.item_unidad_seleccion, conPista(contexto, unidades));
        setDropDownViewResource(R.layout.item_unidad_desplegable);
        this.colorTexto = colorTexto;
        this.colorPista = colorPista;
        this.colorDesplegable = contexto.getColor(R.color.texto_oscuro);
        this.colorPistaDesplegable = contexto.getColor(R.color.borde_campo);
    }

    private static List<String> conPista(Context contexto, List<String> unidades) {
        List<String> elementos = new ArrayList<>(unidades.size() + 1);
        elementos.add(contexto.getString(R.string.conv_seleccione_unidad));
        elementos.addAll(unidades);
        return elementos;
    }

    String unidadEn(int posicion) {
        return posicion <= 0 || posicion >= getCount() ? null : getItem(posicion);
    }

    @NonNull
    @Override
    public View getView(int posicion, View reciclada, @NonNull ViewGroup padre) {
        TextView vista = (TextView) super.getView(posicion, reciclada, padre);
        vista.setTextColor(posicion == 0 ? colorPista : colorTexto);
        return vista;
    }

    @Override
    public View getDropDownView(int posicion, View reciclada, @NonNull ViewGroup padre) {
        TextView vista = (TextView) super.getDropDownView(posicion, reciclada, padre);
        vista.setTextColor(posicion == 0 ? colorPistaDesplegable : colorDesplegable);
        return vista;
    }

    @Override
    public boolean isEnabled(int posicion) {
        return posicion != 0;
    }
}
