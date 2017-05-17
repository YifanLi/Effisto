package fr.inria.oak.effisto;

import java.io.IOException;
import java.util.*;

import com.mongodb.DBObject;
import fr.inria.oak.effisto.Data.FileAccess;
import fr.inria.oak.effisto.Loader.StorageDescriptor.AccessAndDistributionPattern;
import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBAccessPattern;
import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBDistributionPattern;
import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBStorageAccess;
import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptor;
import fr.inria.oak.effisto.Loader.StorageHandler.MongoDBStorageHandler;

public class Main{

	void store(String fileName, String ConfigFilePath, String rootName) throws IOException{

		ArrayList<DBObject> data;
		data = new ArrayList<DBObject>();
		FileAccess fa = new FileAccess();
		
		// 1. read and translate all the JSON trees from the file whose name is given into DBObject(MongoDB)
		data = fa.getDBObjectsFromFile(fileName);
		// get the config info from config file		
		ArrayList<String> configs = fa.getMongoDBconfig(ConfigFilePath);
			
		MongoDBStorageAccess mdsa = new MongoDBStorageAccess(configs);
		
		// 2. load the JSON trees in a store as prescribed by a fixed StorageDescriptor (for the time being, 31/1/14)
		// here, they will be stored in a file "/Users/yifanli/test/***", where *** is named MongoDB_host_port_dbname_collectionname_trees
		String treesFilePath = fa.storeJsonTrees(fileName, rootName, mdsa, "/Users/yifanli/test/" );
		
		MongoDBStorageHandler mdsh = new MongoDBStorageHandler(mdsa);
		MongoDBAccessPattern mdap = new MongoDBAccessPattern();
		MongoDBDistributionPattern mdbp = new MongoDBDistributionPattern();
		
		//for now, the AccessPattern for MongoDB is "i:*,for i in [1,data.size]", (for the time being, 3/2/2014)
		mdap.ap = new String("i:*,for i in [1," + data.size() +"]");
		mdap.ref = treesFilePath;
		
		StorageDescriptor sd = mdsh.load(data, mdap, mdbp);
		fa.storeStorageDescriptor(sd, "/Users/yifanli/test/effisto.StorageDescriptors");	
		
		
	}
	
	
	/*
	 * for test:
	 * 
	 */
	public static void main(String[] args) throws IOException{
		//System.out.println("Hello World!");
		/*
		if (args.length == 0){
			return;
		}
		if (args[0].compareTo("load")==0){
			if (args.length == 1){
				System.out.println("We need the name of the data file and of the config file!");
				return;
			}
		
			// path to local JSON file 
			String fileName = args[1];
			String ConfigFilePath = args[2];
		*/
			Main m = new Main();
			
			/*
			 * for test:
			 */
			String fileName = new String("/Users/yifanli/test/cars");
			String ConfigFilePath = new String("/Users/yifanli/test/effisto.conf");
			m.store(fileName,ConfigFilePath, "cars");
			
		
	}
}
