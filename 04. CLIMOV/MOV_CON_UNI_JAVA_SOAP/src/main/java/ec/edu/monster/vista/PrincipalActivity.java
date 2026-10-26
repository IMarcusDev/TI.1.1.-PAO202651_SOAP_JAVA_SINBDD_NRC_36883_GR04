package ec.edu.monster.vista;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import ec.edu.monster.R;

public class PrincipalActivity extends AppCompatActivity {

    private static final String PESTANA_CONVERSION = "conversion";
    private static final String PESTANA_CATALOGO = "catalogo";

    private BottomNavigationView barraPestanas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.WHITE, Color.WHITE));
        setContentView(R.layout.activity_principal);

        barraPestanas = findViewById(R.id.barraPestanas);
        barraPestanas.setItemActiveIndicatorEnabled(false);
        barraPestanas.setOnItemSelectedListener(item -> {
            mostrarPestana(item.getItemId() == R.id.pestana_catalogo ? PESTANA_CATALOGO : PESTANA_CONVERSION);
            return true;
        });

        if (savedInstanceState == null) {
            mostrarPestana(PESTANA_CONVERSION);
        }

        View raiz = findViewById(R.id.raizPrincipal);
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, bordes) -> {
            boolean teclado = bordes.isVisible(WindowInsetsCompat.Type.ime());
            int alto = bordes.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            barraPestanas.setVisibility(teclado ? View.GONE : View.VISIBLE);
            vista.setPadding(0, 0, 0, teclado ? alto : 0);
            return bordes;
        });
    }

    private void mostrarPestana(String etiqueta) {
        FragmentManager gestor = getSupportFragmentManager();
        FragmentTransaction transaccion = gestor.beginTransaction().setReorderingAllowed(true);

        Fragment destino = gestor.findFragmentByTag(etiqueta);
        if (destino == null) {
            destino = PESTANA_CATALOGO.equals(etiqueta) ? new CatalogoFragment() : new ConversionFragment();
            transaccion.add(R.id.contenedorPestanas, destino, etiqueta);
        }
        for (Fragment pestana : gestor.getFragments()) {
            if (pestana != destino) {
                transaccion.hide(pestana);
            }
        }
        transaccion.show(destino).commit();
    }
}
