package com.bio4j.exporter;

import java.util.concurrent.TimeUnit;
import com.tinkerpop.blueprints.Graph;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.google.common.util.concurrent.*;

//holds business logic behind the exporter
public class ExporterCore {
	private static final long TIME_LIMIT_IN_SECONDS = 60;
	private String outputFormat;
	private int maxNumberOfResults;
	private long maxTime = TIME_LIMIT_IN_SECONDS;
	private boolean stream;
	private String source;
	private String query;
	private TitanGraph graph;

	private final String[] supportedFormats = { "gexf", "graphml", "graphson" };

	public ExporterCore() {
	}

	public String getFormat() {
		return this.outputFormat;
	}

	public void setFormat(String format) throws Exception {
		this.outputFormat = null;
		String lowercaseFormat = format.toLowerCase();
		// check whether the format is supported or not
		for (String supported : supportedFormats) {
			if (lowercaseFormat.equals(supported)) {
				this.outputFormat = lowercaseFormat;
				break;
			}
		}
		if (this.outputFormat == null) {
			throw new Exception("Format not supported: " + format);
		}
	}

	public int getMaxNumberOfResults() {
		return this.maxNumberOfResults;
	}

	public void setMaxNumberOfResults(int limit) {
		this.maxNumberOfResults = limit;
	}

	public void setMaxNumberOfResults(String limit) {
		setMaxNumberOfResults(Integer.parseInt(limit));
	}

	public long getMaxTime() {
		return this.maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public void setMaxTime(String maxTime) {
		setMaxTime(Integer.parseInt(maxTime));
	}

	public boolean isStream() {
		return this.stream;
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}

	public void setStream(String stream) throws Exception {
		if(stream == null){
			throw new Exception("Stream flag can't be null");
		}
		String lowercaseStream = stream.toLowerCase();
		if (lowercaseStream.equals("yes")) {
			this.stream = true;
		} else if (lowercaseStream.equals("no")) {
			this.stream = false;
		} else {
			throw new Exception("Stream flag requires 'yes' or 'no'");
		}
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) throws Exception {
		if(source == null){
			throw new Exception("Source can't be null");
		} 
		this.source = source;
		this.graph = loadTitanGraph(source);		
	}

	private TitanGraph loadTitanGraph(String source) {
		return TitanFactory.open(source);
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		if(query == null){
			this.query = null;
			return;
		}
		query = query.replace("[", "(");
		query = query.replace("]", ")");
		this.query = query;		
	}

	public void runQuery() throws Exception {
		
		QueryComputer qc = new QueryComputer(this.query, this.graph);
		TimeLimiter limiter = new SimpleTimeLimiter();
		try{
			Graph result = limiter.callWithTimeout(qc, this.maxTime, TimeUnit.SECONDS, false);	
			return;
		} catch(Exception e){
			if(e instanceof UncheckedTimeoutException){
				System.out.println("Time limit exceeded");
				return;
			}
			System.out.println("Error running query");
			throw e;
		}
	}

	public Graph getGraph() {
		return this.graph;
	}

	public void shutdownGraph() throws Exception {
		if(this.graph instanceof TitanGraph){
			this.graph.shutdown();			
		} else{
			throw new Exception("Graph does not exist");
		}			
	}		
}	

