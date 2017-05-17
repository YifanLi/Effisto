package fr.inria.oak.effisto.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.inria.oak.effisto.Loader.JsonTreeParser;
import fr.inria.oak.effisto.Loader.StorageDescriptor.MongoDBStorageAccess;
import fr.inria.oak.effisto.Loader.StorageDescriptor.StorageDescriptor;

public class FileAccess {
	
	public ArrayList<String> getMongoDBconfig(String ConfigFilePath) throws IOException{
		ArrayList<String> MongoDBconfig;
		MongoDBconfig = new ArrayList<String>();
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(ConfigFilePath);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			BufferedReader in = new BufferedReader(isr);
			
			String line="";
            while ((line=in.readLine())!=null) {
            	//System.out.println(line);
            	String[] configs = line.split("\\,");
            	if(configs[0].equals("MongoDB")){
            		for(int i=1;i<configs.length;i++){
            		MongoDBconfig.add(configs[i]);
            		}
            	}
            	
            }
            
    		in.close();
    		//coll = buffer.toString();
    		isr.close();
    		fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return MongoDBconfig;
		
	}
	
	
	public ArrayList<DBObject> getDBObjectsFromFile(String fileName) throws IOException{
		
		ArrayList<DBObject> data;
		data = new ArrayList<DBObject>();
		
		// 1. read and parse all the JSON trees from the file whose name is given
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			BufferedReader in = new BufferedReader(isr);
			
			String line="";
	        while ((line=in.readLine())!=null) {
	        	//System.out.println(line);
	        	DBObject dbObject = (DBObject) JSON
						.parse(line);
	        	data.add(dbObject);
	        	
	        }
	        
			in.close();
			//coll = buffer.toString();
			isr.close();
			fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
		
	}
	
	public String storeJsonTrees(String fileName, String root, MongoDBStorageAccess mdsa, String path ){
		
		JsonTreeParser jtp = new JsonTreeParser();
		jtp.treeRoot = root;
		
		String treesFile = new String("MongoDB_"+mdsa.host+"_"+mdsa.port+"_"+mdsa.dbName+"_"+mdsa.collectionName+"_trees");
		String treesFilePath = new String(path+treesFile);
		FileOutputStream fos;

		try{
			fos = new FileOutputStream(new File(treesFilePath), true);
			OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
	        BufferedWriter  bw=new BufferedWriter(osw);
	        /*
	        ArrayList<HashMap<String,String>> jsonMapList = jtp.parse(fileName);
	        //System.out.println(jsonMapList.toString());
	        //for x in 
	        for(int i=0; i<jsonMapList.size(); i++){
	        	bw.write(jsonMapList.get(i).toString());
	        	bw.write("\n");
	        	bw.write("*******");
	        	bw.write("\n");
	        	
	        }
			
			*/
	        
	        FileInputStream fis;
			try {
				fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF8");
				BufferedReader in = new BufferedReader(isr);
				
				String line="";
	            while ((line=in.readLine())!=null) {
	            	System.out.println("11111--->"+line);
	            	JSONObject jsonObject = jtp.getJSONObject(line);
	        		jtp.parseJson(jsonObject);
	        		
	        		System.out.println("2222----->"+jtp.getJSONMap().toString());
	        		bw.write(jtp.getJSONMap().toString());
		        	bw.write("\n");
		        	bw.write("*******");
		        	bw.write("\n");
	            	
	            }
	            
	    		in.close();
	    		isr.close();
	    		fis.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			bw.close();
			osw.close();
			fos.close(); 
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return treesFilePath;
	}
	
	public void storeStorageDescriptor(StorageDescriptor sd, String filepath){
		
		FileOutputStream fos1;
		try{
			fos1 = new FileOutputStream(new File(filepath), true);
			OutputStreamWriter osw=new OutputStreamWriter(fos1, "UTF-8");
	        BufferedWriter  bw=new BufferedWriter(osw);
	        if(sd.sdType.equals("MongoDB")){
	        	bw.write(sd.mgsa.getStorageAccess() + "##" + sd.mdap.ap + "<->" + sd.mdap.ref + "##" + sd.mddp.getDistributionPattern());
	        	bw.write("\n");
	        	System.out.println("sd.mgsa.getStorageAccess() : "+sd.mgsa.getStorageAccess());
	        }else if(sd.sdType.equals("DynamoDB")){
	        	//
	        	//
	        	
	        }
			bw.close();
			osw.close();
			fos1.close();       
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void discardLastStorageDescriptor(){
		
	}
	

}
