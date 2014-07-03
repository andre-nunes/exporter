package com.bio4j.exporter;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.groovy.Gremlin;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.util.iterators.SingleIterator;

import java.util.concurrent.Callable;

public class QueryComputer implements Callable<Graph> {
	private String query;
	private TitanGraph graph;

	public QueryComputer(String cmd, TitanGraph g) {
		this.query = cmd;
		this.graph = g;
	}
	
	@Override
	public Graph call() throws Exception {
		//TODO create a graph for return
		if(this.query.startsWith("g.V().")){
			String translatedQuery = this.query.substring(6); // discard g.V()
			Pipe pipe = Gremlin.compile("_()." + translatedQuery); // create pipe for iteration
			pipe.setStarts(this.graph.getVertices());
				for(Object name : pipe) {
					System.out.println("----------------------------------------------------");
					System.out.println(name);
					System.out.println("----------------------------------------------------");
				}			
		}
		if(this.query.startsWith("g.v(")){
			// get the index number given
			String[] indexInString = this.query.split("\\("); 
			indexInString = indexInString[1].split("\\)"); // get the number between parentesis
			int index = Integer.parseInt(indexInString[0]); // number should be in the vector
			
			String translatedQuery = this.query.substring(4); // discard 'g.v('
			translatedQuery = translatedQuery.substring(indexInString[0].length() + 1); // discard the number and ')'
			
			if(translatedQuery.equals("")){
				//TODO IMPROVE QUERY HANDLING
				return null;
			}
		    		    
		    Pipe pipe = Gremlin.compile("_()." + translatedQuery) ;
		    pipe.setStarts(new SingleIterator<Vertex>(this.graph.getVertex(index))); // create pipe for iteration
		    for(Object name : pipe) {
		    	System.out.println("----------------------------------------------------");
				System.out.println((String) name);
				System.out.println("----------------------------------------------------");
		    }		
		}
		return null;
	}
}