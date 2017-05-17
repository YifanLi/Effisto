package fr.inria.oak.effisto.Query.DataExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Tuples {
	
	/*
	 * here, we extracted those data(String) needed and put them into an Array(as a column),
	 * the key of this map is the node_tag
	 */
	private HashMap<String, ArrayList<String>> TuplesMap = new HashMap<String, ArrayList<String>>();
	
	public Tuples(){
		
	}
	
	public void addColumn(String key){
		this.TuplesMap.put(key, new ArrayList<String>());
		
	}
	
	public void addRecord(String key, String value){
		this.TuplesMap.get(key).add(value);
		
	}
	
	
	

}
