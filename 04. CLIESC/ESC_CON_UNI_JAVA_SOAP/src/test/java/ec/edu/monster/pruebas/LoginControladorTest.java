package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.vista.LoginVista;
import ec.edu.monster.ws.conversor.CatalogDTO;
import ec.edu.monster.ws.conversor.ConversorWebService;
import ec.edu.monster.ws.conversor.ConverterDTO;
import ec.edu.monster.ws.login.EstadosEnum;
import ec.edu.monster.ws.login.LoginDTO;
import ec.edu.monster.ws.login.LoginWebService;
import org.junit.jupiter.api.Test;

class LoginControladorTest {

    private final Sesion sesion = new Sesion();
    private final VistaLoginFalsa vista = new VistaLoginFalsa();
    private int llamadasAlServidor;

    @Test
    void showsErrorAndSkipsServerWhenCredentialsAreBlank() {
        new LoginControlador(vista, servicio((usuario, clave) -> loginOk()));
        vista.usuario = "  ";
        vista.clave = "";

        vista.presionarIngresar();

        assertEquals("Ingrese usuario y contraseña", vista.error);
        assertFalse(vista.menuAbierto);
        assertEquals(0, llamadasAlServidor);
    }

    @Test
    void opensMenuWhenLoginSucceeds() {
        new LoginControlador(vista, servicio((usuario, clave) -> loginOk()));
        vista.usuario = "user";
        vista.clave = "1234";

        vista.presionarIngresar();

        assertTrue(vista.menuAbierto);
        assertNull(vista.error);
        assertEquals("user", sesion.getUsuario());
    }

    @Test
    void showsServerMessageWhenLoginIsRejected() {
        new LoginControlador(vista, servicio((usuario, clave) -> loginError("Credenciales incorrectas")));
        vista.usuario = "user";
        vista.clave = "mala";

        vista.presionarIngresar();

        assertEquals("Credenciales incorrectas", vista.error);
        assertFalse(vista.menuAbierto);
    }

    private ConversorServicio servicio(LoginWebService login) {
        LoginWebService contador = (usuario, clave) -> {
            llamadasAlServidor++;
            return login.login(usuario, clave);
        };
        return new ConversorServicio(contador, new ConversorSinUso(), sesion);
    }

    private static LoginDTO loginOk() {
        LoginDTO dto = new LoginDTO();
        dto.setEstado(EstadosEnum.OK);
        dto.setToken("tok-1");
        return dto;
    }

    private static LoginDTO loginError(String mensaje) {
        LoginDTO dto = new LoginDTO();
        dto.setEstado(EstadosEnum.ERROR);
        dto.setMensaje(mensaje);
        return dto;
    }

    private static final class VistaLoginFalsa implements LoginVista {

        private String usuario = "";
        private String clave = "";
        private String error;
        private boolean menuAbierto;
        private Runnable accionIngresar;

        void presionarIngresar() {
            accionIngresar.run();
        }

        @Override
        public String getUsuario() {
            return usuario;
        }

        @Override
        public String getClave() {
            return clave;
        }

        @Override
        public void mostrarError(String mensaje) {
            error = mensaje;
        }

        @Override
        public void abrirMenu() {
            menuAbierto = true;
        }

        @Override
        public void alIngresar(Runnable accion) {
            accionIngresar = accion;
        }
    }

    private static final class ConversorSinUso implements ConversorWebService {

        @Override
        public ConverterDTO convert(String token, String type, double value, String from, String to) {
            throw new AssertionError("Login must not call the converter");
        }

        @Override
        public CatalogDTO catalog(String token, String type) {
            throw new AssertionError("Login must not call the catalog");
        }
    }
}
