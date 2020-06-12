package it.polito.tdp.denvercrimes.model;

public class Vicino implements Comparable<Vicino>{
	private Integer id;
	private Double distanzaMedia;
	
	public Vicino(Integer id, Double distanzaMedia) {
		super();
		this.id = id;
		this.distanzaMedia = distanzaMedia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getDistanzaMedia() {
		return distanzaMedia;
	}

	public void setDistanzaMedia(Double distanzaMedia) {
		this.distanzaMedia = distanzaMedia;
	}
	
	public int compareTo(Vicino other) {
		return this.distanzaMedia.compareTo(other.distanzaMedia);
	}
}
