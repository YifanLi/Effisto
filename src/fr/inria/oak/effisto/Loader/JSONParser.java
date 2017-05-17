package fr.inria.oak.effisto.Loader;

import javax.net.ssl.HttpsURLConnection;

import fr.inria.oak.effisto.Data.HTTPURI;
import fr.inria.oak.effisto.Data.JSONCollection;
import fr.inria.oak.effisto.Data.LocalFileURI;

//import java.util.ArrayList;


public class JSONParser extends Parser{
	public String DataURI;
	
	public String getDataCollection(String DataURI){
		//convert the dataset from DataURI into DataCollection(String)
		String DataCollection = new String();
		String DataCollectionTmp = new String();
		//...
		LocalFileURI lfURI = new LocalFileURI();
		lfURI.URI = DataURI;
		
		HTTPURI httpURI = new HTTPURI();
		httpURI.URI = DataURI;
		
		JSONCollection JsonColl = new JSONCollection();
		
		if (lfURI.isLocalFileURI())
		{
			//read the file into a String DataCollectionTmp
			//
			JsonColl.DataCollection = DataCollectionTmp;
			if(JsonColl.isJSONCollection()){
				DataCollection = DataCollectionTmp;
			}
			
			
			
		}else{
			if(httpURI.isHTTPURI())
			{
				//read the file from a HTTP URI into a String DataCollectionTmp
				//
				JsonColl.DataCollection = DataCollectionTmp;
				if(JsonColl.isJSONCollection()){
					DataCollection = DataCollectionTmp;
				}
				
				
			}
		}
		
		
		return DataCollection;
		
	}

	
}
