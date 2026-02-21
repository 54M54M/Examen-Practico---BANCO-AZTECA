package com.cajero.service;

import java.util.List;

import com.cajero.dto.CajeroDTO.Detalle;
import com.cajero.dto.CajeroDTO.Estado;
import com.cajero.dto.CajeroDTO.RetiroResponse;
import com.cajero.modelo.Denominacion;

public interface CajeroService {

	public List<Estado> getStatus();
	public double totalAvailable();
	
	public RetiroResponse processWhitdrawal(double monto);
	public RetiroResponse failedResponse(double monto, String mensaje);
	
	public void saveTransaction(double monto, List<Detalle> detalles, List<Denominacion> denominacions);
	
}
