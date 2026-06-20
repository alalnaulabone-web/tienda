package com.tienda.alal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.alal.model.ApiResponse;
import com.tienda.alal.service.TiendaService;

@RestController
@RequestMapping("/tienda")
public class TiendaController {

    private final TiendaService service;

    public TiendaController(TiendaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> insertar(@RequestBody Map<String, Object> req) {
        String resultado = service.insertarTienda(req);
        return ResponseEntity.ok(new ApiResponse(200, resultado));
    }
}
