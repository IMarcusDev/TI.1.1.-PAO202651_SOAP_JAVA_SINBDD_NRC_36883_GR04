package ec.edu.monster.vista;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import ec.edu.monster.R;

public class ReglaVista extends View {

    private final Paint pincelMarca = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pincelNumero = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float paso;
    private final float margenNumero;
    private final boolean mostrarNumeros;

    public ReglaVista(Context contexto, AttributeSet atributos) {
        super(contexto, atributos);
        float densidad = getResources().getDisplayMetrics().density;
        paso = 6 * densidad;
        margenNumero = 3 * densidad;

        TypedArray valores = contexto.obtainStyledAttributes(atributos, R.styleable.ReglaVista);
        int color = valores.getColor(R.styleable.ReglaVista_colorMarca,
                contexto.getColor(R.color.tinta));
        mostrarNumeros = valores.getBoolean(R.styleable.ReglaVista_mostrarNumeros, false);
        valores.recycle();

        pincelMarca.setColor(color);
        pincelMarca.setStrokeWidth(1.25f * densidad);
        pincelNumero.setColor(color);
        pincelNumero.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 11, getResources().getDisplayMetrics()));
        pincelNumero.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
    }

    @Override
    protected void onDraw(Canvas lienzo) {
        super.onDraw(lienzo);
        float alto = getHeight();
        float largo = alto * (mostrarNumeros ? 0.45f : 0.9f);
        float medio = largo * 0.66f;
        float corto = largo * 0.4f;

        float ancho = getWidth();
        for (int i = 0; ; i++) {
            float x = getPaddingStart() + i * paso;
            if (x > ancho) {
                break;
            }
            float marca = i % 10 == 0 ? largo : i % 5 == 0 ? medio : corto;
            lienzo.drawLine(x, 0, x, marca, pincelMarca);

            if (mostrarNumeros && i % 10 == 0 && i > 0) {
                lienzo.drawText(String.valueOf(i / 10), x + margenNumero,
                        largo + pincelNumero.getTextSize(), pincelNumero);
            }
        }
    }
}
