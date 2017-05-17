package fr.inria.oak.effisto.Loader.StorageDescriptor;

import java.util.ArrayList;

//import ...

public abstract class DistributionPattern{
	public String dp; 
	//...
	public String getDistributionPattern(){
		//String DistributionPattern = null;
		//...
		//return String DistributionPattern (esp. for MongoDB, "_id#name");
		return this.dp;
	}
}
