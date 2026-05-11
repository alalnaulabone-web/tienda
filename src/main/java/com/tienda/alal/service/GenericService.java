package com.tienda.alal.service;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class GenericService {

    private final JdbcTemplate jdbcTemplate;

    public GenericService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> ejecutarSP(Integer spId, Map<String, Object> params) {
        if (spId == null) {
            throw new RuntimeException("El campo sp_id es obligatorio");
        }

        String sqlConfig = "SELECT nombre_sp, cantidad_parametros, param_orden, activo FROM sp_config WHERE id = ?";

        Map<String, Object> config;
        try {
            config = jdbcTemplate.queryForMap(sqlConfig, spId);
        } catch (Exception e) {
            throw new RuntimeException("Consulta no encontrada");
        }

        Boolean activo = Boolean.FALSE;
        Object activoObj = config.get("activo");
        if (activoObj instanceof Boolean) {
            activo = (Boolean) activoObj;
        } else if (activoObj != null) {
            activo = Boolean.parseBoolean(activoObj.toString());
        }

        if (!activo) {
            throw new RuntimeException("Consulta desactivada");
        }

        String nombreSP = config.get("nombre_sp").toString();
        validateStoredProcedureName(nombreSP);

        int cantidad;
        try {
            cantidad = ((Number) config.get("cantidad_parametros")).intValue();
        } catch (Exception e) {
            throw new RuntimeException("Configuración de cantidad_parametros inválida");
        }

        String orden = config.get("param_orden").toString();
        String[] campos = orden.split(",");

        if (campos.length != cantidad) {
            throw new RuntimeException("Configuración inconsistente: cantidad de parámetros no coincide");
        }

        if (params == null) {
            throw new RuntimeException("El campo params es obligatorio");
        }

        Object[] valores = new Object[campos.length];
        for (int i = 0; i < campos.length; i++) {
            String campo = campos[i].trim();
            if (campo.isEmpty()) {
                throw new RuntimeException("Configuración inconsistente: nombre de parámetro vacío");
            }
            if (!params.containsKey(campo)) {
                throw new RuntimeException("Falta parámetro: " + campo);
            }
            valores[i] = params.get(campo);
        }

        StringBuilder sql = new StringBuilder("CALL ").append(nombreSP).append("(");
        for (int i = 0; i < campos.length; i++) {
            sql.append("?");
            if (i < campos.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            return jdbcTemplate.queryForMap(sql.toString(), valores);
        } catch (Exception e) {
            throw new RuntimeException("Error interno al ejecutar el stored procedure");
        }
    }

    private void validateStoredProcedureName(String nombreSP) {
        if (nombreSP == null || !nombreSP.matches("^[a-zA-Z0-9_]+$")) {
            throw new RuntimeException("Nombre de stored procedure inválido en la configuración");
        }
    }
}
// 1234