package ec.edu.monster.pruebas.soap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelo.soap.SoapConstructorPeticion;
import java.util.Locale;
import org.junit.Test;

public class SoapConstructorPeticionTest {

    private static final String NAMESPACE = "http://ws.monster.edu.ec/";

    @Test
    public void construir_operacionConPrefijo_yParametrosEnOrdenSinPrefijo() {
        String xml = new SoapConstructorPeticion(NAMESPACE, "login")
                .parametro("username", "user")
                .parametro("password", "1234")
                .construir();

        assertTrue(xml.contains("xmlns:ws=\"" + NAMESPACE + "\""));
        assertTrue(xml.contains(
                "<ws:login><username>user</username><password>1234</password></ws:login>"));
    }

    @Test
    public void parametro_valorNulo_seOmite() {
        String xml = new SoapConstructorPeticion(NAMESPACE, "catalog")
                .parametro("token", null)
                .parametro("type", "Area")
                .construir();

        assertFalse(xml.contains("<token>"));
        assertTrue(xml.contains("<type>Area</type>"));
    }

    @Test
    public void parametro_caracteresEspeciales_seEscapan() {
        String xml = new SoapConstructorPeticion(NAMESPACE, "login")
                .parametro("password", "a&b<c")
                .construir();

        assertTrue(xml.contains("<password>a&amp;b&lt;c</password>"));
    }

    @Test
    public void parametro_double_usaPuntoDecimalSinImportarElIdioma() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);

            String xml = new SoapConstructorPeticion(NAMESPACE, "convert")
                    .parametro("value", 2.5)
                    .construir();

            assertTrue(xml.contains("<value>2.5</value>"));
        } finally {
            Locale.setDefault(original);
        }
    }
}