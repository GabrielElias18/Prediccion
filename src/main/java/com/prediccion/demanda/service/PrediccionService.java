package com.prediccion.demanda.service;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.SerializationHelper;
import weka.core.Attribute;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrediccionService {

    private Classifier modelo;
    private Instances estructura;

    public PrediccionService() throws Exception {
        // Cargar el modelo desde resources
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("modelo_demanda.model");
        if (inputStream == null) {
            throw new IllegalArgumentException("No se pudo encontrar el archivo modelo_j48_demanda.model en resources");
        }
        modelo = (Classifier) SerializationHelper.read(inputStream);

        // Definir estructura de datos (sin datos, solo definición)
        ArrayList<Attribute> atributos = new ArrayList<>();
        atributos.add(new Attribute("precio_venta"));
        atributos.add(new Attribute("cantidad_disponible"));
        atributos.add(new Attribute("historico_ventas"));
        atributos.add(new Attribute("tiempo_en_mercado"));

        ArrayList<String> clases = new ArrayList<>();
        clases.add("bajo");
        clases.add("medio");
        clases.add("alto");

        atributos.add(new Attribute("demanda", clases));

        estructura = new Instances("PrediccionDemanda", atributos, 0);
        estructura.setClassIndex(4);
    }

    public Map<String, Double> obtenerProbabilidades(double precioVenta, double cantidadDisponible, double historicoVentas, double tiempoEnMercado) throws Exception {
        Instance instancia = new DenseInstance(5);
        instancia.setValue(estructura.attribute(0), precioVenta);
        instancia.setValue(estructura.attribute(1), cantidadDisponible);
        instancia.setValue(estructura.attribute(2), historicoVentas);
        instancia.setValue(estructura.attribute(3), tiempoEnMercado);
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
