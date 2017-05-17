package fr.inria.oak.effisto.Data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

//import java.util.ArrayList;


@SuppressWarnings("deprecation")
public class HTTPURI extends URI{

	public String URI;
	
	public boolean isHTTPURI(){
		//to check if the URI is an URL 
		//
		//
		return false;
	}

	public String getURI(){
		return URI;
	}
	
	public static String getJSONfromURL(String url){
	    InputStream is = null;
	    String result = "";
	    JSONArray jArray = null;

	    // Download JSON data from URL
	    try{
	        @SuppressWarnings("resource")
			HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(url);
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        

	    }catch(Exception e){
	        System.out.println("Error in http connection "+e.toString());
	    }

	    // Convert response to string
	    try{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	        is.close();
	        result = sb.toString();
	    }catch(Exception e){
	    	System.out.println("Error converting result "+e.toString());
	    }

	    try{

	        jArray = new JSONArray(result);
	    }catch(JSONException e){
	    	System.out.println("Error parsing data "+e.toString());
	    }

	    return result;
	}
	
}
