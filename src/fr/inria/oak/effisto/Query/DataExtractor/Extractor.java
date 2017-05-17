package fr.inria.oak.effisto.Query.DataExtractor;

import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptor;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreePattern;

public class Extractor {
	private QueryTreePattern qtp;
	
	/*
	 * to evaluate this query on a specific data file
	 */
	public Extractor(String dataFilePath, QueryTreePattern query){
		this.qtp = query;
		//
		//
		
	}
	
	/*
	 * to evaluate this query on a specific database(collection)
	 */
	public Extractor(StorageDescriptor sd, QueryTreePattern query){
		this.qtp = query;
		//
		//
		
	}
	

}
