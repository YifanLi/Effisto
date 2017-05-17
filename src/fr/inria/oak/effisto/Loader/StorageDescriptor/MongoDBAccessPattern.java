package fr.inria.oak.effisto.Loader.StorageDescriptor;

//import java.util.ArrayList;


public class MongoDBAccessPattern extends AccessPattern{
	/*
	public String host;
	public int port;
	public String nameOfdb;
	public String nameOfNewCollection;
	*/
	
	public String ap;
	public String ref;//the name of referred file, e.g. the file stored all the parsed json-tree patterns 
	
	public String getAccessPattern(String dc){
		//String AccessPattern=null;
		
		//...
		return this.ap+"@@"+this.ref;
	}
	

	
}
