/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ec.edu.monster.modelo.soap;

import ec.edu.monster.modelo.ServicioException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author IMarcusDev
 */
public class SoapTransporte {
    private static final int TIEMPO_ESPERA_POR_DEFECTO_MS = 10_000;
    
    private final int tiempoEsperaMs;
    
    public SoapTransporte() {
        this(TIEMPO_ESPERA_POR_DEFECTO_MS);
    }
    
    public SoapTransporte(int tiempoEsperaMs) {
        this.tiempoEsperaMs = tiempoEsperaMs;
    }
    
    public String enviar(String url, String sobre) throws ServicioException {
        HttpURLConnection conexion = null;
        try{
            conexion = (HttpURLConnection) new URL(url).openConnection();
            conexion.setRequestMethod("POST");
            conexion.setConnectTimeout(tiempoEsperaMs);
            conexion.setReadTimeout(tiempoEsperaMs);
            conexion.setDoOutput(true);
            conexion.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conexion.setRequestProperty("SOAPAction", "");
        
            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(sobre.getBytes(StandardCharsets.UTF_8));
            }
            
            int codigo = conexion.getResponseCode();
            if(codigo == HttpURLConnection.HTTP_OK) {
                return leer(conexion.getInputStream());
            }
            
            if(codigo == HttpURLConnection.HTTP_INTERNAL_ERROR && conexion.getErrorStream() != null){
                return leer(conexion.getErrorStream());
            }
            throw new ServicioException("El servidor respondio con HTTP " + codigo + " en " + url);
        } catch (IOException e) {
            throw new ServicioException("No se pudo conectar con el servicio en " + url, e);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }
    
    private static String leer(InputStream flujo) throws IOException {
        try (InputStream entrada = flujo; ByteArrayOutputStream buffer = new ByteArrayOutputStream()){
            byte[] bloque = new byte[4096];
            int leidos;
            while ((leidos = entrada.read(bloque)) != -1) {
                buffer.write(bloque,0 ,leidos);
            }
            return buffer.toString(StandardCharsets.UTF_8.name());
        }
    }
}
