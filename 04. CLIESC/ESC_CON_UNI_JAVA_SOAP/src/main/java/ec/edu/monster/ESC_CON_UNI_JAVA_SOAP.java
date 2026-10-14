package ec.edu.monster;

import ec.edu.monster.ws.conversor.CatalogDTO;
import ec.edu.monster.ws.conversor.ConversorWebService;
import ec.edu.monster.ws.conversor.ConversorWebService_Service;
import ec.edu.monster.ws.conversor.ConverterDTO;
import ec.edu.monster.ws.login.LoginDTO;
import ec.edu.monster.ws.login.LoginWebService;
import ec.edu.monster.ws.login.LoginWebService_Service;

public class ESC_CON_UNI_JAVA_SOAP {

    public static void main(String[] args) {
        LoginWebService login = new LoginWebService_Service().getLoginWebServicePort();
        LoginDTO session = login.login("user", "1234");
        System.out.println("Login: " + session.getEstado() + " - " + session.getMensaje());

        ConversorWebService conversor = new ConversorWebService_Service().getConversorWebServicePort();

        ConverterDTO conversion = conversor.convert(session.getToken(), "Longitud", 100, "m", "ft");
        System.out.println("Convert: " + conversion.getEstado() + " -> " + conversion.getResultado());

        CatalogDTO catalog = conversor.catalog(session.getToken(), "Longitud");
        System.out.println("Catalog: " + catalog.getResultado().getUnidad());
    }
}