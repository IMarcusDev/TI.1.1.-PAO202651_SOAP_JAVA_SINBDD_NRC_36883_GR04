package ec.edu.monster.pruebas.controlador;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.servicios.SesionServicio;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class LoginControladorTest {

    private static class RepositorioFalso implements ec.edu.monster.modelos.interfaces.LoginRepositorio {
        final List<String> recibidas = new ArrayList<>();
        LoginResultado respuesta = new LoginResultado(true, "ok", "token-1");

        @Override
        public LoginResultado iniciarSesion(String usuario, String clave) {
            recibidas.add(usuario + "/" + clave);
            return respuesta;
        }
    }

    private static String repetir(char c, int veces) {
        return new String(new char[veces]).replace('\0', c);
    }

    @Test
    public void iniciarSesion_validaSinLlamarAlServidor() {
        Object[][] casos = {
            {null, "1234", "Ingrese el usuario"},
            {"", "1234", "Ingrese el usuario"},
            {"   ", "1234", "Ingrese el usuario"},
            {"user", null, "Ingrese la contraseña"},
            {"user", "   ", "Ingrese la contraseña"},
            {repetir('u', 51), "1234", "El usuario no puede superar 50 caracteres"},
            {"user", repetir('c', 65), "La contraseña no puede superar 64 caracteres"},
        };
        for (Object[] caso : casos) {
            RepositorioFalso repositorio = new RepositorioFalso();
            LoginResultado resultado = new LoginControlador(repositorio, new SesionServicio())
                    .iniciarSesion((String) caso[0], (String) caso[1]);
            assertEquals((String) caso[2], caso[2], resultado.getMensaje());
            assertTrue("no debía llamar al servidor", repositorio.recibidas.isEmpty());
        }
    }

    @Test
    public void iniciarSesion_limitesExactos_seAceptan() {
        RepositorioFalso repositorio = new RepositorioFalso();

        new LoginControlador(repositorio, new SesionServicio()).iniciarSesion(repetir('u', 50), repetir('c', 64));

        assertEquals(1, repositorio.recibidas.size());
    }

    @Test
    public void iniciarSesion_aplicaTrimYGuardaLaSesion() {
        RepositorioFalso repositorio = new RepositorioFalso();
        SesionServicio sesion = new SesionServicio();

        LoginResultado resultado = new LoginControlador(repositorio, sesion).iniciarSesion("  user ", " 1234  ");

        assertTrue(resultado.isExito());
        assertEquals("user/1234", repositorio.recibidas.get(0));
        assertEquals("token-1", sesion.getToken());
        assertEquals("user", sesion.getUsuario());
    }

    @Test
    public void iniciarSesion_fallido_noGuardaSesion() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.respuesta = LoginResultado.error("Credenciales Incorrectas");
        SesionServicio sesion = new SesionServicio();

        new LoginControlador(repositorio, sesion).iniciarSesion("user", "x");

        assertFalse(sesion.estaAutenticado());
    }

    @Test
    public void cerrarSesion_limpiaLaSesion() {
        SesionServicio sesion = new SesionServicio();
        sesion.iniciar("user", "t");

        new LoginControlador(new RepositorioFalso(), sesion).cerrarSesion();

        assertFalse(sesion.estaAutenticado());
    }
}
