package com.tienda.alal.model;

public class ApiResponse {

    private int status;
    private String mensaje;

    public ApiResponse() {
    }

    public ApiResponse(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}