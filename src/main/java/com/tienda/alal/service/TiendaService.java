package com.tienda.alal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tienda.alal.exception.SpExecutionException;
import com.tienda.alal.exception.SpValidationException;

@Service
public class TiendaService {

    private final GenericService genericService;

    public TiendaService(GenericService genericService) {
        this.genericService = genericService;
    }

    public String insertarTienda(Map<String, Object> req) {
        if (req == null || req.isEmpty()) {
            throw new SpValidationException("El body del request está vacío");
        }

        List<Map<String, Object>> rows = genericService.ejecutarSP(1, new HashMap<>(req));
        if (rows.isEmpty()) {
            throw new SpExecutionException("El stored procedure no retornó resultados");
        }

        Object resultadoObj = rows.get(0).get("resultado");
        if (resultadoObj == null) {
            throw new SpExecutionException("El stored procedure no retornó el campo 'resultado'");
        }

        return resultadoObj.toString();
    }
}
