package ec.edu.monster.config;

public final class SoapConfig {

    public static final String URL_BASE = "http://192.168.1.2:8080/WS_CON_UNI_JAVA_SOAP/";

    public static final String NAMESPACE = "http://ws.monster.edu.ec/";
    public static final String SERVICIO_LOGIN = "LoginWebService";
    public static final String SERVICIO_CONVERSOR = "ConversorWebService";

    public static final int TIEMPO_ESPERA_SEGUNDOS = 15;
    public static final int TIEMPO_ESPERA_MS = TIEMPO_ESPERA_SEGUNDOS * 1000;

    private SoapConfig() { }
}
