package com.cajero.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cajero.dto.CajeroDTO.Estado;
import com.cajero.dto.CajeroDTO.RetiroRequest;
import com.cajero.dto.CajeroDTO.RetiroResponse;
import com.cajero.service.CajeroService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping({"/api/cajero"})
@CrossOrigin(origins = "http://localhost:4200")
public class CajeroController {

	private final CajeroService service;

	public CajeroController(CajeroService service) {
		super();
		this.service = service;
	}
	
	// GET :: http://localhost:7575/api/cajero/estado
	// DEVUELVE EL INVENTARIO ACTUAL DE MONEDAS Y BILLETES
	@GetMapping("/estado")
	public ResponseEntity<List<Estado>> getEstado() {
		return ResponseEntity.ok(service.getStatus());
	}
	
	// GET :: http://localhost:7575/api/cajero/total
	// DEVUELVE EL DINERO TOTAL EN EL CAJERO
	@GetMapping("/total")
	public ResponseEntity<Map<String, Double>> getTotal() {
		return ResponseEntity
				.ok(
					Map.of("totalDisponible", 
							service.totalAvailable()));
	}
	
	// POST :: http://localhost:7575/api/cajero/retirar
	// BODY :: RAW :: { "monto": 500.00 }
	@PostMapping("/retirar")
	public ResponseEntity<RetiroResponse> retirar(@Valid @RequestBody RetiroRequest request) {
		RetiroResponse response = service.processWhitdrawal(request.getMonto());
		
		if (!response.isExitoso()) {
			return ResponseEntity.unprocessableEntity().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	
}








