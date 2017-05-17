package fr.inria.oak.effisto.Loader.StorageHandler;

// a test to save the JSON data into MongoDB using a simplest way
//case01: DS1 for MongoDB

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBStorageAccess;

public class MongoDBDataStorageOne extends Handler{
	public String host;
	public int port;
	public String nameOfdb;
	public String nameOfNewCollection;
	
	MongoDBStorageAccess mg = new MongoDBStorageAccess(null);
	
	public String[] load(String DataCollection, String AccessAndDistributionPattern){
		String[] StorageDescriptor = null;
		try {
 
			//Mongo mongo = new Mongo("localhost", 27017);
			//DB db = mongo.getDB("DS1");
			//DBCollection collection = db.getCollection("cars");
			Mongo mongo = new Mongo(host, port);
			DB db = mongo.getDB(nameOfdb);
			DBCollection collection = db.getCollection(nameOfNewCollection);
			
		   /*
			*to parse the JSON file(UTF-8 encoding) into a collection of documents(java String)
			*e.g. a document for one line in this file "cars.json"
			*
			*@throws IOException 
			*/
			//String coll;
			//StringBuffer buffer = new StringBuffer();
    		try {
        		FileInputStream fis = new FileInputStream("/Users/yifanli/test/cars.json");
        		InputStreamReader isr = new InputStreamReader(fis, "UTF8");
        		BufferedReader in = new BufferedReader(isr);
        		
        		String line="";
                while ((line=in.readLine())!=null) {
                	//System.out.println(line);
                	DBObject dbObject = (DBObject) JSON
        					.parse(line);
                	collection.insert(dbObject);
                	
                }
                
        		in.close();
        		//coll = buffer.toString();
        		isr.close();
        		fis.close();
    		} 
    		catch (IOException e) {
        		e.printStackTrace();
        		//coll = null;
    		}
			
			
			/*
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON
					.parse("{'_id':'1000', 'color':'red', 'model':{'name':'Panda 4*4 Climbing','year':2007}, 'owners':[{'fname':'Bob', 'lname':'Doe'},{'fname':'Alice', 'lname':'Smith'}]}");
 
			collection.insert(dbObject);
 			*/
			
			DBCursor cursorDoc = collection.find();
			while (cursorDoc.hasNext()) {
				System.out.println(cursorDoc.next());
			}
 
			System.out.println("Done");
 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		mg.dc = DataCollection;
		String StorageAccess = mg.getStorageAccess();
		StorageDescriptor = new String[]{StorageAccess, AccessAndDistributionPattern};
		
		return StorageDescriptor;
	}
	

}
