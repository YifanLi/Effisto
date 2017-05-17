package fr.inria.oak.effisto.IDs;
/**
 * Interface that will be implemented by classes that represent different ID schemes.
 * 
 * @author Ioana MANOLESCU
 * 
 * @created 13/06/2005
 */
public interface IDScheme {
	
	/**
	 * e.g. 1, 2, 3...
	 * @return true if this scheme is order preserving
	 */
	public boolean isOrderPreserving();
	
	
	/**
	 * e.g. (pre, post, depth)
	 * @return true if the scheme allows to infer parent-child relationships
	 */
	public boolean isParentAncestorPreserving();
	
	/**
	 * e.g. Dewey
	 * @return true if the scheme allows parent navigation
	 */
	public boolean allowsParentNavigation();
	
	/**
	 * e.g. Dewey or floating point (pre, post)
	 * @return true if updates are possible
	 */
	public boolean allowsUpdates();

	/**
	 * To be called when a document starts, for possible initialization
	 */
	public void beginDocument();
	
	/** 
	 * To be called when an element or attribute starts. The convention is that this is called in 
	 * element pre-order.
	 */
	public void beginNode();
	
	/** 
	 * To be called when an element or attribute starts. The convention is that this is called in 
	 * element pre-order.  This used when the IdScheme is required to store the tag associated
	 * with each ID.
	 */
	public void beginNode(String tag);
	
	/**
	 * To be called when an element or attribute ends. The convention is that this is called in element
	 * post-order.
	 *
	 */
	public void endNode();
	
	/**
	 * To be called at the end of the document
	 *
	 */
	public void endDocument();
	
	/**
	 * (convention is: is called after endElement() and before the next beginElement();
	 * @return the ID of the last element for which endElement has been called
	 */
	public ElementID getLastID();
	
	/**
	 * Returns a string snippet containing the JDBC type(s) associated to the IDs produced
	 * by this scheme.
	 * 
	 * @param suffix
	 * @return snippet for "create table" statement
	 */
	public  String getSignature(String suffix);
	
	/**
	 * Returns a string snippet containing the name of the ID attribute produced by this
	 * scheme. This is used to declare an index via JDBC.
	 * @param suffix
	 * @return snippet for "create index" statement
	 */
	public String getIndexSignature(String suffix);
	
	/**
	 * 
	 * @return a string that will be used to enter null ID values in an RDB
	 */
	public  String nullIDStringImage();
}