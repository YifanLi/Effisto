package fr.inria.oak.effisto.Query.Extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.inria.oak.effisto.Exception.InvalidArgumentException;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryExtractTreePattern;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryRetrieveTreePattern;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreeEdge;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreeNode;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreePattern;

/*
 * a tool to extract results(json-object, still) from a Json object, or a file containing multiple ones, which
 * meets the matching to a Retrieve tree pattern(and a few extract tree patterns).
 * Input: QueryTreePattern, Json Object(or, Json file)
 * Output: the matched "Json objects" returned from every extract tree pattern
 * @author Yifan LI
 * 
 * Warning:
 * 1) when you use this class in others, make sure that class file is encoded by UTF-8 due to the existence of "ε" .
 */
public class SinglePatternExtractorForJson {
	
	public SinglePatternExtractorForJson(QueryTreePattern qtp, String jsonStr){
		//this.retLLAJ  = new LinkedList<ArrayList<Match>>();
		//this.extLLAJ = new LinkedList<ArrayList<JSONObject>>();
		this.currentQTP = qtp;
		this.currentQP = qtp.getRetrieveTreePattern();
		this.ArrQTP = qtp.getExtractTreePatternS();
		this.docObj = this.getJSONObjectFromString(jsonStr); 
		this.idset = new HashSet<String>();
		this.objsForEpsilon = new HashMap<String, ArrayList<Object>>();
	}
	
	public SinglePatternExtractorForJson(QueryTreePattern qtp){
		this.currentQTP = qtp;
		this.currentQP = qtp.getRetrieveTreePattern();
		this.ArrQTP = qtp.getExtractTreePatternS();
		this.docObj = new JSONObject(); 
		this.idset = new HashSet<String>();
		this.objsForEpsilon = new HashMap<String, ArrayList<Object>>();
	}
	
	/*
	* The QueryTreePattern we currently try to match
	*/
	QueryTreePattern currentQTP;
		
		
	/*
	* The RetrieveTreePattern we currently try to match
	*/
	//QueryRetrieveTreePattern currentQP = this.currentQTP.getRetrieveTreePattern();
	QueryRetrieveTreePattern currentQP;
	
	/*
	* The ExtractTreePattern(s) we currently try to match
	*/
	ArrayList<QueryExtractTreePattern> ArrQTP;
	
	/*
	 * the whole document(as a json object)
	 * e.g. {..., ..., ...}
	 */
	private JSONObject docObj;
	
	/*
	 * a set to store those IDs in the query-retrieve clause, if the IDSelection is not "*"
	 */
	HashSet<String> idset = new HashSet<String>();
	
	/*
	 * to store returned Objects from a extract-tree pattern
	 */
	ArrayList<Object> returnedObjects = new ArrayList<Object>();
	
	/*
	 * to store extract-node(in json dataset) names of those returned Objects
	 */
	ArrayList<String> NamesOfreturnedObjects = new ArrayList<String>();
	
	
	/*
	 * to store ALL returned Objects from each extract-tree pattern
	 */
	ArrayList<ArrayList<Object>> allReturnedObjects = new ArrayList<ArrayList<Object>>();
	
	/*
	 * to store ALL returned Objects from each extract-tree pattern, with extract-node names
	 */
	ArrayList<ArrayList<String>> allReturnedObjectsWithNames = new ArrayList<ArrayList<String>>();
	
	/*
	 * to store those path-object(s) corresponding to each Epsilon
	 */
	HashMap<String, ArrayList<Object>> objsForEpsilon;
	
	
	
	/*
	 * to add a new object to the hashmap objsForEpsilon
	 */
	public void addObjectToEpsilon(String path, Object jsonObj){
		if(this.objsForEpsilon.containsKey(path)){
			ArrayList<Object> newObjects = new ArrayList<Object>(this.objsForEpsilon.get(path));
			newObjects.add(jsonObj);
			this.objsForEpsilon.remove(path);
			this.objsForEpsilon.put(path, newObjects);
		}else{
			ArrayList<Object> newObjects = new ArrayList<Object>();
			newObjects.add(jsonObj);
			this.objsForEpsilon.put(path, newObjects);
		}
	}
	
	/**
	 * to transfer a String to JSONObject
	 * @param json, a String representation of json object
	 * @return a JSONObject
	 */
	public JSONObject getJSONObjectFromString(String json){
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
	 * get the ID of a json Object, if it has.
	 * @param jsObj, a JSONObject with an ID
	 * @return its ID
	 */
	public String getID(JSONObject jsObj){
		String ID = null;
		Object id = null;
		try{
			id = jsObj.get("ID");
		}catch(JSONException e){
			e.getMessage();
		}
		if(id.getClass().getSimpleName().equals("String") && !id.toString().isEmpty()){
			ID = id.toString();
		}
		
		return ID;
	}
	
	/**
	 * to check the ID of this doc is in query's IDSelection
	 * Notice:
	 * 1) the id of a document is represented as "ID:1001", "ID" is a child of root.
	 * @param QP, a QueryRetrieveTreePattern
	 * @param jsonObject(the doc)
	 * @return boolean
	 */
	public boolean checkID(QueryRetrieveTreePattern QP, JSONObject jsonObject){
		boolean isValidID = false;
		//if the query doesn't begin with "*", instead of a id list...
		if(!QP.getIDSelection()[0].equals("*")){
			String[] idlist = QP.getIDSelection();
			Object id = null;
			try{
				id = jsonObject.get("ID");
			}catch(JSONException e){
				e.getMessage();
			}
			if(id != null){
				if(id.getClass().getSimpleName().equals("String")){
					String id_str = id.toString();
					idset = new HashSet<String>(Arrays.asList(idlist));
					if(idset.contains(id_str)){
						isValidID = true;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
				
		}else{
			isValidID = true;
		}

		return isValidID;
	}
	
	/**
	 * method for extract tree, with storing those path-object(s) corresponding to each Epsilon
	 * @param key, the key of this json object. It is "*" for tree-root
	 * @param jsonObj, Object
	 * @param branchRoot, the root of this Query Tree Pattern
	 * @param isRetrieveTree, to indicate if this "matching" is for Retrieve tree, and true if it is.
	 * @param pathForEpsilon, used for getting the path of each successfully matched Epsilon. null as default
	 * @return
	 */
	public boolean treeMatch(String key, Object jsonObj, QueryTreeNode branchRoot, boolean isRetrieveTree, String pathForEpsilon){
		boolean matched = false;
			QueryTreeNode child = branchRoot;
			String Epath = new String();
			// to construct the path of this object
			if(pathForEpsilon == null){
				Epath = key;
			}else{
				Epath = pathForEpsilon + "/" + key;
			}
			if(key.equals("")){
				Epath = pathForEpsilon;
			}
			
			String objClass = jsonObj.getClass().getSimpleName();
			//the query node is not a "leaf", but there is no child-elements for this json object
			if(!objClass.equals("JSONObject") && !objClass.equals("JSONArray") && !child.getChildren().isEmpty()){
				return false;
			}
			//the query node is a "leaf"
			if(child.getChildren().size() == 0){
				matched = match(child, key, jsonObj, isRetrieveTree, Epath);
			}
			//the query node has one or more branches.
			if(child!=null && child.getChildren().size()>0){
				if(match(child, key, jsonObj,isRetrieveTree, Epath)){
					//the child is a Epsilon and it is matched to current json object(using match() method)
					if(child.getTag().equals("ε")){
						return true;
					}else{
						int index = 0;
						for(QueryTreeNode ch : child.getChildren()){
							if(ch.getTag().equals("ε")){
								if(jsonObj.getClass().getSimpleName().equals("JSONArray")){
									if(match(ch, "", jsonObj, isRetrieveTree, Epath)){
										return true;
									}
								}else{
									return false;
								}
							}else{
								if(jsonObj.getClass().getSimpleName().equals("JSONObject")){
									JSONObject jsObj = (JSONObject)jsonObj;
									@SuppressWarnings("unchecked")
									Iterator<String> it = jsObj.keys();
									while (it.hasNext()) {
										String kk = it.next();
										if(treeMatch(kk, jsObj.get(kk), ch, isRetrieveTree, Epath)){
											index++;
											if(!ch.getTag().equals("*")){
												break;
											}
								}
							}
							}
							}
						}
						if(index >= child.getChildren().size()){
							return true;
						}else{
							return false;
						}
					}
				}else{
					return false;
				}
			
			}
		return matched;
	}
	

	/**
	 * to match a doc-node(JSONObject, JSONArray, Integer, String, ...) to a tree pattern node.
	 * Notice:
	 * for now, only the objects of above 4 types are considered in.
	 * @param qtn, the to-be matched QueryTreeNode
	 * @param key, the key of this json object(jsObj)
	 * @param jsObj, this json object
	 * @param isRetrieveTree, also, this param is used to indicate if this matching is for Retrieve Tree.
	 * @param Epath, the path of Epsilon
	 * @return
	 */
	public boolean match(QueryTreeNode qtn, String key, Object jsObj, boolean isRetrieveTree, String Epath){
		
		//here, we suppose there is only "=" predicate in query, and which is
		//only for Integer or String value of node
		if(qtn.getTag().equals("*")){
			if(qtn.isReturned && !isRetrieveTree){
				this.returnedObjects.add(jsObj);
				this.NamesOfreturnedObjects.add(key);
			}
			return true;
		}else if(key.equals(qtn.getTag())){
			if(qtn.selectsValue()){
				if(qtn.getStringValue().isEmpty()){
					//here, notice the type of figure value: Integer, Double, ...
					if(jsObj.getClass().getSimpleName().equals("Double")){
						if(qtn.getDoubleValue().equals(jsObj)){
							if(qtn.isReturned && !isRetrieveTree){
								this.returnedObjects.add(jsObj);
								this.NamesOfreturnedObjects.add(key);
							}
							return true;
						}else{
							return false;
						}
					}else{
						return false;
					}
				}else{
					if(jsObj.getClass().getSimpleName().equals("String")){
						if(qtn.getStringValue().equals(jsObj)){
							if(qtn.isReturned && !isRetrieveTree){
								this.returnedObjects.add(jsObj);
								this.NamesOfreturnedObjects.add(key);
							}
							return true;
						}else{
							return false;
						}
					}else{
						return false;
					}
				}
				
			}else{
				if(qtn.isReturned && !isRetrieveTree){
					this.returnedObjects.add(jsObj);
					this.NamesOfreturnedObjects.add(key);
				}
				return true;
			}
		}else if(qtn.getTag().equals("ε")){
				if(!isRetrieveTree){
					if(key.equals("") && jsObj.getClass().getSimpleName().equals("JSONObject")){
						JSONObject jo = (JSONObject)jsObj;
						String kk = new String();
						if(qtn.getChildren().size() == 1){
							kk = qtn.getChildren().get(0).getTag();
							Object obj = jo.get(kk);
							boolean m = treeMatch(kk, obj, qtn.getChildren().get(0), isRetrieveTree, Epath+"/"+kk);
							return m;
						}
						
						
					}
				}
				if(jsObj.getClass().getSimpleName().equals("JSONArray")){
					if (arrayEpsilonMatch(qtn, jsObj, isRetrieveTree, Epath)){
						if(qtn.isReturned && !isRetrieveTree){
							this.returnedObjects.add(jsObj);
							this.NamesOfreturnedObjects.add(key);
						}
						return true;
					}
				}else{
					return false;
				}
			}
		return false;
	}
	
	
	/**
	 * to check the Epsilon's children nodes are matched with (at least) one element of this JSONArray
	 * 
	 * @param qtn, the to-be matched QueryTreeNode(whose tag is "ε" actually.)
	 * @param jsObj, the json Object is to-be matched to above node.
	 * @param isRetrieveTree, to indicate if this "matching" is for Retrieve tree
	 * @param Epath, the path of Epsilon
	 * @return
	 */
	public boolean arrayEpsilonMatch(QueryTreeNode qtn, Object jsObj, boolean isRetrieveTree, String Epath){
		/*
		 * an ArrayList to store all those JSONArray elements which are matched with (every child node of)Epsilon
		 */
		ArrayList<Object> matchedElements = new ArrayList<Object>();
		boolean matched = false;
		if(!isRetrieveTree){
			if(this.objsForEpsilon.containsKey(Epath+"/ε")){
				//System.out.println("there is a path for Epsilon in the hashmap: "+Epath+"/ε");
				boolean eleMatched = false;
				for(Object obj : this.objsForEpsilon.get(Epath+"/ε")){
					if(treeMatch("", obj, qtn, false, Epath+"/ε"))
						eleMatched = true;
				}
				return eleMatched;
			}
		}
		if((jsObj instanceof JSONArray) && !this.objsForEpsilon.containsKey(Epath+"/ε")){
			JSONArray jsArr = (JSONArray) jsObj;
			for (int i = 0; i < jsArr.length(); i++) {
				// used to count the number of sub-trees matched to query-node's children
				int index = 0;
				Object element = jsArr.get(i);
				for(QueryTreeNode node : qtn.getChildren()){
					if(element.getClass().getSimpleName().equals("JSONObject")){
						JSONObject jobj = (JSONObject)element;
						@SuppressWarnings("unchecked")
						Iterator<String> it = jobj.keys();
						while (it.hasNext()) {
							String key = it.next();
							boolean isEx;
							if(isRetrieveTree){
								isEx = treeMatch(key, jobj.get(key), node, true, Epath+"/ε");
							}else{
								isEx = treeMatch(key, jobj.get(key), node, false, Epath+"/ε");
							}
							if(isEx){
								index++;
								break;
							}
							
						}
					}
					if(element.getClass().getSimpleName().equals("JSONArray") && node.getTag().equals("ε")){	
						if(match(node, "", element, isRetrieveTree, Epath)){
							index++;
						}	
					}
					
					
				}
				// if index == qtn.getChildren().size(), this element is matched with Epsilon
				if(index == qtn.getChildren().size()){
					matchedElements.add(element);
				}
				
			}
			
		}else{
			return false;
		}
		
		if(matchedElements.size()>0){
			matched = true;
			if(isRetrieveTree){
			for(Object obj : matchedElements){
				addObjectToEpsilon(Epath+"/ε", obj);
			}
			}
		}
		
		return matched;
	}
	
	
	/**
	 * to get the returns from extract tree pattern(s)
	 * @output the results returned will be stored in this.allReturnedObjects
	 */
	public void getExtractReturns(){
		//firstly, to check if the doc's ID is in the selectionID list.
		if(this.checkID(this.currentQP, this.docObj)){
			//then, to check if the retrieve tree is matched.
			boolean matched = this.treeMatch("*", this.docObj, this.currentQP.root, true, null);
			//System.out.println("are they matched? "+matched);
			
			if(matched){
				//for each extract tree, to check and get the matched result
				for(QueryTreeNode ext : this.currentQP.getRoot().getExtractNodes()){
					QueryTreeNode root = this.currentQP.getRoot();
					QueryTreeNode newRoot = new QueryTreeNode(root.getCollectionName(), root.getTag(), null, null, null, root.isReturned);
					QueryTreeEdge newEdge = new QueryTreeEdge(newRoot, ext, true);
					newRoot.clearEdges();
					newRoot.addEdge(newEdge);
					boolean m = this.treeMatch("*", this.docObj, newRoot, false, null);
					if(m){
						ArrayList<Object> newReturned = new ArrayList<Object>(this.returnedObjects);
						this.allReturnedObjects.add(newReturned);
					}else{
						//to indicate there is no return on this extract-tree
						ArrayList<Object> NullReturned = new ArrayList<Object>();
						this.allReturnedObjects.add(NullReturned);
					}
					this.returnedObjects.clear();
					this.NamesOfreturnedObjects.clear();
				}
				
			}
			}
	}

	/**
	 * to get the returns(with extract-node names) from extract tree pattern(s)
	 * @output the results returned will be stored in this.allReturnedObjectsWithNames
	 */
	public void getExtractReturnsWithNames(){
		//firstly, to check if the doc's ID is in the selectionID list.
		if(this.checkID(this.currentQP, this.docObj)){
			//then, to check if the retrieve tree is matched.
			boolean matched = this.treeMatch("*", this.docObj, this.currentQP.root, true, null);
			//System.out.println("are they matched? "+matched);
			
			if(matched){
				//for each extract tree, to check and get the matched result
				for(QueryTreeNode ext : this.currentQP.getRoot().getExtractNodes()){
					QueryTreeNode root = this.currentQP.getRoot();
					QueryTreeNode newRoot = new QueryTreeNode(root.getCollectionName(), root.getTag(), null, null, null, root.isReturned);
					QueryTreeEdge newEdge = new QueryTreeEdge(newRoot, ext, true);
					newRoot.clearEdges();
					newRoot.addEdge(newEdge);
					boolean m = this.treeMatch("*", this.docObj, newRoot, false, null);
					if(this.returnedObjects.size() != this.NamesOfreturnedObjects.size()){
						System.out.println("the number of extract-node names should be equal to returned objects! ");
						return;
					}
					if(m){
						ArrayList<String> newReturned = new ArrayList<String>();
						for(int i=0; i<this.returnedObjects.size(); i++){
							String objWithName = new String(this.NamesOfreturnedObjects.get(i)+":"+this.returnedObjects.get(i).toString());
							newReturned.add(objWithName);
						}
						this.allReturnedObjectsWithNames.add(newReturned);
					}else{
						//to indicate there is no return on this extract-tree
						ArrayList<String> NullReturned = new ArrayList<String>();
						NullReturned.add(":");
						this.allReturnedObjectsWithNames.add(NullReturned);
					}
					this.returnedObjects.clear();
					this.NamesOfreturnedObjects.clear();
				}
				
			}
			}
	}
	
	
	/**
	 * to extract the matching result for a json file to a query
	 * Notice: each line is for a json object in the file
	 * @param fileName
	 * @param qtp, QueryTreePattern
	 * @return a HashMap to store those matched results, its keys is IDs of those matched json objects, 
	 * and values from getExtractReturns()
	 */
	public HashMap<String, ArrayList<ArrayList<Object>>> toExtractJsonFile(String fileName, QueryTreePattern qtp){
		//to store the extraction result
		//Notice: later, these result should be constructed using Tuples
		HashMap<String, ArrayList<ArrayList<Object>>> result = new HashMap<String, ArrayList<ArrayList<Object>>>();
		this.currentQTP = qtp;
		try{
	        FileInputStream fis;
			try {
				fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF8");
				BufferedReader in = new BufferedReader(isr);
				
				String line="";
	            while ((line=in.readLine())!=null) {
	            	this.objsForEpsilon.clear();
	            	this.docObj = this.getJSONObjectFromString(line);
	        		this.getExtractReturns();
	        		if(this.allReturnedObjects.size()>0){
	        			String ID = this.getID(this.docObj);
	        			ArrayList<ArrayList<Object>> objs = new ArrayList<ArrayList<Object>>(this.allReturnedObjects);
	        			result.put(ID, objs);
	        		}
	        		this.allReturnedObjects.clear();
	            	
	            	
	            }	
	            in.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * to extract the matching result for a json file to a query
	 * Notice: each line is for a json object in the file
	 * @param fileName
	 * @param qtp, QueryTreePattern
	 * @param withNames, to indicate the if the query results(allReturnedObjectsWithNames) contain node-names
	 * @return a HashMap to store those matched results, its keys is IDs of those matched json objects, 
	 * and values from getExtractReturns()
	 */
	public HashMap<String, ArrayList<ArrayList<String>>> toExtractJsonFile(String fileName, QueryTreePattern qtp, boolean withNames){
		//to store the extraction result
		//Notice: later, these result should be constructed using Tuples
		HashMap<String, ArrayList<ArrayList<String>>> resultWithNames = new HashMap<String, ArrayList<ArrayList<String>>>();
		this.currentQTP = qtp;
		try{
	        FileInputStream fis;
			try {
				fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF8");
				BufferedReader in = new BufferedReader(isr);
				
				String line="";
	            while ((line=in.readLine())!=null) {
	            	this.objsForEpsilon.clear();
	            	this.docObj = this.getJSONObjectFromString(line);
	            	if(withNames){
	            		this.getExtractReturnsWithNames();
	            		if(this.allReturnedObjectsWithNames.size()>0){
		        			String ID = this.getID(this.docObj);
		        			ArrayList<ArrayList<String>> objs = new ArrayList<ArrayList<String>>(this.allReturnedObjectsWithNames);
		        			resultWithNames.put(ID, objs);
		        		}
		        		this.allReturnedObjectsWithNames.clear();
	            	}
	            	
	            }	
	            in.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultWithNames;
	}
	
	/*
	 * for test:
	 * args[0]: the name or path of query file, "src/resource/query03", etc.
	 * args[1]: the name or path of json file, "src/resource/cars_test", etc.
	 */
	public static void main(String[] args) throws InvalidArgumentException {
		QueryTreePattern qtp;
		String fileName;
		if(args.length == 0){
			System.out.println("Error: there is no arguments for the command!");
			return;
		}
		if(args.length == 1){
			System.out.println("Error: the json file name or path is not provided!");
			return;
		}
		if(args.length == 2){
			qtp = new QueryTreePattern(args[0]);
			fileName = new String(args[1]);
		}else{
			System.out.println("Error: Too many arguments, only names of query file and json file are needed!");
			return;
		}
		//String jsonStr = new String("{\"ID\":\"1001\",\"model\":{\"name\":\"Clio IV\",\"manufacturer\":\"Renault\",\"year\":\"2013\"},\"color\":\"Blue\",\"owners\":[{\"first-name\":\"Will\",\"last-name\":\"Smith\"},{\"first-name\":\"Alice\",\"last-name\":\"Smith\",\"residence\":{\"city\":\"Rome\",\"Road\":\"Rd xxx\"},\"ownership\":{\"start-date\":\"2013-03-14\",\"end-date\":\"2013-03-14\",\"km\":\"1115\"}}]}");
		//SinglePatternExtractorForJson spefj = new SinglePatternExtractorForJson(qtp, jsonStr);
		SinglePatternExtractorForJson spefj = new SinglePatternExtractorForJson(qtp);
		//HashMap<String, ArrayList<ArrayList<Object>>> results = spefj.toExtractJsonFile(fileName, qtp);
		HashMap<String, ArrayList<ArrayList<String>>> results = spefj.toExtractJsonFile(fileName, qtp, true);
		System.out.println("There are "+results.size()+" json objects has the matched returns.");
		for(String ID : results.keySet()){
			System.out.println("#######");
			System.out.println("the ID of json Object that has matched returns: "+ID);
			int i = 1;
			for(ArrayList<String> os : results.get(ID)){
				System.out.println("the #"+i+" extract tree:");
				for(String obj : os){
					System.out.println(obj);
				}
				i++;
			}
		}
		
	}
	
	
	

}
