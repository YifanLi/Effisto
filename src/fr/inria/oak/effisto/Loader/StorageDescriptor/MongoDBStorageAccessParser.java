package fr.inria.oak.effisto.Loader.StorageDescriptor;

public class MongoDBStorageAccessParser{
	// 31/1/2014 Not now; later.
	
	public String sa;
	//StorageAccess 
	//MongoDB: host     : port: DB : coll: attributes
	//e.g.  1:192.168...:27017:Cars:brand:_id+year+address
	// 1 for MongoDB
	
	
	
	public String[] spliteSA(){
	if (sa.contains(":")) {
	    // Split it.
		return sa.split("\\:");
	} else {
	    throw new IllegalArgumentException("StorageAccess " + sa + " does not contain :");
	}
	}
	

	public boolean isStoredInMongoDB(){
		System.out.println("spliteSA()[0]: "+this.spliteSA()[0]);
		if (this.spliteSA()[0].equals("1")){
			return true;
		}else{
			//System.out.println("spliteSA()[0]: "+this.spliteSA()[0]);
			return false;
		}
	}
	
	public String getHost(){
		if(this.spliteSA().length > 1){
			return this.spliteSA()[1];
		}else{
			return null;
		}
	}
	
	public String getPort(){
		if(this.spliteSA().length > 2){
			return this.spliteSA()[2];
		}else{
			return null;
		}
	}
	
	public String getDBName(){
		if(this.spliteSA().length > 3){
			return this.spliteSA()[3];
		}else{
			return null;
		}
	}
	
	public String getCollName(){
		if(this.spliteSA().length > 4){
			return this.spliteSA()[4];
		}else{
			return null;
		}
	}
	
	public String getAttributes(){
		if(this.spliteSA().length > 5){
			return this.spliteSA()[5];
		}else{
			return null;
		}
	}
	
}
