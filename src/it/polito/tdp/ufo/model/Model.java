package it.polito.tdp.ufo.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.ufo.db.SightingsDAO;

public class Model {
	
	private List<AnnoCount> anniAvvistamenti;
	private List<String> stati;
	private Graph<String, DefaultEdge> graph;
	
	public List<AnnoCount> getAnniAvvistamenti(){
		SightingsDAO dao = new SightingsDAO();
		
		this.anniAvvistamenti = dao.getAnni();
		return this.anniAvvistamenti;
	}

	public void creaGrafo(Year anno) {
		this.graph = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		SightingsDAO dao = new SightingsDAO();
		this.stati = dao.getStati(anno);
		
		Graphs.addAllVertices(this.graph, stati);
		
		for(String stato1: this.graph.vertexSet()) {
			for(String stato2: this.graph.vertexSet()) {
				if(!stato1.equals(stato2)) {
					if(dao.esisteArco(stato1, stato2, anno)) {
						this.graph.addEdge(stato1, stato2);
					}
				}
			}
		}
		// VEDI ALTERNATIVA CON QUERI.. SU GITHUB (ULTIMI MINUTI LEZIONE 12.06.2018)
		System.out.println(this.graph.vertexSet().size()+" "+ this.graph.edgeSet().size());
	}
	
	public List<String> getStatiPrecendenti(String stato) {
		//this.graph.incomingEdgesOf(stato); // elenco di archi -> li seleziono a uno a uno e vedo i vertici 
		return Graphs.predecessorListOf(this.graph, stato);
		
	}
	
	public List<String> getStatiSuccesivi(String stato) {
		return Graphs.successorListOf(this.graph, stato);
	}
	
	public List<String> getStatiRaggiungibili(String stato) {
		// devo creare un visitatore del grafo
		BreadthFirstIterator<String, DefaultEdge> bfv = new BreadthFirstIterator<String, DefaultEdge>(this.graph, stato);
		
		List<String> raggiungibili = new ArrayList<String>();
		bfv.next(); // non voglio salvare il primo elemento (perche sarebbe se stesso).
		while(bfv.hasNext()) {
			 raggiungibili.add(bfv.next());
		}
		return raggiungibili;
	}
	
	public List<String> getStati(){
		return this.stati;
	}
}
