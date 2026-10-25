package ec.edu.monster.controlador;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.data.ValidacionResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.modelos.interfaces.ConversionRepositorio;
import ec.edu.monster.servicios.SesionServicio;

public class ConversionControlador extends ControladorConSesion {

    public static final String MENSAJE_MISMA_UNIDAD = "Origen y destino son la misma unidad; el valor no cambia.";

    private final ConversionRepositorio conversionRepositorio;
    private final CatalogoRepositorio catalogoRepositorio;

    public ConversionControlador(ConversionRepositorio conversionRepositorio,
                                 CatalogoRepositorio catalogoRepositorio,
                                 SesionServicio sesion) {
        super(sesion);
        this.conversionRepositorio = conversionRepositorio;
        this.catalogoRepositorio = catalogoRepositorio;
    }

    public List<Magnitud> getMagnitudes() {
        return Collections.unmodifiableList(Arrays.asList(Magnitud.values()));
    }

    public CatalogoResultado obtenerUnidades(Magnitud magnitud) {
        if (magnitud == null) {
            return CatalogoResultado.error(ValidadorConversion.MENSAJE_SIN_MAGNITUD);
        }
        if (!haySesion()) {
            return CatalogoResultado.expirada(MENSAJE_SESION_EXPIRADA);
        }

        CatalogoResultado resultado = catalogoRepositorio.obtenerCatalogo(getToken(), magnitud);

        limpiarSiExpiro(resultado.isSesionExpirada());
        return resultado.isSesionExpirada() ? resultado.conMensaje(MENSAJE_SESION_EXPIRADA) : resultado;
    }

    public String filtrarValor(String texto, Magnitud magnitud) {
        return ValidadorConversion.filtrarValor(texto, magnitud != null && magnitud.permiteNegativos());
    }

    public ConversionResultado convertir(Magnitud magnitud, String valor, String origen, String destino) {
        ValidacionResultado validacion = ValidadorConversion.validar(magnitud, valor, origen, destino);
        if (!validacion.isValido()) {
            return ConversionResultado.error(validacion.getMensaje());
        }

        Conversion conversion = new Conversion(magnitud, validacion.getValor(), origen, destino);

        if (origen.equals(destino)) {
            conversion.setResultado(validacion.getValor());
            return new ConversionResultado(true, MENSAJE_MISMA_UNIDAD, validacion.getValor(), false, conversion);
        }

        if (!haySesion()) {
            return ConversionResultado.expirada(MENSAJE_SESION_EXPIRADA);
        }

        ConversionResultado resultado = conversionRepositorio.convertir(getToken(), conversion);

        limpiarSiExpiro(resultado.isSesionExpirada());
        if (resultado.isSesionExpirada()) {
            return resultado.conMensaje(MENSAJE_SESION_EXPIRADA);
        }
        if (!resultado.isExito()) {
            return resultado;
        }

        conversion.setResultado(resultado.getValor());
        return resultado.conConversion(conversion);
    }
}
