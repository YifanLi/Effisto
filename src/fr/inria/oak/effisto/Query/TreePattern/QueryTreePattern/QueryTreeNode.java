package fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern;

//import PredicateType;

import fr.inria.oak.effisto.Query.Common.Predicates.BasePredicate.PredicateType;

import java.util.ArrayList;
import java.util.Iterator;

public class QueryTreeNode {
	
	/*Collection info(to identify this node)*/
	private String CollectionName;

	/* Node related properties */
	
	/**
	 * If true, this node represents an Element.
	 */
	private boolean isElement;
	
	/**
	 * If true, this node represents an Epsilon ε.
	 */
	private boolean isEpsilon;
	
	/**
	 * If true, this node represents a Star *.
	 */
	private boolean isStar;
	
	/**
	 * If true, this node is a root.
	 */
	public boolean isRoot;
	
	/**
	 * If true, this node is a leaf.
	 */
	private boolean isLeaf;
	
	/**
	 * If true, this node is set returned in "extract ..." clause of this query .
	 */
	public boolean isReturned;
	
	
	/**
	 * Here, we use the integer nodeCode following the syntax using in XAM(also included in ViP2P and PAXQuery),
	 * by the hashCode(). 
	 */
	private Integer nodeCode;
	
	/* ID related properties */
	// ID is to be discussed in Effisto.
	
	/* Tag related properties */
	
	/**
	 * If true, the tag of this element is stored.
	 */
	private boolean storesTag;	
	
	/**
	 * If true, the Tag of this element is (R)equired, must be known in order to access
	 * the data stored in the xam.
	 */
	private boolean requiresTag;
	
	/**
	 * The tag of this node ([Tag="a"]), "" for the root, "ε" for the Epsilon, "*" for the Star.
	 */
	private String tag;
	
	/**
	 * If true, there is a selection on tag.
	 */
	private boolean selectOnTag;
	
	/* ID related properties */
	/**
	 * If true, the ID of this element is needed. When {@link #storesID} is set to
	 * true, needs ID is always set to true.
	 */
	private boolean storesID;
	
	/**
	 * If true, the ID of this element is (R)equired, must be known in order to access
	 * the data stored in the xam.
	 */
	private boolean requiresID;
	
	/**
	 * If true, "ID i" was specified for this node in the XAM file, so the identity ID for
	 * this node should be stored. 
	 */
	private boolean identityID;

	/**
	 * If true, "ID o" was specified for this node in the XAM file, so the order preserving ID for
	 * this node should be stored. 
	 */
	private boolean orderID;

	/**
	 * If true, "ID s" was specified for this node in the XAM file, so the structural ID for
	 * this node should be stored. 
	 */
	private boolean structID;

	/**
	 * If true, "ID u" was specified for this node in the XAM file, so the update ID for
	 * this node should be stored. 
	 */
	private boolean updateID;
	
	/* Value related properties */
	/**
	 * If true, the value of this element is stored.
	 */
	private boolean storesValue;	
	
	/**
	 * If true, the value of this element is Required, must be known in order to access
	 * the data stored in the doc or collection.
	 */
	private boolean requiresVal;
	
	//private PredicateType selectOnValuePredicate;
	
	/**
	 * If true, there is a selection on value.
	 */
	private boolean selectOnValue;
	
	/**
	 * If selectOnValue is true, the predicate on it is stored
	 * Notice: here, only base predicates are introduced so far.
	 */
	private PredicateType selectOnValuePredicate;

	/**
	 * If selectOnValue is true, this is what the value should be ([Val="a"]).
	 * Notice: default value is null
	 */
	private String stringValue = new String();
	
	/**
	 * If selectOnValue is true, this is what the value should be ([Val=a]).
	 * Notice: default value is 0.0d
	 */
	private double doubleValue = 0.0d;
	
	/* Content related properties */
	/**
	 * If true, the content of this element is stored.
	 */
	private boolean storesContent;

	/* Edges to other nodes */
	/**
	 * Edge connecting this node to its parent.
	 */
	private QueryTreeEdge parentEdge;
	
	/**
	 * Edges connecting this node to its children.
	 */
	private ArrayList<QueryTreeEdge> edges;
	
	
	/**
	 * If true, the node is root of those extract trees.
	 */
	private boolean isExtractRoot;
	
	/**
	 * Edges connecting this node to its Extract-node(s) children if they exist.
	 */
	private ArrayList<QueryTreeNode> extractNodes;
	
	
	
	/**
	 * Creates a new node with no children. some properties are set to false
	 * by default(manually or from JAVA compiler).
	 * 
	 * @param tag, the tag associated to this node
	 * @param nodeCode, the code associated to this node
	 * @param valueWithQuotes, the value of this node(only for element), "Paris", 1200, etc.
	 */
	public QueryTreeNode(String collName, String tag, Integer nodeCode, String valueWithQuotes, PredicateType predicate, boolean isReturned) {
		this.CollectionName = collName;
		this.tag = tag;
		if(tag.equals("*")){
			this.selectOnTag = true;
		}
		if(tag != null){
		this.storesTag = true;
		}
		if(tag.equals("ε")){
			this.isEpsilon = true;
		}
		
		if(nodeCode == null){
			int hash = 10;
			this.nodeCode = hash + this.hashCode();
		}else{
			this.nodeCode = nodeCode;
		}
		
		if (valueWithQuotes != null) {
			//for now, we suppose there would be only "=" predicate in the retrieve clause.
			this.selectOnValue = true;
			this.selectOnValuePredicate = predicate;
			this.storesValue = true;
			this.requiresVal = false;
			
			if(valueWithQuotes.startsWith("\"")){
				this.stringValue = valueWithQuotes.substring(1,valueWithQuotes.length()-1);
			}
			else{
				this.doubleValue = Double.parseDouble(valueWithQuotes);
			}
		}
		
		if(isReturned){
			setReturned();
		}
		this.edges = new ArrayList<QueryTreeEdge>();
		this.extractNodes = new ArrayList<QueryTreeNode>();
		if(tag != null){
		this.requiresTag = false;
		this.storesID = false;
		}
		
		
	}
	
	
	/* Tag related getters/setters */
	/**
	 * If true, the tag of this element is stored.
	 * 
	 * @return the storesTag
	 */
	public boolean storesTag() {
		return this.storesTag;
	}
	
	/**
	 * If it is set to true, the tag of this element is stored. 
	 * 
	 * @param storesTag the storesTag to set
	 */
	public void setStoresTag(boolean storesTag) {
		this.storesTag = storesTag;
	}
	/**
	 * return the tag of this node.
	 */
	public String getTag() {
		return this.tag;
	}
	/**
	 * If true, there is a selection on tag.
	 * 
	 * @return the selectOnTag
	 */
	public boolean selectsTag() {
		return this.selectOnTag;
	}
	
	/**
	 * If true, the Tag of this element is (R)equired,must be known in order to access
	 * the data stored in the xam.
	 * @return the requiresID;
	 */
	public boolean requiresTag(){
		return this.requiresTag;
	}
	
	/**
	 * return the NodeCode of this node.
	 */
	public Integer getNodeCode() {
		return this.nodeCode;
	}
	
	/**
	 * check if the node is "*".
	 */
	public boolean isStar() {
		return this.isStar;
	}
	
	/**
	 * check if the node is Epsilon.
	 */
	public boolean isEpsilon() {
		return this.isEpsilon;
	}
	
	/**
	 * set the node isReturned.
	 */
	public void setReturned() {
		this.isReturned = true;
	}
	
	/**
	 * check the returned status of the node.
	 */
	public boolean checkReturned() {
		return this.isReturned;
	}
	
	/**
	 * add an edge to its edges
	 */
	public void addEdge(QueryTreeEdge e) {
		this.edges.add(e);
	}
	
	/**
	 * return all the edges of this node.
	 */
	public ArrayList<QueryTreeEdge> getEdges() {
		return this.edges;
	}
	
	/*
	 * set true if the node is root of extract-tree
	 */
	public void setExtractRoot(){
		this.isExtractRoot = true;
	}
	
	/*
	 * check if the node is root of extract-tree
	 */
	public boolean checkIsExtractRoot(){
		return this.isExtractRoot;
	}
	
	/**
	 * add an edge to its extract-tree node(s) edges
	 */
	public void addExtractNodes(QueryTreeNode e) {
		if(this.isExtractRoot){
			this.extractNodes.add(e);
		}else{
			throw new IllegalArgumentException("this node is not a root for extract-tree pattern!" + this.tag);
		}
	}
	
	/**
	 * return all the extract nodes of this root-node.
	 */
	public ArrayList<QueryTreeNode> getExtractNodes() {
		return this.extractNodes;
	}
	
	/**
	 * return the predicateType of this node, default null.
	 */
	public PredicateType getPredicateType() {
		return this.selectOnValuePredicate;
	}
	
	/**
	 * return the value-selected possibility of this node.
	 */
	public boolean getSelectOnValue() {
		return this.selectOnValue;
	}
	
	/**
	 * return the String value of this node.
	 * if not exist, return null
	 */
	public String getStringValue(){
		if(this.selectOnValue){
			return this.stringValue;
		}else{
			return null;
		}
		
	}
	
	/**
	 * return the double value of this node.
	 * Notice: default value is 0.0d
	 */
	public Double getDoubleValue(){
		if(this.selectOnValue && this.stringValue==null){
			return this.doubleValue;
		}else{
			return null;
		}
		
	}
	
	/**
	 * If true, the value of this element is (R)equired,must be known in order to access
	 * the data stored in the xam.
	 * @return the requiresVal;
	 */
	public boolean requiresVal(){
		return this.requiresVal;
	}
	
	/**
	 * Returns true if there is some required field underneath this node.
	 * 
	 * @return if there is some required field below
	 */
	public  boolean requiresSomething() {
		if (this.requiresID || this.requiresTag || this.requiresVal) {
			// Parameters.logger.info(this.tag + " requires id: " + this.requiresID
			// +
			// " requires tag: " + this.requiresTag + " requires value: " +
			// this.requiresValue);
			return true;
		}
		Iterator<QueryTreeEdge> it = edges.iterator();
		while (it.hasNext()) {
			if (((QueryTreeEdge) it.next()).n2.requiresSomething()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Counts the nodes in this subtree.
	 * 
	 * @return the number of xam nodes
	 */
	public int getNumberOfNodes() {
		if (this.edges == null) {
			return 1;
		}
		if (this.edges.size() == 0) {
			return 1;
		}
		int aux = 1;
		Iterator<QueryTreeEdge> it = edges.iterator();
		while (it.hasNext()) {
			aux += ((QueryTreeEdge) it.next()).n2.getNumberOfNodes();
		}
		return aux;
	}
	/*
	 * return the CollectionName for this node
	 * 
	 * @return the CollectionName
	 */
	public String getCollectionName(){
		return this.CollectionName;
	}
	
	/* Content related getters/setters */
	/**
	 * If true, the content of this element is stored.
	 * 
	 * @return the storesContent
	 */
	public boolean storesContent() {
		return this.storesContent;
	}
	
	/**
	 * If set to true, the content of this element is stored.
	 * 
	 * @param storesContent the storesContent
	 */
	public void setStoresContent(boolean storesContent) {
		this.storesContent = storesContent;
	}
	
	/* Value related getters/setters */
	/**
	 * If true, the value of this element is stored.
	 * 
	 * @return the storesValue
	 */
	public boolean storesValue() {
		return this.storesValue;
	}
	
	/**
	 * If set to true, the value of this element is stored.
	 * 
	 * @param storesValue the storesValue to set
	 */
	public void setStoresValue(boolean storesValue) {
		this.storesValue = storesValue;
	}
	
	/**
	 * If true, there is a selection on value.
	 * 
	 * @return the selectOnValue
	 */
	public boolean selectsValue() {
		return this.selectOnValue;
	}
	
	public PredicateType getSelectOnValuePredicate() {
		return this.selectOnValuePredicate;
	}

	/*
	 * get all the child nodes of this node
	 */
	public ArrayList<QueryTreeNode> getChildren(){
		ArrayList<QueryTreeNode> children = new ArrayList<QueryTreeNode>();
		if(this.edges.size()>0){
			for(QueryTreeEdge e : this.edges){
				children.add(e.getN2());
			}
		}
		return children;
	}
	
	/*
	 * to clear all the edges
	 */
	public void clearEdges(){
		this.edges.clear();
	}
	
	/* ID related getters/setters */
	/**
	 * If true, the ID of this element is needed. When {@link #storesID} is set to
	 * true, needs ID is always set to true.
	 * 
	 * @return the storesID
	 */
	public boolean storesID() {
		return this.storesID;
	}
	
	/**
	 * This method sets the variable {@link #storesID} to the given value.
	 * 
	 * @param storesID the storesID to set
	 */
	public void setStoresID(boolean storesID) {
		this.storesID = storesID;
	}
	
	/**
	 * If true, the ID of this element is (R)equired,must be known in order to access
	 * the data stored in the xam.
	 * @return the requiresID;
	 */
	public boolean requiresID(){
		return this.requiresID;
	}
	
	/**
	 * This method sets the variable {@link #requiresID} to the given value.
	 * 
	 * @param requireID the requireID to set
	 */
	public void setRequiresID(boolean requiresID){
		this.requiresID = requiresID;
	}
	
	/**
	 * If true, "ID i" was specified for this node in the XAM file, so the identity ID for
	 * this node should be stored. 
	 * 
	 * @return the identityID
	 */
	public boolean isIdentityIDType() {
		return this.identityID;
	}
	
	/**
	 * If true, "ID o" was specified for this node in the XAM file, so the order preserving ID for
	 * this node should be stored.
	 * 
	 * @return the orderID
	 */
	public boolean isOrderIDType() {
		return this.orderID;
	}
	
	/**
	 * If true, "ID s" was specified for this node in the XAM file, so the structural ID for
	 * this node should be stored. 
	 * 
	 * @return the structID
	 */
	public boolean isStructIDType() {
		return this.structID;
	}
	
	/**
	 * If true, "ID u" was specified for this node in the XAM file, so the update ID for
	 * this node should be stored. 
	 * 
	 * @return the updateID
	 */
	public boolean isUpdateIDType() {
		return this.updateID;
	}
	
	/**
	 * This method sets which type of ID will be stored for this node.
	 * 
	 * @param identityID to set
	 * @param orderID to set
	 * @param structID to set
	 * @param updateID to set
	 */
	public void setIDType(boolean identityID, boolean orderID, boolean structID, boolean updateID) {
		this.identityID = identityID;
		this.orderID = orderID;
		this.structID = structID;
		this.updateID = updateID;
	}
}
