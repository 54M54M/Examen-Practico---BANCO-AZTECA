package com.cajero.modelo;

import java.time.OffsetDateTime;

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
@Table(name = "TRANSACCIONES")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaccion {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "MONTO_SOLICITADO", nullable = false)
    private double montoSolicitado;
    @Column(name = "MONTO_ENTREGADO", nullable = false)
    private double montoEntregado;
    @Column(name = "FECHA_HORA", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT SYSTIMESTAMP")
    private OffsetDateTime fechaHora;
    @Column(name = "EXITOSO", nullable = false)
    private boolean exitoso;
    
}
