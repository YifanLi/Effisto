package fr.inria.oak.effisto.Query.Translator;

import java.util.ArrayList;

import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptor;
import fr.inria.oak.effisto.Query.EffistoQuery.EffistoQuery;
import fr.inria.oak.effisto.Query.LocalQuery.LocalQuery;
import fr.inria.oak.effisto.Query.LocalQuery.MongoDBQuery;

public class MongoDBTranslator extends Translator {
	
	public String collectionName;
	public EffistoQuery eq;
	
	public MongoDBQuery rewrite(ArrayList<StorageDescriptor> sdlist, EffistoQuery eq){
		return null;
		
	}

}
