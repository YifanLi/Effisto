package fr.inria.oak.effisto.Query.JsonQueryEngine.Jaql;

import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.lang.JaqlQuery;

public class Jaql {
	
	public String DNname;
	public String CollectionName;
	
	public String FilePath;
	public String QueryString;
	
	public void Jaql(){
		
	}
	
	public void constructQuery(){
		
	}
	
	public JsonValue evalQuery(JaqlQuery q){
		return null;
		
	}
	
	public void test(){
        String PUBLISHERS = "[{type: 'a', name: 'Scholastic', country: 'USA'}, "
        + "{type: 'a',name: 'Grosset', country: 'UK'}, "
        + "{type: 'b',name: 'Writers Publishing House', country: 'China'}]";
        //String LOCATION = "/Users/yifanli/test/publishers";
        try{
                JaqlQuery q = new JaqlQuery();
                q.setQueryString("$publishers -> filter $.type == 'a' -> transform {country: $.country};");     
                q.setArray("$publishers", PUBLISHERS); 
                //q.setVar("$location", LOCATION);
                JsonValue jv = q.evaluate();
                System.out.println(jv);
                q.close();
        }catch(Exception ex){
                ex.printStackTrace();
        }
}
	
	
	/*
	 * for test:
	 */
	public static void main(String[] args){
		Jaql jl = new Jaql();
		jl.test();
		
	}

}
