package fr.inria.oak.effisto.Query.JsonQueryEngine;

import org.json.JSONObject;
import org.json.XML;



public class json2XML {
	public String str = new String("{'name':'JSON','integer':1,'double':2.0,'boolean':true,'nested':{'id':42},'array':[1,2,3]}");

	public void main(String[] args) throws Exception{
		//public String str1 = new String("{'name':'JSON','integer':1,'double':2.0,'boolean':true,'nested':{'id':42},'array':[1,2,3]}");
		JSONObject json = new JSONObject(str);
		String xml = XML.toString(json);
		System.out.println(xml);
		
	}
}

