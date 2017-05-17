package fr.inria.oak.effisto.Loader.StorageDescriptor;

import java.util.ArrayList;

//import ...

public abstract class AccessPattern{
	public String ap; 
	public String ref;//the name of referred file, e.g. the file stored all the parsed json-tree patterns 
	//...
	public String getAccessPattern(){
		//String AccessPattern = null;
		//...
		//return String AccessPattern (esp. for MongoDB);
		//or, return String[] (need override, e.g. for DynamoDB)
		return this.ap;
	}
}
