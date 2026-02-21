package com.cajero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cajero.modelo.TransaccionDetalle;

@Repository
public interface TransaccionDetalleRepository extends JpaRepository<TransaccionDetalle, Integer>{

}
