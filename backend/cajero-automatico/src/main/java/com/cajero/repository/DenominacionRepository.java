package com.cajero.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cajero.modelo.Denominacion;

@Repository
public interface DenominacionRepository extends JpaRepository<Denominacion, Integer> {

    @Query("SELECT d FROM Denominacion d WHERE d.activo = true ORDER BY d.valor DESC")
    List<Denominacion> findAllOrdenadas();
}