package com.cajero.dto;

import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CajeroDTO<T> {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	// SOLICITUD DE RETIRO
	public static class RetiroRequest {
		@NotNull(message = "El monto a retirar es obligatorio")
		@DecimalMin(value = "0.50", message = "El moonto minimo es de $0.50")
		private double monto;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	// ITEM DE DENOMINACION [ MONEDA - BILLETE ]
	public static class Detalle {
		private String tipo;
		private double denominacion;
		private int cantidad;
		private double subtotal;		
		/*
		 {
            "tipo": "Billete",
            "denominacion": 1000.0,
            "cantidad": 1,
            "subtotal": 1000.0
         } 
		 */
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	// RESPUESTA DE RETIRO
	public static class RetiroResponse {
		private boolean exitoso;
		private String mensaje;
		private double montoSolicitado;
		private double montoEntregado;
		private List<Detalle> detalles;
		/*
		 {
		    "exitoso": true,
		    "mensaje": "Retiro exitoso.",
		    "montoSolicitado": 1500.0,
		    "montoEntregado": 1500.0,
		    "detalles": [ { 'public static class Detalle {...}' } ]
		 } 
		*/
	}
	

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	// ESTADO DEL CAJERO
	public static class Estado {
		private int id;
		private String tipo;
		private double denominacion;
		private int cantidad;
		private double valorTotal;
	}

}
