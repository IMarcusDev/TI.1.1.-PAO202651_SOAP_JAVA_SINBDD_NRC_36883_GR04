package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.monster.controlador.FabricaServicios;
import ec.edu.monster.controlador.InicioAplicacion;
import ec.edu.monster.controlador.MenuServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/** Application wiring: startup listener, factory lookup and the menu page. */
class AplicacionWebTest {

    @Test
    void startupRegistersFactoryThatServletsLookUp() {
        ServletContext context = mock(ServletContext.class);

        new InicioAplicacion().contextInitialized(new ServletContextEvent(context));

        ArgumentCaptor<Object> factory = ArgumentCaptor.forClass(Object.class);
        verify(context).setAttribute(eq(WebFixture.FACTORY_ATTRIBUTE), factory.capture());
        when(context.getAttribute(WebFixture.FACTORY_ATTRIBUTE)).thenReturn(factory.getValue());
        assertSame(factory.getValue(), FabricaServicios.de(context));
    }

    @Test
    void menuForwardsToMenuView() throws ServletException, IOException {
        WebFixture web = new WebFixture();
        web.logIn();

        web.call(new MenuServlet(), "GET");

        assertEquals("/ec/edu/monster/vista/menu.jsp", web.forwardedTo);
        verify(web.dispatcher).forward(any(), any());
    }
}
