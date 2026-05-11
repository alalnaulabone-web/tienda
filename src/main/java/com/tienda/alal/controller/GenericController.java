package com.tienda.alal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.alal.model.GenericRequest;
import com.tienda.alal.service.GenericService;

@RestController
@RequestMapping("/sp")
public class GenericController {

    private final GenericService service;

    public GenericController(GenericService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> ejecutar(@RequestBody GenericRequest req) {
        try {
            Map<String, Object> result = service.ejecutarSP(req.getSp_id(), req.getParams());
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "data", result
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "error", "Error interno"
            ));
        }
    }
}