package com.example.enrutador.ordenesmultiarea.repository;


import com.example.enrutador.ordenesmultiarea.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Integer> {
    Optional<Area> findByNombre(String nombre);
}

