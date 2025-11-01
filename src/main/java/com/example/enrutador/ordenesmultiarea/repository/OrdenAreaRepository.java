package com.example.enrutador.ordenesmultiarea.repository;


import com.example.enrutador.ordenesmultiarea.entity.OrdenArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface OrdenAreaRepository extends JpaRepository<OrdenArea, Long> {
    Optional<OrdenArea> findByOrdenIdAndAreaId(Long ordenId, Integer areaId);
    List<OrdenArea> findByOrdenId(Long ordenId);

    List<OrdenArea> findByEstadoParcialIn(List<String> enProgress);
}

