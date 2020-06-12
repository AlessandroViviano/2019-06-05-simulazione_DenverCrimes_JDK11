package it.polito.tdp.denvercrimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.denvercrimes.db.EventsDAO;
import it.polito.tdp.denvercrimes.model.Evento.TipoEvento;

public class Simulator {
	//TIPI DI EVENTO
	//1. Evento criminoso
	// 1.1 La centrale seleziona l'agente libero più vicino
	// 1.2 Se non ci sono disponibilità -> Crimine MAL GESTITO
	// 1.3 Se c'è un agente libero -> Setto l'agente a occupato
	
	//2. L'agente selzionato ARRIVA sul posto
	// 2.1 Definisco quanto durerà l'intervento
	// 2.2 Controllo se il crimine è mal gestito (ritardo dell'agente)
	
	//3. Il crimine è TERMINATO
	// 3.1 "Libero" l'agente, che torna ad essere disponibile
	
	//Strutture dati che ci servono
	// Input utente
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	// Stato del sistema
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private Map<Integer, Integer> agenti; //Mappa<distretto, numeroAgentiLiberi> agenti
	// Coda degli eventi
	private PriorityQueue<Evento> queue;
	// Output
	private Integer malGestiti;
	
	public void init(Integer N, Integer anno, Integer mese, Integer giorno, Graph<Integer, DefaultWeightedEdge> grafo) {
		this.N = N;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;
		this.grafo = grafo;
		
		this.malGestiti = 0;
		this.agenti = new HashMap<>();
		for(Integer d: this.grafo.vertexSet()) {
			this.agenti.put(d, 0);
		}
		//Devo scegliere dov'è la centrale (e mettere N agenti in quel distretto)
		EventsDAO dao = new EventsDAO() ;
		Integer minD = dao.getDistrettoMin(anno);
		this.agenti.put(minD, N);
			
		//Creo e inizializzo la coda
		this.queue = new PriorityQueue<>();
		
		for(Event e: dao.listAllEventsByDate(anno, mese, giorno)) {
			this.queue.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(), e));
		}
	}
	
	public int run() {
		Evento e;
		while((e = queue.poll()) != null){
			switch(e.getTipo()) {
			case CRIMINE:
				System.out.println("NUOVE CRIMINE! "+e.getCrimine().getIncident_id());
				//Cerco l'agente libero più VICINO
				Integer partenza = null;
				partenza = cercaAgente(e.getCrimine().getDistrict_id());
				if(partenza != null) {
					//C'è un agente libero in partenza
					//setto l'agente come occupato
					this.agenti.put(partenza, this.agenti.get(partenza-1));
					//Cerco di capire quanto ci metterà l'agente libero ad arrivare sul posto
					Double distanza;
					if(partenza.equals(e.getCrimine().getDistrict_id())) {
						distanza = 0.0;
					}else {
						distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, e.getCrimine().getDistrict_id()));
						Long seconds = (long) ((distanza * 1000)/(60/3.6));
						this.queue.add(new Evento(TipoEvento.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getCrimine()));
						
					}
				}else {
					//Non c'è un agente libero al momento -> crimine MAL GESTITO
					System.out.println("CRIMINE "+e.getCrimine().getIncident_id()+" MAL GESTITO!");
					this.malGestiti++;
				}
				break;
			case ARRIVA_AGENTE:
				System.out.println("ARRIVA AGENTE PER CRIMINE! "+e.getCrimine().getIncident_id());
				Long duration = getDurata(e.getCrimine().getOffense_category_id());
				this.queue.add(new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));
				//Controllare se il crimine è mal gestito
				if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
					System.out.println("ARRIVA AGENTE PER CRIMINE! "+e.getCrimine().getIncident_id());
					this.malGestiti++;
				}
				break;
			case GESTITO:
				System.out.println("CRIMINE "+e.getCrimine().getIncident_id()+" GESTITO");
				this.agenti.put(e.getCrimine().getDistrict_id(),  this.agenti.get(e.getCrimine().getDistrict_id())+1);
				break;
			}
		}
		
		
		return this.malGestiti;
	}

	private Long getDurata(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5)
				return Long.valueOf(2*60*60);
			else
				return Long.valueOf(1*60*60);
		}else {
			return Long.valueOf(2*60*60);
		}
	}

	private Integer cercaAgente(Integer district_id) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;
		
		for(Integer d: this.agenti.keySet()) {
			if(this.agenti.get(d) > 0) {
				if(district_id.equals(d)) {
					distanza = 0.0;
					distretto = d;
					}else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
						distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
						distretto = d;
					}
				}
			}
		
		return distretto;
	}
	
}
