package com.example.enrutador.ordenesmultiarea.evolution.sla.scheduler;


import com.example.enrutador.ordenesmultiarea.service.OrdenAreaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class SchedulerTick {

    @Value("${app.tick.interval.segundos:10}")
    private int intervalo;

    private final OrdenAreaService ordenAreaService;

    public SchedulerTick(OrdenAreaService ordenAreaService) {
        this.ordenAreaService = ordenAreaService;
    }

    // Se ejecuta automáticamente cada N_SEG milisegundos
    @Scheduled(fixedRateString = "${app.tick.interval.milisegundos:10000}")
    @Transactional
    public void ejecutarTick() {
        ordenAreaService.ejecutarTick();
    }
}
