package it.polito.tdp.denvercrimes.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento>{
	public enum TipoEvento {
		CRIMINE, 
		ARRIVA_AGENTE,
		GESTITO,
	}
	
	private TipoEvento tipo;
	private LocalDateTime data;
	private Event crimine;
	
	public Evento(TipoEvento tipo, LocalDateTime data, Event crimine) {
		super();
		this.tipo = tipo;
		this.data = data;
		this.crimine = crimine;
	}

	public TipoEvento getTipo() {
		return tipo;
	}

	public LocalDateTime getData() {
		return data;
	}

	public Event getCrimine() {
		return crimine;
	}
	
	public int compareTo(Evento other) {
		return this.data.compareTo(other.data);
	}
}
