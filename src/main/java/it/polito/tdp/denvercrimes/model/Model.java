package it.polito.tdp.denvercrimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.denvercrimes.db.EventsDAO;

public class Model {
	
	private EventsDAO dao;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Integer> distretti;
	
	public Model() {
		dao = new EventsDAO();
	}
	
	public List<Integer> getAnni(){
		List<Integer> anni = dao.getAnni();
		Collections.sort(anni);
	    return anni;
	}
	
	public void creaGrafo(Integer anno) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		distretti = dao.getDistretti();
		
		//Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, distretti);
		
		//Aggiungo gli archi
		for(Integer d1: this.grafo.vertexSet()) {
			for(Integer d2: this.grafo.vertexSet()) {
				if(!d1.equals(d2)) {
					if(this.grafo.getEdge(d1, d2) == null) {
						Double latMedia1 = dao.getLatitudineMedia(d1, anno);
						Double lonMedia1 = dao.getLongitudineMedia(d1, anno);
						
						Double latMedia2 = dao.getLatitudineMedia(d2, anno);
						Double lonMedia2 = dao.getLongitudineMedia(d2, anno);
						
						Double distanzaMedia = LatLngTool.distance(new LatLng(latMedia1, lonMedia1), new LatLng(latMedia2, lonMedia2), LengthUnit.KILOMETER);
				        Graphs.addEdge(this.grafo, d1, d2, distanzaMedia);
					}
				}
			}
		}
		System.out.println("grafo creato con "+this.grafo.vertexSet().size()+" vertici e "+this.grafo.edgeSet().size()+" archi\n");
	}
	
	public int vertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int archi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Integer> getDistretti(){
		List<Integer> distretti = dao.getDistretti();
		Collections.sort(distretti);
		return distretti;
	}
	
	public List<Vicino> trovaVicini(Integer distretto){
		List<Vicino> vicini = new ArrayList<>();
		for(DefaultWeightedEdge d: this.grafo.outgoingEdgesOf(distretto)) {
			if(distretto == this.grafo.getEdgeSource(d)) {
				Vicino v = new Vicino(this.grafo.getEdgeTarget(d), this.grafo.getEdgeWeight(d));
				vicini.add(v);
			}
			if(distretto == this.grafo.getEdgeTarget(d)) {
				Vicino v = new Vicino(this.grafo.getEdgeSource(d), this.grafo.getEdgeWeight(d));
				vicini.add(v);
			}
		}
		Collections.sort(vicini);
		return vicini;
	}
	
	public int simula(Integer anno, Integer mese, Integer giorno, Integer N) {
		Simulator sim = new Simulator();
		sim.init(N, anno, mese, giorno, grafo);
		return sim.run();
	}
	
	public List<Integer> getMesi(){
		List<Integer> mesi = dao.getMesi();
		Collections.sort(mesi);
		return mesi;
	}
	
	public List<Integer> getGiorni(){
		List<Integer> giorni = dao.getGiorni();
		Collections.sort(giorni);
		return giorni;
	}
}
