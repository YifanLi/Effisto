package fr.inria.oak.effisto.Loader.StorageDescriptor;

//import java.util.ArrayList;


public class StorageDescriptor{
	
	public String sdType;
	
	//for MongoDB:
	public MongoDBStorageAccess mgsa;
	public MongoDBAccessPattern mdap;
	public MongoDBDistributionPattern mddp;
	
	//for DynamoDB:
	public DynamoDBStorageAccess dnsa;
	
	//public Object sa;
	public StorageAccess sa;
	public AccessAndDistributionPattern adp;
	
	
	public StorageDescriptor (MongoDBStorageAccess sa, MongoDBAccessPattern mdap, MongoDBDistributionPattern mddp){
		System.out.println("DBtype:  "+sa.DBtype);
		this.mgsa = sa;
		this.mdap = mdap;
		this.mddp = mddp;
		this.sdType = "MongoDB";
	}
	
	public StorageDescriptor (DynamoDBStorageAccess sa, AccessAndDistributionPattern adp){
		System.out.println("DBtype:  "+sa.DBtype);
		this.dnsa = sa;
		this.adp = adp;
		this.sdType = "DynamoDB";
	}
	
	public StorageDescriptor (StorageAccess sa, AccessAndDistributionPattern adp){
		System.out.println("DBtype:  "+sa.DBtype);
		this.sa = sa;
		this.adp = adp;
	}
	
}
