package fr.inria.oak.effisto.Query.LocalQuery;

import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageAccess;
import fr.inria.oak.effisto.Query.EffistoQuery.EffistoQuery;
import fr.inria.oak.effisto.Query.Index.Index;

public abstract class LocalQuery {
	public String collectionName;
	public String id;
	public Index idx;
	//public EffistoQuery eq;
	public StorageAccess sa;
	
	

}
