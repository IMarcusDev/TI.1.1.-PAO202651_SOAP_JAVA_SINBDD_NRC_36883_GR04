package ec.edu.monster.controlador;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ec.edu.monster.R;
import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.excepciones.SoapException;
import ec.edu.monster.modelos.interfaces.ConvertidorRepositorio;
import ec.edu.monster.modelos.repositorios.ConvertidorSoapRepositorio;
import ec.edu.monster.modelos.soap.SoapTransporte;
import ec.edu.monster.vista.ConvertidorVista;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvertidorActivity extends Activity {

    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private ConvertidorVista vista;
    private ConvertidorRepositorio repositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String token = getIntent().getStringExtra(LoginActivity.EXTRA_TOKEN);
        if (token == null) {
            volverAlLogin();
            return;
        }

        setContentView(R.layout.activity_convertidor);
        vista = new ConvertidorVista(this);

        String urlBase = getString(R.string.url_servidor);
        repositorio = new ConvertidorSoapRepositorio(urlBase + "/ConversorWebService", token, new SoapTransporte());

        vista.alCambiarMagnitud(this::cargarUnidades);
        vista.alPresionarConvertir(this::convertir);
        vista.alPresionarCerrarSesion(this::volverAlLogin);

        vista.mostrarMagnitudes(Arrays.asList(Magnitud.values()));
    }

    private void cargarUnidades(Magnitud magnitud) {
        vista.ocultarError();
        vista.ocultarResultado();
        vista.mostrarCargando(true);

        ejecutor.execute(() -> {
            try {
                List<String> unidades = repositorio.obtenerUnidades(magnitud);
                enHiloPrincipal(() -> {
                    vista.mostrarUnidades(unidades);
                    vista.mostrarCargando(false);
                });
            } catch (SoapException e) {
                enHiloPrincipal(() -> {
                    vista.mostrarCargando(false);
                    vista.mostrarError(e.getMessage());
                });
            }
        });
    }

    private void convertir() {
        vista.ocultarError();
        vista.ocultarResultado();

        Conversion conversion;
        try {
            double valor = Double.parseDouble(vista.getValor().replace(',', '.'));
            conversion = new Conversion(vista.getMagnitud(), valor,
                    vista.getUnidadOrigen(), vista.getUnidadDestino());
        } catch (NumberFormatException e) {
            vista.mostrarError(getString(R.string.error_valor_invalido));
            return;
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
            return;
        }

        vista.mostrarCargando(true);

        ejecutor.execute(() -> {
            try {
                Conversion resultado = repositorio.convertir(conversion);
                enHiloPrincipal(() -> {
                    vista.mostrarCargando(false);
                    vista.mostrarResultado(formatear(resultado.getResultado()), describir(resultado));
                });
            } catch (SoapException e) {
                enHiloPrincipal(() -> {
                    vista.mostrarCargando(false);
                    vista.mostrarError(e.getMessage());
                });
            }
        });
    }

    private String describir(Conversion conversion) {
        return getString(R.string.conv_resultado,
                formatear(conversion.getValor()), conversion.getUnidadOrigen(),
                formatear(conversion.getResultado()), conversion.getUnidadDestino());
    }

    private static String formatear(double numero) {
        NumberFormat formato = NumberFormat.getInstance();
        formato.setMaximumFractionDigits(6);
        return formato.format(numero);
    }

    private void enHiloPrincipal(Runnable accion) {
        runOnUiThread(() -> {
            if (!isFinishing()) {
                accion.run();
            }
        });
    }

    private void volverAlLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        ejecutor.shutdownNow();
        super.onDestroy();
    }
}