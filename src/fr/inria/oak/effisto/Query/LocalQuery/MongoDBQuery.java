package fr.inria.oak.effisto.Query.LocalQuery;

import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBStorageAccess;
import fr.inria.oak.effisto.Query.Index.MongoDBIndex;

public class MongoDBQuery extends LocalQuery {
	public String collectionName;
	public String id;
	public MongoDBIndex midx;
	public MongoDBStorageAccess msa;
	

}
