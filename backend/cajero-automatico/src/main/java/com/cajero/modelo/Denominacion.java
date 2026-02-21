package com.cajero.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DENOMINACIONES")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Denominacion {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "TIPO", nullable = false, length = 10)
    private String tipo;
    @Column(name = "CANTIDAD", nullable = false)
    private int cantidad;
    @Column(name = "VALOR", nullable = false)
    private double valor;
    @Column(name = "ACTIVO", nullable = false)
    private boolean activo;

}