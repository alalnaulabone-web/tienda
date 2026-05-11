package com.tienda.alal.model;

import lombok.Data;

@Data
public class TiendaRequest {
    private String nombre;
    private String codigo;
    private String logo;
    private String propietario;
    private String cedula;
    private String ubicacion;
    private String comentario;
}