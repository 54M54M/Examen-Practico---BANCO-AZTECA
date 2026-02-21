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
@Table(name = "TRANSACCION_DETALLES") 
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransaccionDetalle {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "CANTIDAD_USADA", nullable = false)
    private int cantidadUsada;
    @Column(name = "ID_DENOMINACIONES", nullable = false)
    private int idDenominacion;
    @Column(name = "ID_TRANSACCIONES", nullable = false)
    private int idTransaccion;
    
}
