package com.cajero.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cajero.dto.CajeroDTO;
import com.cajero.dto.CajeroDTO.Detalle;
import com.cajero.dto.CajeroDTO.Estado;
import com.cajero.dto.CajeroDTO.RetiroResponse;
import com.cajero.modelo.Denominacion;
import com.cajero.modelo.Transaccion;
import com.cajero.modelo.TransaccionDetalle;
import com.cajero.repository.DenominacionRepository;
import com.cajero.repository.TransaccionDetalleRepository;
import com.cajero.repository.TransaccionRepository;

@Service
public class CajeroSrrviceImpl implements CajeroService {

	// INYECCION DE DEPENDENCIAS POR REFERENCIA AL CONSTRUCTOR
    private final DenominacionRepository denomiRepository;
    private final TransaccionRepository transRepository;
    private final TransaccionDetalleRepository detalleRepository;

	public CajeroSrrviceImpl(DenominacionRepository denomiRepository, TransaccionRepository transRepository,
			TransaccionDetalleRepository detalleRepository) {
		super();
		this.denomiRepository = denomiRepository;
		this.transRepository = transRepository;
		this.detalleRepository = detalleRepository;
	}

	@Override
	public List<Estado> getStatus() {
		return denomiRepository.findAllOrdenadas()
				.stream()
				.map(d -> new CajeroDTO.Estado(
						d.getId(),
						d.getTipo(),
						d.getValor(),
						d.getCantidad(),
						d.getValor() * d.getCantidad()
						))
				.toList();
	}


	@Override
	public RetiroResponse failedResponse(double monto, String mensaje) {
		 return new RetiroResponse(false, mensaje, monto, 0.0, List.of());
	}
	
	@Override
	public double totalAvailable() {
		return denomiRepository.findAllOrdenadas()
				.stream()
				.mapToDouble(d -> d.getValor() * d.getCantidad())
				.sum();
	}

	@Override
	@Transactional
	public RetiroResponse processWhitdrawal(double monto) {
		// ANTES DE HACER EL PROCESO SE VERIFICA SI ES UN VALOR ACEPTABLE
		if (monto <= 0) {
			return failedResponse(monto, "El monto debe ser mayor a cero.");
		}
		if (monto > totalAvailable()) {
			return failedResponse(monto, "Fondos insuficientes en el cajero.");
		}

	    // SE INSTANCIAN VARIABLES PARA EL MANEJO DE DATOS
	    List<Denominacion> denominaciones = denomiRepository.findAllOrdenadas();
	    List<Detalle> detalles = new ArrayList<>();
	    List<Denominacion> modificadas = new ArrayList<>();

	    double restante = monto;

	    // SE RECORRE LA TABLA 'DENOMINACION'
	    for (Denominacion d : denominaciones) {
	    	
	        if (restante < 0.005) break;
	        if (d.getCantidad() <= 0) continue;

	        int necesarias = (int) (restante / d.getValor());
	        int usar = Math.min(necesarias, d.getCantidad());

	        if (usar > 0) {
	            double subtotal = Math.round(usar * d.getValor() * 100.0) / 100.0;
	            restante = Math.round((restante - subtotal) * 100.0) / 100.0;

	            detalles.add(new Detalle(d.getTipo(), d.getValor(), usar, subtotal));

	            d.setCantidad(d.getCantidad() - usar);
	            modificadas.add(d);
	        }
	    }

	    // SI NO SE PUDO DISPENSAR EL MONTO EXACTO, RETORNA ERROR
	    if (restante > 0.005) {
	        return failedResponse(monto,
	                "No es posible dispensar el monto exacto con las denominaciones disponibles.");
	    }

	    // ACTUALIZAMOS EL INVENTARIO Y GUARDAMOS LOS CAMBIOS
	    denomiRepository.saveAll(modificadas);
	    saveTransaction(monto, detalles, modificadas);

	    return new RetiroResponse(true, "Retiro exitoso.", monto, monto, detalles);
	}


	@Override
	public void saveTransaction(double monto, List<Detalle> detalles, List<Denominacion> denominaciones) {

	    // SE GUARDA EL HAEDRR DE LAS TRANSACCIONES
	    Transaccion tx = new Transaccion();
	    tx.setMontoSolicitado(monto);
	    tx.setMontoEntregado(monto);
	    tx.setFechaHora(OffsetDateTime.now());
	    tx.setExitoso(true);
	    Transaccion txGuardada = transRepository.save(tx);

	    // SE GUARDA EL DETALLE POR CADA DENOMINACION USADA
	    List<TransaccionDetalle> txDetalles = new ArrayList<>();

	    for (Detalle det : detalles) {
	        // BUSCAMOS EL ID DE LA DENOMINACION POR SU VALOR
	        int idDenom = denominaciones.stream()
	                .filter(d -> d.getValor() == det.getDenominacion())
	                .findFirst()
	                .map(Denominacion::getId)
	                .orElse(0);

	        TransaccionDetalle td = new TransaccionDetalle();
	        td.setCantidadUsada(det.getCantidad());
	        td.setIdDenominacion(idDenom);
	        td.setIdTransaccion(txGuardada.getId());
	        txDetalles.add(td);
	    }

	    detalleRepository.saveAll(txDetalles);
	}

    
}
