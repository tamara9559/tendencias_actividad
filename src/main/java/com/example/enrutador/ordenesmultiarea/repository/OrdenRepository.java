package com.example.enrutador.ordenesmultiarea.repository;

import com.example.enrutador.ordenesmultiarea.entity.Orden;
import com.example.enrutador.ordenesmultiarea.util.EstadoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByEstadoGlobal(EstadoEnum.EstadoGlobal estadoGlobal);
}


