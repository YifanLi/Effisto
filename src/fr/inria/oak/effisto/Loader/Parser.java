package fr.inria.oak.effisto.Loader;

import java.util.ArrayList;
//import ...
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreeNode;

/*
 * this class is only used for json parsing test.
 */
public class Parser{
	public String DataURI;
	private HashMap<String, String> jsonMap = new HashMap<String, String>();
	Double doub = 0.0d;
	
	public String getDataCollection(String DataURI){
		//convert the dataset from DataURI into DataCollection(String)
		String DataCollection = new String();
		//...
		return DataCollection;
		
	}
	
	public HashMap<String,String> getJSONMap(){
		return jsonMap;
	}
	
	public static void printtest(String tag, JSONObject jsObj){
		
		String str = "abc";
		Object id = jsObj.get("id");
		System.out.println("----ID: ");
		System.out.println(id.toString());
		/*
		
		*/
		Iterator<String> it = jsObj.keys();
		while (it.hasNext()) {
			String key = it.next();
			Object child = jsObj.get(key);
			if(child.getClass().getSimpleName().equals("String")){
				if(child.equals(str)){
					System.out.println("abc abc abc equal!");
				}
			}
			//JSONObject child = jsObj.getJSONObject(key);
			System.out.println(child.getClass().getSimpleName());
			System.out.println(child.toString());
			System.out.println("----");
			
		}
	}
		
	public static ArrayList<Integer> addlist(Integer a){
		ArrayList<Integer> ali = new ArrayList<Integer>();
		for(int i = 0; i<3; i++){
			ali.add(a+i);
			if(a == 5){
				ArrayList<Integer> ali2 = addlist(6);
			}
			
		}
		return ali;
	}
	
	public static String joinStrArr(String[] strArr, String sep){
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < strArr.length; i++) {
			if(i!=0){
			   result.append(sep); 
			}
			result.append(strArr[i] );   
		}
		String mynewstring = result.toString();
		return mynewstring;
	}
	
	
	public static void main(String[] args) {
		
		//String jsonString = new String("{\"id\":201, \"name\":\"abc\"}");
		String jsonString = new String("{\"id\":10.01, \"name\":\"abc\", \"children\":[[\"Boa1\",\"Bob2\",\"Boc3\"],\"Tom\"], \"address\":{\"details\":{\"list\":[\"rue xxx\",\"rue yyy\"], \"phone\":\"123456\"},\"zip-code\":91140}, \"friends\":[{\"f_name\":\"zz\",\"l_name\":\"yy\"},{\"f_name\":\"aa\",\"l_name\":\"bb\"},{\"f_name\":\"tt\",\"l_name\":\"pp\"}]}");
		JSONObject result = new JSONObject();
		try {
			result = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		printtest("cars", result);
		String key = "color";
		String val= new String();
		String val2 = new String();
		String val3 = new String("*/abc/$/&/*/owo/ε/884");
		String path = new String("*/abc/$/&/*/owo/ε/884/dd/ε/wo/ur/**/ε/092");
		String path2 = new String("*/abc/def/opq/rsd/zzz");
		
		/*
		System.out.println("*********");
		ArrayList<QueryTreeNode> children = new ArrayList<QueryTreeNode>();
		if(children.size() == 0){
			System.out.println("it is 0!!!");
		}
		*/
		if(val==null){
			System.out.println("null null");
		}
		
		Parser par = new Parser();
		if(par.doub.equals(0.0d)){
			System.out.println("0.0d");
		}
		if(val2.isEmpty()){
			System.out.println("EMPTY!!!");
		}
		System.out.println(val3.split("\\/\\ε")[0]);
		if(val3.contains("/*")){
			System.out.println("/*");
		}
		System.out.println(val3.replaceFirst("\\/\\*", "/"+key));
		
		String prePath = new String();
		for(int i=0; i<path.split("\\/\\ε").length-1; i++){
			if(prePath.isEmpty()){
				prePath = path.split("\\/\\ε")[i];
			}else{
				prePath = prePath +"/ε"+ path.split("\\/\\ε")[i];
			}
			//p.epsilonPaths.add(prePath);
			System.out.println(prePath);
			
		}
		System.out.println(key.split("\\/").length);
		
		String[] ph2 = path2.split("\\/");
		ph2[1] = "111";
		ph2[3] = "333";
		System.out.println(joinStrArr(ph2,"/"));
		
		//JSONObject result2 = new JSONObject(val);
		//System.out.println(result2.toString());
		
	}
	
}
