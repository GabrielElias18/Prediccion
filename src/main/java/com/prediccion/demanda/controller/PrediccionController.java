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
    public Map<String, Double> predecir(@RequestBody Map<String, Double> datos) throws Exception {
        double precio = datos.get("precio_venta");
        double cantidad = datos.get("cantidad_disponible");
        double historico = datos.get("historico_ventas");
        double tiempo = datos.get("tiempo_en_mercado");

        return prediccionService.obtenerProbabilidades(precio, cantidad, historico, tiempo);
    }
}
