package com.lucas.projetoid.repository;

import com.lucas.projetoid.model.EtiquetaErroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/* Repositorio de erro */

public interface EtiquetaErroRepository extends JpaRepository<EtiquetaErroEntity, Long> {

    Optional<EtiquetaErroEntity> findByCheckid(Integer checkid);

}
