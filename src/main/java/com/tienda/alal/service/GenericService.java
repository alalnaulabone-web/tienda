package com.tienda.alal.service;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.tienda.alal.exception.SpExecutionException;
import com.tienda.alal.exception.SpNotFoundException;
import com.tienda.alal.exception.SpValidationException;

@Service
public class GenericService {

    private final JdbcTemplate jdbcTemplate;

    public GenericService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> ejecutarSP(Integer spId, Map<String, Object> params) {
        if (spId == null) {
            throw new SpValidationException("El campo sp_id es obligatorio");
        }

        if (params == null) {
            throw new SpValidationException("El campo params es obligatorio");
        }

        String sqlConfig = "SELECT nombre_sp, cantidad_parametros, param_orden, activo FROM sp_config WHERE id = ?";

        Map<String, Object> config;
        try {
            config = jdbcTemplate.queryForMap(sqlConfig, spId);
        } catch (Exception e) {
            throw new SpNotFoundException("Consulta no encontrada con id: " + spId);
        }

        boolean activo = false;
        Object activoObj = config.get("activo");
        if (activoObj instanceof Boolean b) {
            activo = b;
        } else if (activoObj != null) {
            activo = Boolean.parseBoolean(String.valueOf(activoObj));
        }

        if (!activo) {
            throw new SpValidationException("Consulta desactivada");
        }

        String nombreSP = config.get("nombre_sp").toString();
        if (nombreSP == null || !nombreSP.matches("^[a-zA-Z0-9_]+$")) {
            throw new SpValidationException("Nombre de stored procedure inválido en la configuración");
        }

        int cantidad;
        try {
            cantidad = ((Number) config.get("cantidad_parametros")).intValue();
        } catch (Exception e) {
            throw new SpValidationException("Configuración de cantidad_parametros inválida");
        }

        String orden = config.get("param_orden").toString();
        String[] campos = orden.split(",");

        if (campos.length != cantidad) {
            throw new SpValidationException("Configuración inconsistente: cantidad de parámetros no coincide");
        }

        Object[] valores = new Object[campos.length];
        for (int i = 0; i < campos.length; i++) {
            String campo = campos[i].trim();
            if (campo.isEmpty()) {
                throw new SpValidationException("Configuración inconsistente: nombre de parámetro vacío");
            }
            if (!params.containsKey(campo)) {
                throw new SpValidationException("Falta parámetro: " + campo);
            }
            valores[i] = params.get(campo);
        }

        StringBuilder sql = new StringBuilder("CALL ").append(nombreSP).append("(");
        for (int i = 0; i < campos.length; i++) {
            sql.append("?");
            if (i < campos.length - 1) sql.append(",");
        }
        sql.append(")");

        try {
            return jdbcTemplate.queryForMap(sql.toString(), valores);
        } catch (Exception e) {
            throw new SpExecutionException("Error al ejecutar el stored procedure", e);
        }
    }
}
