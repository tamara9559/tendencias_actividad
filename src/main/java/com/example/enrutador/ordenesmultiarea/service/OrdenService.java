package com.example.enrutador.ordenesmultiarea.service;


import com.example.enrutador.ordenesmultiarea.dto.*;
import com.example.enrutador.ordenesmultiarea.entity.Orden;

import java.util.List;
import java.util.Optional;

public interface OrdenService {

    OrdenDTO createOrden(CreateOrdenDTO dto);

    List<OrdenDTO> listOrdenes(Optional<String> estado);

    OrdenDetalleDTO getOrden(Long id);

    OrdenDetalleDTO asignarAreas(Long ordenId, AsignacionesRequestDTO dto);

    OrdenAreaDTO updateAreaState(Long ordenId, Integer areaId, PatchAreaEstadoDTO dto);

    List<HistorialDTO> getHistorial(Long ordenId);

    void recalcularEstadoGlobal(Orden o);

    void ejecutarTick();
}

