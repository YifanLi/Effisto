package fr.inria.oak.effisto.Query.Translator;

import java.util.ArrayList;

import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptor;
import fr.inria.oak.effisto.Query.EffistoQuery.EffistoQuery;
import fr.inria.oak.effisto.Query.LocalQuery.LocalQuery;

public abstract class Translator {
	public String collectionName;
	public EffistoQuery eq;
	
	public LocalQuery rewrite(ArrayList<StorageDescriptor> sdlist, EffistoQuery eq){
		return null;
		
	}

}
