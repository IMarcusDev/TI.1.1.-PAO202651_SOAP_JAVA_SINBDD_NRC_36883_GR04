package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

/**
 * Main window shown after login: conversion and catalog tabs plus logout.
 * Controllers are wired by the composition root through the panel getters.
 */
public class PrincipalFrame extends JFrame {

    private final ConversionPanel conversionPanel = new ConversionPanel();
    private final CatalogoPanel catalogoPanel = new CatalogoPanel();

    public PrincipalFrame(Runnable alCerrarSesion) {
        super(Estilos.TITULO_VENTANA);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(760, 520);
        setLocationRelativeTo(null);

        JButton botonCerrarSesion = Estilos.botonPrimario("Cerrar sesión");
        botonCerrarSesion.addActionListener(e -> {
            try {
                alCerrarSesion.run();
            } finally {
                dispose();
            }
        });
        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        barraSuperior.setBackground(Estilos.FONDO);
        barraSuperior.add(botonCerrarSesion);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(Estilos.FUENTE_ETIQUETA);
        pestanas.addTab("Convertir", conversionPanel);
        pestanas.addTab("Catálogo", catalogoPanel);

        JPanel estado = Estilos.estadoServicio("Sesión activa · Servicio disponible");
        estado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.BORDE),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        estado.setOpaque(true);
        estado.setBackground(Estilos.BLANCO);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilos.FONDO);
        raiz.add(barraSuperior, BorderLayout.NORTH);
        raiz.add(pestanas, BorderLayout.CENTER);
        raiz.add(estado, BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    public ConversionPanel getConversionPanel() {
        return conversionPanel;
    }

    public CatalogoPanel getCatalogoPanel() {
        return catalogoPanel;
    }
}
