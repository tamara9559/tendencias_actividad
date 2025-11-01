package com.example.enrutador.ordenesmultiarea.controller;

import com.example.enrutador.ordenesmultiarea.dto.*;
import com.example.enrutador.ordenesmultiarea.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    public ResponseEntity<OrdenDTO> createOrden(@Valid @RequestBody CreateOrdenDTO dto) {
        OrdenDTO created = ordenService.createOrden(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<OrdenDTO>> listOrdenes(@RequestParam Optional<String> estado) {
        return ResponseEntity.ok(ordenService.listOrdenes(estado));
    }



    @GetMapping("/{id}")
    public ResponseEntity<OrdenDetalleDTO> getOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.getOrden(id));
    }

    @PostMapping("/{id}/asignaciones")
    public ResponseEntity<OrdenDetalleDTO> asignarAreas(@PathVariable Long id,
                                                        @Valid @RequestBody AsignacionesRequestDTO dto) {
        return ResponseEntity.ok(ordenService.asignarAreas(id, dto));
    }

    @PatchMapping("/{id}/areas/{areaId}")
    public ResponseEntity<OrdenAreaDTO> patchArea(@PathVariable Long id,
                                                  @PathVariable Integer areaId,
                                                  @Valid @RequestBody PatchAreaEstadoDTO dto) {
        return ResponseEntity.ok(ordenService.updateAreaState(id, areaId, dto));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialDTO>> getHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.getHistorial(id));
    }

}

