package fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern;

import fr.inria.oak.effisto.Query.TreePattern.TreePattern;

public class QueryExtractTreePattern extends TreePattern{
	
	public String extractClause = null;
	public String CollectionName = null;
	
	public QueryTreeNode root = null;
	public QueryTreeNode child = null;
	//public QueryTreeNode returnedNode = null;
	
	public QueryExtractTreePattern(QueryTreeNode r, QueryTreeNode c, String ec, String cn) {
		this.root = r;
		this.child = c;
		this.extractClause = ec;
		this.CollectionName = cn;
		//this.returnedNode = rdNode;
	}

}
