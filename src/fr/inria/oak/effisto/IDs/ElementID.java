package fr.inria.oak.effisto.IDs;

import fr.inria.oak.effisto.Exception.VIP2PException;

/**
 * Interface that will be implemented by classes that represent different kind of IDs.
 * 
 * @author Ioana MANOLESCU
 *
 * @created 13/06/2005
 */
public interface ElementID {

	/**
	 * TODO: refine this into better type description (3 integers...)
	 * @return the type code, in a type system to be defined. But for the time being this needs 
	 * to be redone
	 */
	public int getType();
	
	/**
	 * @param id2
	 * @return true if id1 is a parent of id2
	 */
	public boolean isParentOf(ElementID id2) throws VIP2PException;
	
	/**
	 * @param id2
	 * @return true if id1 is an ancestor of id2
	 */
	public boolean isAncestorOf(ElementID id2) throws VIP2PException;
	
	/**
	 * @return the parent ID if it can be computed
	 */
	public ElementID getParent() throws VIP2PException;
	
	/**
	 * @param id2
	 * @return true if this element ID starts strictly after id2
	 */
	public boolean startsAfter(ElementID id2) throws VIP2PException;
	
	/**
	 * 
	 * @param id2
	 * @return true if this element ID ends strictly after id2
	 */
	public boolean endsAfter(ElementID id2) throws VIP2PException;
	
	/**
	 * Gets the element ID that represents the null ID.
	 * @return the null element for this kind of ID
	 */
	public ElementID getNull();
	
	/**
	 * Returns true if this element ID is null.
	 * @return if this element ID is null
	 */
	public boolean isNull();
}