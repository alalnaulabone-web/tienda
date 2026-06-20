package com.tienda.alal.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

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

        Map<String, Object> result = genericService.ejecutarSP(1, new HashMap<>(req));
        return result.get("resultado").toString();
    }
}
