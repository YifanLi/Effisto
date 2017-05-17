package fr.inria.oak.effisto.IDs;
import java.io.Serializable;

import fr.inria.oak.effisto.Parameters.*;
import fr.inria.oak.effisto.Exception.*;
import fr.inria.oak.effisto.Query.Execution.*;

/**
 * Ordered integer element ID.
 * 
 * @author Ioana MANOLESCU
 *
 * @created 13/06/2005
 */
public class OrderedIntegerElementID implements ElementID, Serializable {

	private static final long serialVersionUID = 5294160855145350727L;

	public static OrderedIntegerElementID theNull = new OrderedIntegerElementID(-1);
	
	/**
	 * The actual ID
	 */
	public int n;
	
	public OrderedIntegerElementID(int n){
		this.n = n;
	}
	
	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#getType()
	 */
	public int getType() {
		return 0;
	}


	public String toString(){
		if (this == theNull){ 
			return TupleMetadataType.NULL.toString();
		}
		return ("" + n);
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#isParent(fr.inria.gemo.uload.IDs.ElementID, fr.inria.gemo.uload.IDs.ElementID)
	 */
	public  boolean isParentOf(ElementID id2) throws VIP2PException {
		throw new VIP2PException("IntegerIDs cannot answer isParentOf");
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#isAncestor(fr.inria.gemo.uload.IDs.ElementID, fr.inria.gemo.uload.IDs.ElementID)
	 */
	public boolean isAncestorOf(ElementID id2) throws VIP2PException {
		throw new VIP2PException("IntegerIDs cannot answer isAncestorOf");
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#getParent(fr.inria.gemo.uload.IDs.ElementID)
	 */
	public ElementID getParent() throws VIP2PException {
		throw new VIP2PException("IntegerIDs cannot answer getParent");
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#getNull()
	 */
	public ElementID getNull() {
		return theNull;
	}
	
	public boolean isNull(){
		return (this == getNull());
	}
	
	public boolean equals(Object o){
		try{
			OrderedIntegerElementID iid = (OrderedIntegerElementID)o;
			return (this.n == iid.n);
		}
		catch(ClassCastException cce){
			Parameters.logger.warn("ClassCastException: ", cce);
			return false;
		}
	}
	
	public int hashCode(){
		return (new Integer(n)).hashCode();
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#startsAfter(fr.inria.gemo.uload.IDs.ElementID)
	 */
	public boolean startsAfter(ElementID id2) throws VIP2PException {
		OrderedIntegerElementID other = (OrderedIntegerElementID)id2;
		return (this.n > other.n);
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#endsAfter(fr.inria.gemo.uload.IDs.ElementID)
	 */
	public boolean endsAfter(ElementID id2) throws VIP2PException {
		throw new VIP2PException("EndsAfter undefined for integer IDs");
	}

}