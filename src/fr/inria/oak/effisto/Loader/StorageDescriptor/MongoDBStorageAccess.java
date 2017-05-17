package fr.inria.oak.effisto.Loader.StorageDescriptor;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

//import java.util.ArrayList;


public class MongoDBStorageAccess extends StorageAccess{
	public String DBtype = new String("MongoDB");
	
	public String host;
	public int port;
	public String dbName;
	public String collectionName;
	public String DBversion;
	
	
	public  MongoDBStorageAccess(ArrayList<String> configs){
		
		this.host = configs.get(0);
		this.port = Integer.parseInt(configs.get(1));
		this.dbName = configs.get(2);
		this.collectionName = configs.get(3);
		this.DBversion = configs.get(4);
		
	}
	
	public String getStorageAccess(){
		//String StorageAccess = null;
		//...
		return this.DBtype+"("+this.DBversion+")"+":"+this.host+"|"+ Integer.toString(this.port)+"|"+this.dbName+"|"+this.collectionName;
	}
	
	public String getHost(){
		return host;
	}
	public int getPort(){
		return port;
	}
	
	public String getDBName(){
		return dbName; 
	}
	public String getCollectionName(){
		return collectionName;
	}
	
	
	public void display(){
		
		Mongo mg = null;
		try {
            mg = new Mongo(this.host, this.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
        DB db = mg.getDB(this.dbName);
        DBCollection users = db.getCollection(this.collectionName);
        
        DBCursor cursor = users.find();
        try {
           while(cursor.hasNext()) {
               System.out.println(cursor.next());
           }
        } finally {
           cursor.close();
        }
        
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
        System.gc();
		
	}
	
	public void put(String key, String value){
		
		Mongo mg = null;
		try {
            mg = new Mongo(this.host, this.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
        DB db = mg.getDB(this.dbName);
        DBCollection users = db.getCollection(this.collectionName);
        
        BasicDBObject doc = new BasicDBObject("_id", key).append(key,value); // NOTICE: here we use "_id" as the key label
        users.insert(doc);
        
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
        System.gc();
        
        
		
	}
	
	public DBObject get(String key){
		DBObject value = null;
		
		Mongo mg = null;
		try {
            mg = new Mongo(this.host, this.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
        DB db = mg.getDB(this.dbName);
        DBCollection users = db.getCollection(this.collectionName);
        
        BasicDBObject query = new BasicDBObject("_id", key);
        value = (DBObject) users.find(query);
        
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
        System.gc();
		
		return value;
		
	}
	
	// to serialize the json (DBOject in MongoDB)
	public String serialize(DBObject obj){
		return obj.toString();
		
	}
	
}
