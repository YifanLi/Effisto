package fr.inria.oak.effisto.IDs;

import java.util.Stack;

/**
 * Ordered integer element ID scheme.
 * 
 * @author Ioana MANOLESCU
 * 
 * @created 13/06/2005
 */
public class OrderedIntegerIDScheme implements IDScheme {
	
	int n;

	OrderedIntegerElementID ied;

	Stack<OrderedIntegerElementID> s;
	
	private static String nullIDStringImage = "null";
	
	public OrderedIntegerIDScheme() {
		n = 0;
		ied = null;
		s = new Stack<OrderedIntegerElementID>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#isOrderPreserving()
	 */
	public final boolean isOrderPreserving() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#isParentAncestorPreserving()
	 */
	public final boolean isParentAncestorPreserving() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#allowsParentNavigation()
	 */
	public final boolean allowsParentNavigation() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#allowsUpdates()
	 */
	public final boolean allowsUpdates() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#initDocument()
	 */
	public final void beginDocument() {
		n = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#initElement()
	 */
	public final void beginNode() {
		s.push(new OrderedIntegerElementID(n));
		n ++;
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.inria.gemo.vip2p.IDs.IDScheme#beginNode(java.lang.String)
	 */
	public final void beginNode(String tag) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#endElement()
	 */
	public final void endNode() {
		ied = (OrderedIntegerElementID)(s.pop());
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#endDocument()
	 */
	public final void endDocument() {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.gemo.uload.IDs.IDScheme#getLastID()
	 */
	public final ElementID getLastID() {
		return ied;
	}
	
	public final String getSignature(String suffix){
		return new String("ID" + suffix + " int");
	}
	
	public final String nullIDStringImage(){
		return nullIDStringImage;
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#getIndexSignature(java.lang.String)
	 */
	public String getIndexSignature(String suffix) {
		return ("ID" + suffix);
	}

}