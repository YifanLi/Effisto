package fr.inria.oak.effisto.Query.Extractor;

import java.util.ArrayList;
import java.util.HashMap;

import fr.inria.oak.effisto.Exception.InvalidArgumentException;
import fr.inria.oak.effisto.Exception.VIP2PException;
import fr.inria.oak.effisto.IDs.ElementID;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreePattern;
import fr.inria.oak.effisto.Query.Execution.*;

/**
 * to construct the tuple(s) from a returned result by a query
 * @author Yifan LI
 */
public class TupleBuilder {
	
	// to store the tuples
	public ArrayList<NTuple> tuples = new ArrayList<NTuple>();
	
	// the name/path of queried json file
	public String jsonFile;
	
	// the query tree pattern
	public QueryTreePattern qtp;
	
	// the tags returned in every extract tree
	public ArrayList<String> colNames;
	
	//the meta data types of each column
	TupleMetadataType[] types;
	
	public TupleBuilder(String jFile, QueryTreePattern queryTP, TupleMetadataType[] types){
		this.jsonFile = new String(jFile);
		this.qtp = queryTP;
		this.colNames = this.qtp.getReturnedNodes();
		this.types = types;
		//this.getReturnedTuplesFromFile(jFile);
	}
	
	/**
	 * the method to do tuple(s) construction, upon the result from ONE queried json doc(one line in json file).
	 * Notice:
	 * Only String and Integer metadataType were considered in below program, for now.
	 * @param id, the ID of json doc
	 * @param result, the query result from above json doc
	 * @param types, those data types(TupleMetadataType) of each column
	 * @return
	 * @throws VIP2PException 
	 */
	public ArrayList<NTuple> produceTuples(String id, ArrayList<ArrayList<String>> result, TupleMetadataType[] types) throws VIP2PException{
		ArrayList<NTuple> tuples = new ArrayList<NTuple>();
		int colNo = this.colNames.size();
		NRSMD[] childrenNRSMD = null;
		char[][] uriFields = null;
		ElementID[] idFields = null;
		ArrayList<NTuple>[] nestedFields = null;
		if(types.length != colNo){
			System.out.println("the number of types is not equal to colNo!");
			return null;
		}
		ArrayList<ArrayList<String>> ts = produceCartesianProduct(result, true);
		for(ArrayList<String> objs : ts){
			//TupleMetadataType[] types = new TupleMetadataType[this.colNames.size()];
			char[][] stringFields;
			int[] integerFields;
			int iTypes = 0;
			ArrayList<Integer> ints = new ArrayList<Integer>();
			ArrayList<String> strs = new ArrayList<String>();
			for(int i=0; i<types.length; i++){
				String obj = objs.get(i);
				TupleMetadataType ty = types[i];
				if(ty == TupleMetadataType.STRING_TYPE){
					strs.add(obj);
					iTypes++;
				}else if(ty == TupleMetadataType.INTEGER_TYPE){
					String[] parts = obj.split("\\:", 2);
					try{
					ints.add(Integer.parseInt(parts[1]));
					}catch(NumberFormatException e){
						System.out.println(e.toString());
					}
					iTypes++;
				}else{
					strs.add(obj);
					iTypes++;
				}
			}
			stringFields = new char[strs.size()][];
			for(int i=0; i<strs.size(); i++){
				stringFields[i] = strs.get(i).toCharArray();
			}
			integerFields = new int[ints.size()];
			for(int i=0; i<ints.size(); i++){
				integerFields[i] = ints.get(i);
			}
			
			NTuple t = new NTuple(colNo, types, childrenNRSMD, stringFields, uriFields, idFields, integerFields, nestedFields);
			t.idOfJsonObj = id;
			tuples.add(t);
			
		}
		
		return tuples;
	}
	
	/**
	 * to get all those returned tuples from a to-be-extracted json file, and they are stored in a hashmap,
	 * key is each json-object's id.
	 * @param fileName
	 * @return
	 * @throws VIP2PException 
	 */
	public HashMap<String, ArrayList<NTuple>> getReturnedTuplesFromFile() throws VIP2PException{
		String fileName = this.jsonFile;
		HashMap<String, ArrayList<NTuple>> tuplesFromFile = new HashMap<String, ArrayList<NTuple>>();
		SinglePatternExtractorForJson spefj = new SinglePatternExtractorForJson(this.qtp);
		//HashMap<String, ArrayList<ArrayList<Object>>> results = spefj.toExtractJsonFile(fileName, this.qtp);
		HashMap<String, ArrayList<ArrayList<String>>> results = spefj.toExtractJsonFile(fileName, this.qtp, true);
		System.out.println("There are "+results.size()+" json objects has the matched returns.");
		for(String ID : results.keySet()){
			//System.out.println("============");
			ArrayList<NTuple> ntuples = this.produceTuples(ID, results.get(ID), this.types);
			tuplesFromFile.put(ID, ntuples);
		}
		
		
		return tuplesFromFile;
	}
	
	/**
	 * to construct the Cartesian product over those result sets of every extract tree:
	 * the columns order in product is same to input's.
	 * @param material
	 * @return
	 */
	public ArrayList<ArrayList<Object>> produceCartesianProduct(ArrayList<ArrayList<Object>> material){
		ArrayList<ArrayList<Object>> tmp = new ArrayList<ArrayList<Object>>();
		if(material.isEmpty()){
			return null;
		}
		if(tmp.isEmpty()){
			if( material.get(0).isEmpty()){
				Object nullObj = new Object();
				ArrayList<Object> t = new ArrayList<Object>();
				t.add(nullObj);
				tmp.add(t);
			}else{
				for(Object obj : material.get(0)){
					ArrayList<Object> t = new ArrayList<Object>();
					t.add(obj);
					tmp.add(t);
				
			}
			}
		}
		if(material.size()>1){
			ArrayList<ArrayList<Object>> tmp2 = new ArrayList<ArrayList<Object>>();
			for(int i =1; i<material.size(); i++){
				for(ArrayList<Object> ol : tmp){
					if(material.get(i).isEmpty()){
						Object nullObj = new Object();
						ArrayList<Object> newOl = new ArrayList<Object>(ol);
						newOl.add(nullObj);
						tmp2.add(newOl);
					}else{
						for(Object obj : material.get(i)){
							ArrayList<Object> newOl = new ArrayList<Object>(ol);
							newOl.add(obj);
							tmp2.add(newOl);
					}
					}
				}
				
				tmp = new ArrayList<ArrayList<Object>>(tmp2);
				tmp2.clear();
			}
		}
		
		
		return tmp;
	}
	
	/**
	 * to construct the Cartesian product over those result sets(with object-node names) of every extract tree:
	 * the columns order in product is same to input's.
	 * @param material
	 * @return
	 */
	public ArrayList<ArrayList<String>> produceCartesianProduct(ArrayList<ArrayList<String>> material, boolean withNames){
		ArrayList<ArrayList<String>> tmp = new ArrayList<ArrayList<String>>();
		if(!withNames || material.isEmpty()){
			return null;
		}
		if(tmp.isEmpty()){
			for(String obj : material.get(0)){
				ArrayList<String> t = new ArrayList<String>();
				t.add(obj);
				tmp.add(t);
			}
		}
		if(material.size()>1){
			ArrayList<ArrayList<String>> tmp2 = new ArrayList<ArrayList<String>>();
			for(int i =1; i<material.size(); i++){
				for(ArrayList<String> ol : tmp){
					for(String obj : material.get(i)){
						ArrayList<String> newOl = new ArrayList<String>(ol);
						newOl.add(obj);
						tmp2.add(newOl);
					}
				}
				
				tmp = new ArrayList<ArrayList<String>>(tmp2);
				tmp2.clear();
			}
		}
		
		
		return tmp;
	}

	/**
	 * for test:
	 * @param args
	 * @throws InvalidArgumentException
	 * @throws VIP2PException 
	 */
	public static void main(String[] args) throws InvalidArgumentException, VIP2PException{
		QueryTreePattern qtp = new QueryTreePattern("src/resource/query03_1");
		String fileName = new String("src/resource/cars_test");
		TupleMetadataType[] types = {TupleMetadataType.STRING_TYPE, TupleMetadataType.STRING_TYPE, TupleMetadataType.INTEGER_TYPE};
		
		TupleBuilder tb = new TupleBuilder(fileName, qtp, types);
		//String e = new String("ε");
		/*
		//System.out.println("query tree pattern: "+qtp.numOfExtractTrees);
		SinglePatternExtractorForJson spefj = new SinglePatternExtractorForJson(tb.qtp);
		HashMap<String, ArrayList<ArrayList<Object>>> results = spefj.toExtractJsonFile(tb.jsonFile, tb.qtp);
		System.out.println("There are "+results.size()+" json objects has the matched returns.");
		for(String ID : results.keySet()){
			System.out.println("#######");
			System.out.println("the ID of json Object that has matched returns: "+ID);
			int i = 1;
			for(ArrayList<Object> os : results.get(ID)){
				System.out.println("the #"+i+" extract tree:");
				for(Object obj : os){
					System.out.println(obj.toString());
				}
				i++;
			}
		}
		
		
		System.out.println("+!+!+!+!");
		for(String ID: results.keySet()){
			ArrayList<ArrayList<Object>> os = tb.produceCartesianProduct(results.get(ID));
			for(ArrayList<Object> tuple : os){
				String tmp = new String();
				for(Object obj : tuple){
					//System.out.println("___"+obj.getClass().getSimpleName());
					if(obj.getClass().getSimpleName().equals("Object")){
						tmp = tmp + ":::"+"NULL";
					}else{
						tmp = tmp + ":::"+obj.toString();
					}
				}
				System.out.println(tmp);
			}
			System.out.println("============");
			ArrayList<NTuple> ntuples = tb.produceTuples(ID, results.get(ID));
			for(NTuple tu : ntuples){
				System.out.println("string fields in Tuple:");
				tu.printStrs();
				System.out.println("integer fields in Tuple:");
				tu.printInts();
				System.out.println("-----");
				System.out.println("colNO: "+tu.nrsmd.colNo);
				System.out.println("types: "+tu.nrsmd.types[0]);
				System.out.println(tu.getIntegerField(2));
				System.out.println(tu.stringFields[1]);
			}
		}
		*/
		
		
		System.out.println("&&&&&&&&&&&&&&&&&");
		
		HashMap<String, ArrayList<NTuple>> rs = tb.getReturnedTuplesFromFile();
		for(String id : rs.keySet()){
			System.out.println("doc ID: "+id);
			for(NTuple t: rs.get(id)){
				System.out.println("string fields in Tuple:");
				t.printStrs();
				System.out.println("integer fields in Tuple:");
				t.printInts();
				System.out.println("++++++++++++++");
			}
		}
		
		
		
	}

	
	
}