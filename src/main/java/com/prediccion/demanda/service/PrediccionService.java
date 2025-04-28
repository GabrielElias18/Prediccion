package com.prediccion.demanda.service;

import weka.classifiers.Classifier;
import weka.core.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class PrediccionService {

    private Classifier modelo;
    private Instances estructura;

    public PrediccionService() throws Exception {
        // Cargar modelo desde resources
        InputStream modeloStream = getClass().getClassLoader().getResourceAsStream("modelo_prediccion.model");
        if (modeloStream == null) {
            throw new IllegalArgumentException("No se pudo encontrar el archivo modelo_demanda.model en resources");
        }
        modelo = (Classifier) SerializationHelper.read(modeloStream);

        // Leer categorías desde categorias.txt
        InputStream catStream = getClass().getClassLoader().getResourceAsStream("categorias.txt");
        if (catStream == null) {
            throw new IllegalArgumentException("No se pudo encontrar categorias.txt en resources");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(catStream));
        ArrayList<String> categorias = new ArrayList<>();
        String linea;
        while ((linea = reader.readLine()) != null) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                categorias.add(linea);
            }
        }

        // Definir estructura de datos
        ArrayList<Attribute> atributos = new ArrayList<>();
        atributos.add(new Attribute("precio_venta"));
        atributos.add(new Attribute("precio_compra"));
        atributos.add(new Attribute("cantidad_disponible"));
        atributos.add(new Attribute("historico_ventas"));
        atributos.add(new Attribute("tiempo_en_mercado"));
        atributos.add(new Attribute("categoria", categorias));

        ArrayList<String> clases = new ArrayList<>();
        clases.add("bajo");
        clases.add("medio");
        clases.add("alto");
        atributos.add(new Attribute("demanda", clases));

        estructura = new Instances("PrediccionDemanda", atributos, 0);
        estructura.setClassIndex(6); // demanda es la clase
    }

    public Map<String, Double> obtenerProbabilidades(double precioVenta, double precioCompra, double cantidadDisponible,
                                                     double historicoVentas, double tiempoEnMercado, String categoria) throws Exception {

        Instance instancia = new DenseInstance(7);
        instancia.setValue(estructura.attribute(0), precioVenta);
        instancia.setValue(estructura.attribute(1), precioCompra);
        instancia.setValue(estructura.attribute(2), cantidadDisponible);
        instancia.setValue(estructura.attribute(3), historicoVentas);
        instancia.setValue(estructura.attribute(4), tiempoEnMercado);
        instancia.setValue(estructura.attribute(5), categoria);
        instancia.setDataset(estructura);

        double[] distribucion = modelo.distributionForInstance(instancia);

        Map<String, Double> probabilidades = new HashMap<>();
        for (int i = 0; i < distribucion.length; i++) {
            String clase = estructura.classAttribute().value(i);
            double porcentaje = distribucion[i] * 100;
            probabilidades.put(clase, Math.round(porcentaje * 100.0) / 100.0); // Redondeo a 2 decimales
        }

        return probabilidades;
    }
}
