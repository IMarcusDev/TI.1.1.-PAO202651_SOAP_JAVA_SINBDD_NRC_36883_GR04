package ec.edu.monster.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ec.edu.monster.R;
import ec.edu.monster.modelos.data.GrupoUnidades;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class CatalogoAdaptador extends RecyclerView.Adapter<CatalogoAdaptador.Tarjeta> {

    private final List<GrupoUnidades> grupos = new ArrayList<>();
    private final Consumer<GrupoUnidades> alTocar;

    CatalogoAdaptador(Consumer<GrupoUnidades> alTocar) {
        this.alTocar = alTocar;
    }

    void mostrar(List<GrupoUnidades> nuevos) {
        grupos.clear();
        grupos.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Tarjeta onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext()).inflate(R.layout.item_magnitud_card, padre, false);
        return new Tarjeta(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Tarjeta tarjeta, int posicion) {
        GrupoUnidades grupo = grupos.get(posicion);
        tarjeta.imgIcono.setImageResource(grupo.getIcono());
        tarjeta.lblNombre.setText(grupo.getNombre());
        tarjeta.lblDescripcion.setText(grupo.getDescripcion());
        tarjeta.lblCantidad.setText(tarjeta.itemView.getContext()
                .getString(R.string.cat_cantidad, grupo.getCantidad()));
        tarjeta.itemView.setOnClickListener(v -> alTocar.accept(grupo));
    }

    @Override
    public int getItemCount() {
        return grupos.size();
    }

    static class Tarjeta extends RecyclerView.ViewHolder {
        final ImageView imgIcono;
        final TextView lblNombre;
        final TextView lblDescripcion;
        final TextView lblCantidad;

        Tarjeta(View vista) {
            super(vista);
            imgIcono = vista.findViewById(R.id.imgIcono);
            lblNombre = vista.findViewById(R.id.lblNombre);
            lblDescripcion = vista.findViewById(R.id.lblDescripcion);
            lblCantidad = vista.findViewById(R.id.lblCantidad);
        }
    }
}
