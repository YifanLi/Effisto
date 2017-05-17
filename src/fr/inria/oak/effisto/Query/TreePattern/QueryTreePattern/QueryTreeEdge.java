package fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern;

public class QueryTreeEdge {
	
	/**
	 * The "parent" (upper) node. Strictly speaking the edge may avoid storing
	 * the parent node since the parent node stores the edges. But it's more
	 * convenient this way.
	 */
	public QueryTreeNode n1;

	/**
	 * The "child" (lower) node.
	 */
	public QueryTreeNode n2;

	/**
	 * If false, the edge is ancestor-descendant, otherwise it is parent-child.
	 */
	private boolean parent;
	
	/**
	 * If true, the join is nested with the child being nested under the parent.
	 */
	private boolean nested;	

	
	public QueryTreeEdge(QueryTreeNode n1, QueryTreeNode n2,
		boolean parent) {
		this.n1 = n1;
		this.n2 = n2;
		this.parent = parent;
	}

	public QueryTreeNode getN1() {
		return this.n1;
	}


	public QueryTreeNode getN2() {
		return this.n2;
	}

	public boolean isNested(){
		return this.nested;
	}

	/**
	 * If false, the edge is ancestor-descendant edge, 
	 * otherwise it is parent-child edge.
	 * @return
	 */
	public boolean isParent() {
		return this.parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public static String display(QueryTreeEdge parentEdge) {
		StringBuffer sb = new StringBuffer();
		if (parentEdge == null){
			sb.append("null");
		}
		else{
			if (parentEdge.n1 != null){
				sb.append(parentEdge.n1.getTag());
			}
			else{
				sb.append("null");
			}
			if (parentEdge.isParent()){
				sb.append("/");
			}
			else{
				sb.append("//");
			}
			
			sb.append(parentEdge.n2.getTag() + " (" + (parentEdge.n1 == null?"":parentEdge.n1.getNodeCode()) + " " + parentEdge.n2.getNodeCode() + ")");
		}
		return new String(sb);
	}

}
