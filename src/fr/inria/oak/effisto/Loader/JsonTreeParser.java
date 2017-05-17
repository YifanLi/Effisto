package fr.inria.oak.effisto.Loader;

import fr.inria.oak.effisto.Data.HTTPURI;
import fr.inria.oak.effisto.Data.JSONCollection;
import fr.inria.oak.effisto.Data.LocalFileURI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
*/
public class JsonTreeParser extends Parser{
	
public String DataURI;
public String treeRoot = null;

private HashMap<String, String> jsonMap = new HashMap<String, String>();
//private ArrayList<HashMap<String, String>> jsonMapList = new ArrayList<HashMap<String, String>>();
private int layer;
	
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
	
	
	/*
	 * To parse the Json recursively 
	 */
	public void parseJson(JSONObject jsonObject) {

		parseJson(jsonObject, treeRoot);
	}
	
	private void parseJson(JSONObject jsonObject, String appendKey){
		if (jsonObject == null)
			return;
		String ak = appendKey;
		@SuppressWarnings("unchecked")
		Iterator<String> it = jsonObject.keys();
		while (it.hasNext()) {
			String key = it.next();
			JSONArray array = jsonObject.optJSONArray(key);
			//System.out.println("key.toString():  "+key);
			//System.out.println("array.toString():  "+array.toString());
			
			if(appendKey == null)
				appendKey = key;
			else
				appendKey = ak + "/" + key;
			
			if (array == null){
				String value = jsonObject.optString(key);
				if(value != null && !value.contains("{")){
					jsonMap.put(appendKey, value);
				}else{
					parseJson(getJSONObject(value),appendKey);
				}
			}
			else {
				//System.out.println("araay-araay-araay");
				//System.out.println("array.toString: "+array.toString());
				if(!(array.toString().contains(":"))){
					jsonMap.put(appendKey,array.toString());
				}else{
					//System.out.println(":::::::");
					//System.out.println(appendKey);
					//System.out.println(layer);
				parseJsonArray(array, -1,layer,appendKey);
				}
			}
		}
	}
	
	public JSONObject getJSONObject(String json){
		try {
			JSONObject result = new JSONObject(json);
			//System.out.println(result.toString());
			//System.out.println("*********");
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * no Array parsing if index = -1
	 * @param array
	 * @param index
	 * @param layer
	 * @return
	 */
	private int parseJsonArray(JSONArray array, int index,int layer, String appendKey) {
		if (array == null)
			return layer;
		int lastLayer = layer++;
		int len = array.length();
		System.out.println("array:--"+array.toString());
		
		for (int i = 0; i < len; i++) {
			//JSONObject obj = (JSONObject) array.opt(i);
			//System.out.println("array.opt(i).toString():"+array.opt(i).toString());
			//System.out.println("array.opt(i):"+array.opt(i));
			//JSONObject obj;
			
			String value = array.opt(i).toString();
			//System.out.println("value: "+value);
			
			String is = Integer.toString(i);
			
			if(!value.contains(":")){
				jsonMap.put(appendKey+"/"+is,value );
			}else if(value.startsWith("[")){
				JSONArray ja = new JSONArray(value);
				parseJsonArray(ja, i, layer, appendKey+"/"+is);
			}else if(value.startsWith("{")){
				//System.out.println("0-1-2: " + array.opt(i).toString());
				//JSONObject obj = new JSONObject(array.opt(i));
				//System.out.println("JSONObject obj: " + obj.toString());
				parseJson(array.optJSONObject(i), appendKey+"/"+is);
				//JSONObject obj = new JSONObject(array.opt(i));
				
			}else{
				
			}
			
		}
		return layer;
	}
	
	public HashMap<String,String> getJSONMap(){
		return jsonMap;
	}
	
/*
 * Notice: It's NOT so appropriate to use the below method.
 * suggest to use a loop to parse the whole file or to only one json object where it is needed.
 * 
	public ArrayList<HashMap<String,String>> parse(String FilePath) throws IOException{
		
		ArrayList<HashMap<String, String>> jsonMapList = new ArrayList<HashMap<String, String>>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(FilePath);
			InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			BufferedReader in = new BufferedReader(isr);
			
			String line="";
            while ((line=in.readLine())!=null) {
            	System.out.println("11111--->"+line);
            	JSONObject jsonObject = this.getJSONObject(line);
        		this.parseJson(jsonObject);
        		
        		//System.out.println(jtp.getJSONMap().toString());
        		//HashMap<String, String> map = new HashMap<String, String>();
        		//map = this.getJSONMap();
        		//System.out.println("22222--->"+map.toString());
        		//jsonMapList.add(map);
        		jsonMapList.add(this.getJSONMap());
        		System.out.println("!!!!!!--->"+jsonMapList.toString());
        		System.out.println(jsonMapList.size());
        		if(jsonMapList.size() >1 && jsonMapList.get(0)==jsonMapList.get(1)){
        			System.out.println("woooow");
        		}
        		this.jsonMap.clear();
        		//map.clear();
            	
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
		//System.out.println("!!!!!!--->"+jsonMapList.toString());
		return jsonMapList;
	}
	
*/
	
	/*
	 * Test codes below:
	 */
	
	/*
	public static void main(String[] args) {
		JsonTreeParser jtp = new JsonTreeParser();
		jtp.treeRoot = new String("people");
		String jsonString = new String("{\"id\":10, \"name\":\"abc\", \"children\":[[\"Boa1\",\"Bob2\",\"Boc3\"],\"Tom\"], \"address\":{\"details\":{\"list\":[\"rue xxx\",\"rue yyy\"], \"phone\":\"123456\"},\"zip-code\":91140}, \"friends\":[{\"f_name\":\"zz\",\"l_name\":\"yy\"},{\"f_name\":\"aa\",\"l_name\":\"bb\"},{\"f_name\":\"tt\",\"l_name\":\"pp\"}]}");
		JSONObject jsonObject = jtp.getJSONObject(jsonString);
		jtp.parseJson(jsonObject);
		
		//System.out.println(jtp.getJSONMap().toString());
		HashMap<String, String> map = jtp.getJSONMap();
		for (Entry<String, String> entry : map.entrySet()) {
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		
		
	}
	*/
	

}
