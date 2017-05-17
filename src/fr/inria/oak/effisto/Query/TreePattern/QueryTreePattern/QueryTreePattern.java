package fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern;

//import PredicateType;
import fr.inria.oak.effisto.Query.Common.Predicates.BasePredicate.PredicateType;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import fr.inria.oak.effisto.Query.TreePattern.TreePattern;

public class QueryTreePattern extends TreePattern{
	
	//to store the nodes and edges of "retrieve" tree pattern
	public ArrayList<QueryTreeNode> Rnodes = new ArrayList<QueryTreeNode>();
	public ArrayList<QueryTreeEdge> Redges = new ArrayList<QueryTreeEdge>();
	
	//to store the nodes and edges of "extract" tree patterns
	public ArrayList<QueryTreeNode> Enodes = new ArrayList<QueryTreeNode>();
	public ArrayList<QueryTreeEdge> Eedges = new ArrayList<QueryTreeEdge>();
	
	//the count of tree patterns in "extract" clause
	public int numOfExtractTrees = 0;
	
	private QueryRetrieveTreePattern qrtp = null;
	private ArrayList<QueryExtractTreePattern> qetpl = new ArrayList<QueryExtractTreePattern>();
	
	private String retrieveClause = null;
	private String extractClause = null;
	private String CollectionName = null;
	private String[] IDSelection = null;
	private ArrayList<String> returnedNodes = new ArrayList<String>();
	
	public QueryTreePattern(String queryFilePath){
		this.getQueryTreePatternFromFile(queryFilePath);
		this.numOfExtractTrees = this.qetpl.size();
		
	}
	
	public void getQueryTreePatternFromFile(String filePath){
		this.parseQueryFile(filePath);
		
		this.getRetrievePattern();
		this.getExtractPattern();
		
		
	}
	
	public void getRetrievePattern(){
		QueryTreeNode root = null;
		for(QueryTreeNode qtn: this.Rnodes){
			if(qtn.isRoot){
				root = qtn;
			}
		}
		this.qrtp = new QueryRetrieveTreePattern(root, this.retrieveClause, this.CollectionName, this.IDSelection);
		
	}
	
	public void getExtractPattern(){
		for(QueryTreeNode qtn: this.Rnodes){
			if(qtn.checkIsExtractRoot()){
				//QueryTreeNode returnedNode = null;
				//String t = qtn.getNodeCode().split("_")[1];
				for(QueryTreeNode qtn2: qtn.getExtractNodes()){
					QueryExtractTreePattern e = new QueryExtractTreePattern(qtn, qtn2, this.extractClause, this.CollectionName);
					this.qetpl.add(e);
					}
			}
		}
		
	}
	

	
	public void parseQueryFile(String filePath){
		
		File file = new File(filePath);
		if(!file.exists() || !file.isFile()){
			return;
		}
		
		int index = 0;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			//InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line = "";
			while ((line = reader.readLine()) != null) {
				if(line.startsWith("retrieve")){
					this.retrieveClause = line;
				}else if(line.startsWith("extract")){
					this.extractClause = line;
				}else if(line.startsWith("CollectionName")){
					this.CollectionName = line.split(":")[1];				
				}else if(line.startsWith("IDSelection")){
					this.IDSelection = (line.split(":")[1]).split(",");
				}else if(line.startsWith("RETRIEVE")){
					line=reader.readLine();
					if(line.startsWith("Rnode") && line.split(" ").length == 2){
						String root_tag = new String("*");
						QueryTreeNode root = new QueryTreeNode(this.CollectionName, root_tag, null, null, null, false);
						root.isRoot = true;
						Rnodes.add(root);
					}
					while(!(line = reader.readLine()).equals("EXTRACT")){
						if(line.startsWith("Rnode")){
							String tag = null;
							if(line.split(" ").length == 2){
								tag = "*";
							}else{
							tag = line.split(" ")[2].substring(6, line.split(" ")[2].length()-2);
							}
							String value = null;
							PredicateType p = null;
							if(line.split(" ").length >3 && line.split(" ")[3].startsWith("[Val")){
								value = line.split(" ")[3].substring(5, line.split(" ")[3].length()-1);
								if(line.split(" ").length >4 && line.split(" ")[4].startsWith("[BP")){
									String sp = line.split(" ")[4].substring(5, line.split(" ")[4].length()-2);
									p = this.checkPredicateType(sp);
								}
							}
							
							Rnodes.add(new QueryTreeNode(this.CollectionName, tag, null, value, p, false));
							
						}else if(line.startsWith("Redge")){
							QueryTreeNode n1=null;
							QueryTreeNode n2=null;
							
							String[] pair = line.split(" ")[1].split(",");
							n1 = Rnodes.get(Integer.parseInt(pair[0]));
							n2 = Rnodes.get(Integer.parseInt(pair[1]));
							
							Redges.add(new QueryTreeEdge(n1, n2, true));
							n1.addEdge(Redges.get(Redges.size()-1));
						}
						
					}
					
				}
				
				if(line.startsWith("Enode")){
					String tmp = new String();
					while(line != null && line.startsWith("Enode")){
					tmp = line;
					String tag = null;
					if(line.split(" ").length == 2){
						tag = "*";
					}else{
						tag = line.split(" ")[2].substring(6, line.split(" ")[2].length()-2);
					}
					boolean isReturned = false;
					if(line.split(" ").length == 4 && line.split(" ")[3].equals("[return]")){
						isReturned = true;
						this.returnedNodes.add(tag);
					}
					
					Enodes.add(new QueryTreeNode(this.CollectionName, tag, null, null,null, isReturned));
					line = reader.readLine();
					}
					
					if((line == null || line.isEmpty() || line.startsWith(";")) && tmp.startsWith("Enode")){
						System.out.println("^^^^^^^");
						QueryTreeNode n1=null;
						QueryTreeNode n2=null;
						int i = index;
						n1 = Rnodes.get(0);
						n2 = Enodes.get(i);
						Eedges.add(new QueryTreeEdge(n1, n2, true));
						index++;
						n1.setExtractRoot();
						n1.addExtractNodes(n2);
						
					}
				}
				
				if(line != null && line.startsWith("Eedge")){
					QueryTreeNode n1=null;
					QueryTreeNode n2=null;
					int i = index;
					String[] pair;
					//String[] pair = line.split(" ")[1].split(",");
					//n1 = Rnodes.get(Integer.parseInt(pair[0]));
					//n2 = Enodes.get(Integer.parseInt(pair[1])+i);
					n1 = Rnodes.get(0);
					n2 = Enodes.get(i);
					Eedges.add(new QueryTreeEdge(n1, n2, true));
					index++;
					n1.setExtractRoot();
					//System.out.println(Eedges.get(Eedges.size()-1).n1.getTag());
					//System.out.println(Eedges.get(Eedges.size()-1).n2.getTag());
					System.out.println("$$$$"+n2.getTag());
					n1.addExtractNodes(n2);

					while(line!=null && line.startsWith("Eedge")){
						pair = line.split(" ")[1].split(",");
						n1 = Enodes.get(Integer.parseInt(pair[0])+i);
						n2 = Enodes.get(Integer.parseInt(pair[1])+i);
						Eedges.add(new QueryTreeEdge(n1, n2, true));
						index++;
						n1.addEdge(Eedges.get(Eedges.size()-1));
						System.out.println("finished!");
						line = reader.readLine();
					}
					
					
				}
				
				
				System.out.println(line);
			}
			
			fileInputStream.close();
			inputStreamReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public PredicateType checkPredicateType(String sp){
		if(sp.equals("=")) {
			return PredicateType.PREDICATE_EQUAL;
		}else if(sp.equals("!=")){
			return PredicateType.PREDICATE_NOTEQUAL;
		}else if(sp.equals("<=")){
			return PredicateType.PREDICATE_SMALLEROREQUALTHAN;
		}else if(sp.equals("<")){
			return PredicateType.PREDICATE_SMALLERTHAN;
		}else if(sp.equals(">=")){
			return PredicateType.PREDICATE_GREATEROREQUALTHAN;
		}else if(sp.equals(">")){
			return PredicateType.PREDICATE_GREATERTHAN;
		}else{
			return null;
		}
	
		
	}
	
	/*
	 * to print the retrieve-clause tree pattern
	 */
	public void printRetrieveTree(){
		
		for(QueryTreeNode x: this.Rnodes){
			System.out.println("tag:"+ x.getTag() + " |"+" nodeCode:"+x.getNodeCode());
			System.out.println("number of nodes of this subtree: "+x.getNumberOfNodes());
			if(x.getSelectOnValue()){
				if(x.getStringValue() != null){
					System.out.println("StringValue: "+x.getStringValue());
				}
				if(x.getDoubleValue() != null){
					System.out.println("DoubleValue: "+x.getDoubleValue().toString());
				}
			}else{
				System.out.println("No value on this node!");
				}
				
			if(x.getPredicateType() != null){
				System.out.println("predicateType: "+x.getPredicateType().toString());
			}else{
				System.out.println("No predicate on this node!");
			}
			for(QueryTreeEdge e: x.getEdges()){
				System.out.println("child: "+e.n2.getNodeCode() + ", tag:" + e.n2.getTag());
				
			}
			
		}
		System.out.println("=====");
		for(QueryTreeNode eRoot: this.Rnodes.get(0).getExtractNodes()){
			System.out.println("the extract tree(s) roots");
			System.out.println("Tag: "+eRoot.getTag());
		}
		
		
	}
	
	/*
	 * to print the extract-clause tree pattern(s)
	 */
	public void printExtractTree(){
		System.out.println(this.numOfExtractTrees);
		
		for(QueryTreeNode x: this.Enodes){
			
			System.out.println("tag:"+ x.getTag() + " |"+" nodeCode:"+x.getNodeCode());
	
			for(QueryTreeEdge e: x.getEdges()){
				System.out.println("child: "+e.n2.getNodeCode() + ", tag:" + e.n2.getTag());
				
			}
			if(x.isReturned){
				System.out.println("It should be returned!");
			}
			System.out.println("------");
			
		}
		
		
	}
	
	/*
	 * return those returned nodes's tags of this Query Tree pattern
	 */
	public ArrayList<String> getReturnedNodes(){
		return this.returnedNodes;
	}
	
	/*
	 * return the Retrieve Tree pattern
	 */
	public QueryRetrieveTreePattern getRetrieveTreePattern(){
		return this.qrtp;
	}
	
	/*
	 * return the Extract Tree pattern(s)
	 */
	public ArrayList<QueryExtractTreePattern> getExtractTreePatternS(){
		return this.qetpl;
	}
	
	
	
	
	/*
	 * for test:
	 */
	public static void main(String[] args){
		QueryTreePattern qtp = new QueryTreePattern("src/resource/query03");
		System.out.println("@@@@@@@@@@@@@@");
		qtp.printRetrieveTree();
		System.out.println("##############");
		qtp.printExtractTree();
	}

}
