package fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern;


import fr.inria.oak.effisto.Query.TreePattern.TreePattern;

public class QueryRetrieveTreePattern extends TreePattern{
	
	public String retrieveClause = null;
	public String CollectionName = null;
	public String[] IDSelection = null;
	
	public QueryTreeNode root = null;
	
	public QueryRetrieveTreePattern(QueryTreeNode r, String rc, String cn, String[] ids) {
		this.root = r;
		this.retrieveClause = rc;
		this.CollectionName = cn;
		this.IDSelection = ids;
	}
	
	/**
	 * Counts the number of nodes in this subtree. Does not count the root.
	 * 
	 * @return number of nodes in this tree pattern(except its root)
	 */
	public int getNodesNo() {
		return (root.getNumberOfNodes() - 1);
	}
	
	/**
	 * Returns the root/"top" node of this tree pattern
	 * 
	 * @return the root of this tree pattern
	 */
	public QueryTreeNode getRoot() {
		return root;
	}
	
	/*
	 * Returns IDSelection of this query retrieve pattern
	 * @return String[] IDSelection
	 */
	public String[] getIDSelection(){
		return IDSelection;
	}
	
	
	

}
