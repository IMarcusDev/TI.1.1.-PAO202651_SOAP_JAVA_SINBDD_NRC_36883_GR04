package ec.edu.monster.modelos.excepciones;

public class SoapException extends Exception {
    public SoapException(String mensaje) {
        super(mensaje);
    }
    
    public SoapException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
