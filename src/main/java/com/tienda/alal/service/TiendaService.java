package com.tienda.alal.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TiendaService {

    private final GenericService genericService;

    public TiendaService(GenericService genericService) {
        this.genericService = genericService;
    }

    public String insertarTienda(Map<String, Object> req) {

        if (req == null || req.isEmpty()) {
            return "Error: request vacío";
        }

        // Este endpoint /tienda es un flujo especializado para el SP de tienda.
        // No se requiere que el cliente envíe sp_id, porque aquí siempre ejecutamos
        // el stored procedure definido para insertar tienda.
        //
        // Si en el futuro queremos que /tienda reciba otro SP, podemos cambiar
        // esta lógica, pero por ahora mantenemos el sp_id quemado en 1.
        Integer spId = 1;

        Map<String, Object> params = new HashMap<>(req);

        try {
            Map<String, Object> result = genericService.ejecutarSP(spId, params);
            return result.get("resultado").toString();

        } catch (RuntimeException e) {
            // Las validaciones dinámicas lanzan RuntimeException con mensajes específicos
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            // Log del error para debugging
            System.err.println("Error al ejecutar stored procedure: " + e.getMessage());
            return "Error: problema técnico al guardar la tienda";
        }
    }
}