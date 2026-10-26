package ec.edu.monster.modelos.data;

import java.util.HashMap;
import java.util.Map;

public final class NombresUnidades {

    private static final Map<String, String> NOMBRES = new HashMap<>();

    static {
        NOMBRES.put("m", "Metro");
        NOMBRES.put("cm", "Centímetro");
        NOMBRES.put("ft", "Pie");
        NOMBRES.put("yd", "Yarda");
        NOMBRES.put("mi", "Milla");
        NOMBRES.put("g", "Gramo");
        NOMBRES.put("kg", "Kilogramo");
        NOMBRES.put("oz", "Onza");
        NOMBRES.put("lb", "Libra");
        NOMBRES.put("qq", "Quintal");
        NOMBRES.put("°C", "Celsius");
        NOMBRES.put("°F", "Fahrenheit");
        NOMBRES.put("K", "Kelvin");
        NOMBRES.put("°N", "Newton");
        NOMBRES.put("°Ré", "Réaumur");
        NOMBRES.put("mL", "Mililitro");
        NOMBRES.put("L", "Litro");
        NOMBRES.put("m3", "Metro cúbico");
        NOMBRES.put("ft3", "Pie cúbico");
        NOMBRES.put("gal", "Galón");
        NOMBRES.put("ms", "Milisegundo");
        NOMBRES.put("s", "Segundo");
        NOMBRES.put("min", "Minuto");
        NOMBRES.put("h", "Hora");
        NOMBRES.put("d", "Día");
        NOMBRES.put("w", "Semana");
        NOMBRES.put("cm2", "Centímetro cuadrado");
        NOMBRES.put("m2", "Metro cuadrado");
        NOMBRES.put("km2", "Kilómetro cuadrado");
        NOMBRES.put("ha", "Hectárea");
        NOMBRES.put("ft2", "Pie cuadrado");
        NOMBRES.put("ac", "Acre");
    }

    private NombresUnidades() { }

    public static String nombre(String codigo) {
        String nombre = NOMBRES.get(codigo);
        return nombre != null ? nombre : codigo;
    }
}
