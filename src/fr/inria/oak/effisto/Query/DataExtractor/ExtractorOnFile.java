package fr.inria.oak.effisto.Query.DataExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreePattern;

public class ExtractorOnFile {
	
	private DataObject dataObject;
	private QueryTreePattern qtp;
	private String filePath;
	
	private Tuples tups;
	
	public ExtractorOnFile(String fp, QueryTreePattern q){
		this.qtp = q;
		this.filePath = fp;
		
		for(String x : q.getReturnedNodes()){
			this.tups.addColumn(x);
		}
		
		
		File file = new File(filePath);
		if(!file.exists() || !file.isFile()){
			return;
		}
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			//InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line = "";
			while ((line = reader.readLine()) != null) {
				dataObject = new DataObject(line);
				HashMap<String, String> records = evaluate(q, dataObject);
				if(records != null){
					for(String key : records.keySet()){
						this.tups.addRecord(key, records.get(key));
					}
				}
			}
			
			fileInputStream.close();
			inputStreamReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//
		
		
	}
	
	/*
	 * using a hashMap to store the evaluation result of this query on the specific DataObject,
	 * <node_tag, its_string_value>
	 */
	public HashMap<String, String> evaluate(QueryTreePattern q, DataObject obj){
		
		Match mh = new Match(q, obj);
		if(mh.checkMatched()){
			//
			//
			//
			HashMap<String, String> map = new HashMap<String, String>();
			return map;
		}else{
			return null;
		}
		
		//...
		//...
		//...
		
		
	}
	
	
	

}
