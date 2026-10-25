package ec.edu.monster;

import android.app.Application;
import android.content.Context;
import ec.edu.monster.config.SoapConfig;
import ec.edu.monster.controlador.CatalogoControlador;
import ec.edu.monster.controlador.ConversionControlador;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.modelos.interfaces.ConversionRepositorio;
import ec.edu.monster.modelos.interfaces.LoginRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.modelos.repositorios.CatalogoSoapRepositorio;
import ec.edu.monster.modelos.repositorios.ConversionSoapRepositorio;
import ec.edu.monster.modelos.repositorios.LoginSoapRepositorio;
import ec.edu.monster.modelos.soap.SoapTransporte;
import ec.edu.monster.servicios.EjecutorTareas;
import ec.edu.monster.servicios.RedAndroid;
import ec.edu.monster.servicios.SesionServicio;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AplicacionMonster extends Application {

    private SesionServicio sesion;
    private LoginRepositorio loginRepositorio;
    private CatalogoRepositorio catalogoRepositorio;
    private ConversionRepositorio conversionRepositorio;
    private ExecutorService hilos;
    private EjecutorTareas tareas;

    public static AplicacionMonster de(Context contexto) {
        return (AplicacionMonster) contexto.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sesion = new SesionServicio();

        SoapTransporte transporte = new SoapTransporte();
        VerificadorRed red = new RedAndroid(this);
        loginRepositorio = new LoginSoapRepositorio(SoapConfig.URL_BASE, transporte, red);
        catalogoRepositorio = new CatalogoSoapRepositorio(SoapConfig.URL_BASE, transporte, red);
        conversionRepositorio = new ConversionSoapRepositorio(SoapConfig.URL_BASE, transporte, red);

        hilos = Executors.newCachedThreadPool();
        tareas = new EjecutorTareas(hilos);
    }

    public EjecutorTareas getTareas() {
        return tareas;
    }

    public LoginControlador crearLoginControlador() {
        return new LoginControlador(loginRepositorio, sesion);
    }

    public ConversionControlador crearConversionControlador() {
        return new ConversionControlador(conversionRepositorio, catalogoRepositorio, sesion);
    }

    public CatalogoControlador crearCatalogoControlador() {
        return new CatalogoControlador(catalogoRepositorio, sesion, hilos);
    }
}
