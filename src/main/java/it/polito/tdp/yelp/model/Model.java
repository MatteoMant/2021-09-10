package it.polito.tdp.yelp.model;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	
	public Model() {
		dao = new YelpDao();
	}
	
	public void creaGrafo(String city) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, this.dao.getAllBusinessWithCity(city));
		
		// Aggiunta degli archi
		for (Business b1 : this.grafo.vertexSet()) {
			for (Business b2 : this.grafo.vertexSet()) {
				if (!b1.equals(b2)) { 
					
					LatLng punto1 = new LatLng(b1.getLatitude(), b1.getLongitude());
					LatLng punto2 = new LatLng(b2.getLatitude(), b2.getLongitude());
					
					double peso = LatLngTool.distance(punto1, punto2, LengthUnit.KILOMETER);
					Graphs.addEdge(this.grafo, b1, b2, peso);
				}
			}
		}
		
	}
	
	public Business calcolaLocalePiuDistante(Business locale) {
		double max = 0.0;
		Business localePiuDistante = null;
		
		for (DefaultWeightedEdge e : this.grafo.edgesOf(locale)) {
			if (this.grafo.getEdgeWeight(e) > max) {
				max = this.grafo.getEdgeWeight(e);
				localePiuDistante = Graphs.getOppositeVertex(this.grafo, e, locale);
			}
		}
		return localePiuDistante;
	}
	
	public Double getDistanzaLocale(Business locale1, Business locale2) {
		return this.grafo.getEdgeWeight(this.grafo.getEdge(locale1, locale2));
	}
	
	public Set<Business> getVertici(){
		return this.grafo.vertexSet();
	}
	
	public List<String> getAllCitta(){
		return this.dao.getAllCitta();
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
}

