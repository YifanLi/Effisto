package fr.inria.oak.effisto.Loader.StorageHandler;

//import java.util.ArrayList;
//import fr.inria.oak.effisto.Data;
import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBStorageAccess;
import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBStorageAccessParser;
import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptor;
import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptorParser;
import fr.inria.oak.effisto.Loader.StorageDescriptor.*;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;


public class MongoDBStorageHandler extends Handler{
	MongoDBStorageAccess sa;
	
	public MongoDBStorageHandler (MongoDBStorageAccess sa){
		this.sa = sa;
	}
	
	
	public StorageDescriptor load(ArrayList<DBObject> data, MongoDBAccessPattern mdap, MongoDBDistributionPattern mddp){
		try {
			 
			Mongo mongo = new Mongo(sa.getHost(), sa.getPort());
			DB db = mongo.getDB(sa.getDBName());
			DBCollection collection = db.getCollection(sa.getCollectionName());
 
			for (int i = 0; i < data.size(); i ++){
				// convert JSON to DBObject directly
				DBObject dbObject = (DBObject) data.get(i);
				collection.insert(dbObject);
			}
 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		//MongoDBStorageAccess sa = new MongoDBStorageAccess();
		//StorageAccess sa = this.sa;
		StorageDescriptor sd = new StorageDescriptor (this.sa, mdap, mddp);
		
		System.out.println("DBversion:  "+this.sa.DBversion);
			
		return sd;
	}
	
	public String scan(String[] StorageDescriptor){
		String DataCollection = new String();
		String lineSeparator = System.getProperties().getProperty("line.separator");
		
		StorageDescriptorParser sdp = new StorageDescriptorParser();
		sdp.sd = StorageDescriptor;
		String sa = sdp.getStorageAccess();
		System.out.println("sa: "+sa);
		
		MongoDBStorageAccessParser mosap = new MongoDBStorageAccessParser();
		mosap.sa = sa;
		
		System.out.println("isStoredInMongoDB?: "+mosap.isStoredInMongoDB());
	
		if(mosap.isStoredInMongoDB()){
			String Host = mosap.getHost();
			System.out.println("Host: "+Host);
			int Port = Integer.parseInt(mosap.getPort());
			System.out.println("Port: "+Port);
			String DBname = mosap.getDBName();
			System.out.println("DBname: "+DBname);
			String Collname = mosap.getCollName();
			System.out.println("Collname: "+Collname);
			
			try {
				 
				Mongo mongo = new Mongo(Host, Port);
				DB db = mongo.getDB(DBname);
				DBCollection collection = db.getCollection(Collname);
	 
				DBCursor cursorDoc = collection.find();
				while (cursorDoc.hasNext()) {
					//System.out.println(cursorDoc.next());
					DataCollection = DataCollection + lineSeparator + cursorDoc.next();
				}
	 
				//System.out.println("Done");
				 
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (MongoException e) {
				e.printStackTrace();
			}
		
		}	
		return DataCollection; 	
	}
	
}
