package com.example.enrutador.ordenesmultiarea.repository;


import com.example.enrutador.ordenesmultiarea.entity.Historial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialRepository extends JpaRepository<Historial, Long> {
    List<Historial> findByOrdenIdOrderByTimestampDesc(Long ordenId);
    List<Historial> findByOrdenIdOrderByTimestampAsc(Long ordenId);

}

