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
	
	private List<String> ottima;
	
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
	
	// SOLUZIONE PUNTO 2 
	//caso finale (ricorsione)
	public List<String> getPercorsoMassimo(String partenza) {
		this.ottima = new ArrayList<String>();
		List<String> parziale = new ArrayList<>();
		parziale.add(partenza);
		
		cercaSequenza(parziale);
		
		return this.ottima;
	}
	
	//caso medio(ricorsione)
	private void cercaSequenza(List<String> parziale) {
		
		// caso terminale
		if(parziale.size() > this.ottima.size()) {
			this.ottima = new ArrayList<String>(parziale); // deep copy
		}
		
		// ricorsione
		List<String> candidati = this.getStatiSuccesivi(parziale.get(parziale.size()-1)); // i candidati sono tutti i successivi all'ultimo elemento della lista parziale
		for(String prova : candidati) {
			if(!parziale.contains(prova)) { // se ho problemi di efficienza sostituisco a list -> set (candidati)
				// provo ad aggiungerlo
				parziale.add(prova);
				this.cercaSequenza(parziale);
				parziale.remove(parziale.size()-1);
			}
		}
	
	}
	
	
	
	public List<String> getStati(){
		return this.stati;
	}
}
