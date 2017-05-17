package fr.inria.oak.effisto.Loader.StorageHandler;


import java.util.ArrayList;

import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptor;

public abstract class Handler{
	private ArrayList DataCollection;
	private String AccessAndDistributionPattern;;

	public StorageDescriptor load(ArrayList DataCollection, String AccessAndDistributionPattern){
		//StorageDescriptor sd = new StorageDescriptor(null, null);
		// private String[] StorageDescriptors;
		//...
		// 1) load data set
		// 2) load StorageDescriptor
		return null;
	}
	
	public ArrayList scan(StorageDescriptor StorageDescriptor){
		ArrayList DataCollection = null;
		//...
		return DataCollection; 
	}

	
}
