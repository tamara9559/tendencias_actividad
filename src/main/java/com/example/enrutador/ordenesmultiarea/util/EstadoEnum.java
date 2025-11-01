package com.example.enrutador.ordenesmultiarea.util;

public final class EstadoEnum {

    public enum EstadoGlobal {
        NUEVA,
        ASIGNADA,
        EN_PROGRESO,
        PENDIENTE,
        COMPLETADA,
        CERRADA_SIN_SOLUCION,
        VENCIDA
    }

    public enum EstadoParcial {
        NUEVA,
        ASIGNADA,
        EN_PROGRESO,
        PENDIENTE,
        COMPLETADA,
        CERRADA_SIN_SOLUCION,
        VENCIDA
    }

    private EstadoEnum() {}
}

