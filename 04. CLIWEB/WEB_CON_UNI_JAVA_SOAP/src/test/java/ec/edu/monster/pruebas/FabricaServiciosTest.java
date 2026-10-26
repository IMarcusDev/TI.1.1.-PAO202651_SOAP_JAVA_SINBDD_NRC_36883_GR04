package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import ec.edu.monster.controlador.FabricaServicios;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class FabricaServiciosTest {

    private HttpServer servidor;

    @AfterEach
    void apagarServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    // Disponible cuando el servidor SOAP responde el WSDL.
    @Test
    void disponibleWhenServerAnswersWsdl() throws IOException {
        FabricaServicios fabrica = new FabricaServicios(arrancar(200));

        assertTrue(fabrica.disponible());
    }

    // Un error del servidor no cuenta como disponible.
    @Test
    void noDisponibleWhenServerFails() throws IOException {
        FabricaServicios fabrica = new FabricaServicios(arrancar(500));

        assertFalse(fabrica.disponible());
    }

    // Caso de la cabecera: el servidor estuvo arriba y luego se cae.
    // Debe pasar a no disponible en la siguiente consulta, sin quedarse con el estado anterior.
    @Test
    void noDisponibleAfterServerGoesDown() throws IOException {
        URI wsdl = arrancar(200);
        FabricaServicios fabrica = new FabricaServicios(wsdl);
        assertTrue(fabrica.disponible());

        servidor.stop(0);

        assertFalse(fabrica.disponible());
    }

    private URI arrancar(int estado) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            intercambio.sendResponseHeaders(estado, -1);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.flush();
            }
        });
        servidor.start();
        return URI.create("http://localhost:" + servidor.getAddress().getPort() + "/ConversorWebService?wsdl");
    }
}
