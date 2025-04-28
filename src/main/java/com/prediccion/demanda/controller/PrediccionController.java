package com.prediccion.demanda.controller;

import com.prediccion.demanda.service.PrediccionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PrediccionController {

    private final PrediccionService prediccionService;

    public PrediccionController() throws Exception {
        this.prediccionService = new PrediccionService();
    }

    @PostMapping("/predecir")
    public Map<String, Double> predecir(@RequestBody Map<String, Object> datos) throws Exception {
        double precioVenta = ((Number) datos.get("precio_venta")).doubleValue();
        double precioCompra = ((Number) datos.get("precio_compra")).doubleValue();
        double cantidad = ((Number) datos.get("cantidad_disponible")).doubleValue();
        double historico = ((Number) datos.get("historico_ventas")).doubleValue();
        double tiempo = ((Number) datos.get("tiempo_en_mercado")).doubleValue();
        String categoria = (String) datos.get("categoria");

        return prediccionService.obtenerProbabilidades(precioVenta, precioCompra, cantidad, historico, tiempo, categoria);
    }
}
