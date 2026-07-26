package com.tienda.alal.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.tienda.alal.exception.SpExecutionException;
import com.tienda.alal.exception.SpNotFoundException;
import com.tienda.alal.exception.SpValidationException;

@Service
public class GenericService {

    // Palabras/clausulas bloqueadas en tipo_ejecucion='QUERY' ademas del filtro "solo SELECT",
    // como defensa adicional (p.ej. una subconsulta o comentario que intente colar una escritura).
    private static final Pattern PALABRAS_PROHIBIDAS_QUERY = Pattern.compile(
            "\\b(INSERT|UPDATE|DELETE|DROP|ALTER|CREATE|TRUNCATE|GRANT|REVOKE|EXEC|EXECUTE|CALL|REPLACE|MERGE|LOAD_FILE)\\b"
                    + "|INTO\\s+(OUTFILE|DUMPFILE)",
            Pattern.CASE_INSENSITIVE);

    private final JdbcTemplate jdbcTemplate;

    public GenericService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> ejecutarSP(Integer spId, Map<String, Object> params) {
        if (spId == null) {
            throw new SpValidationException("El campo sp_id es obligatorio");
        }

        if (params == null) {
            throw new SpValidationException("El campo params es obligatorio");
        }

        String sqlConfig = "SELECT sentencia_sql, cantidad_parametros, param_orden, activo, tipo_ejecucion FROM sp_config WHERE id = ?";

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

        String sentenciaSql = config.get("sentencia_sql").toString();
        if (sentenciaSql == null || sentenciaSql.isBlank()) {
            throw new SpValidationException("sentencia_sql inválida en la configuración");
        }

        Object tipoObj = config.get("tipo_ejecucion");
        String tipoEjecucion = tipoObj == null ? "SP" : tipoObj.toString();
        boolean esQuery = "QUERY".equalsIgnoreCase(tipoEjecucion);
        if (!esQuery && !"SP".equalsIgnoreCase(tipoEjecucion)) {
            throw new SpValidationException("tipo_ejecucion inválido en la configuración: " + tipoEjecucion);
        }

        if (!esQuery && !sentenciaSql.matches("^[a-zA-Z0-9_]+$")) {
            throw new SpValidationException("Nombre de stored procedure inválido en la configuración");
        }

        if (esQuery) {
            validarConsultaSegura(sentenciaSql);
        }

        int cantidad;
        try {
            cantidad = ((Number) config.get("cantidad_parametros")).intValue();
        } catch (Exception e) {
            throw new SpValidationException("Configuración de cantidad_parametros inválida");
        }

        Object ordenObj = config.get("param_orden");
        String orden = ordenObj == null ? "" : ordenObj.toString();
        String[] campos = orden.isBlank() ? new String[0] : orden.split(",");

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

        String sql;
        if (esQuery) {
            sql = sentenciaSql;
        } else {
            StringBuilder sb = new StringBuilder("CALL ").append(sentenciaSql).append("(");
            for (int i = 0; i < campos.length; i++) {
                sb.append("?");
                if (i < campos.length - 1) sb.append(",");
            }
            sb.append(")");
            sql = sb.toString();
        }

        try {
            return jdbcTemplate.queryForList(sql, valores);
        } catch (Exception e) {
            throw new SpExecutionException("Error al ejecutar el stored procedure", e);
        }
    }

    // Restringido a SELECT por decision explicita: las escrituras deben pasar por
    // tipo_ejecucion='SP', que es donde vive la logica transaccional. Si a futuro
    // se necesita permitir otro tipo de sentencias en QUERY, este es el punto a revisar.
    private void validarConsultaSegura(String sql) {
        String limpio = sql.trim();

        if (!limpio.regionMatches(true, 0, "SELECT", 0, 6)) {
            throw new SpValidationException("tipo_ejecucion='QUERY' solo admite sentencias SELECT");
        }
        if (limpio.contains(";")) {
            throw new SpValidationException("sentencia_sql no puede contener ';' (sentencias apiladas no permitidas)");
        }
        if (limpio.contains("--") || limpio.contains("/*")) {
            throw new SpValidationException("sentencia_sql no puede contener comentarios SQL");
        }
        if (PALABRAS_PROHIBIDAS_QUERY.matcher(limpio).find()) {
            throw new SpValidationException("sentencia_sql contiene una clausula no permitida en modo QUERY");
        }
    }
}
