package fr.inria.oak.effisto.IDs;

import java.io.Serializable;

import fr.inria.oak.effisto.Parameters.*;
import fr.inria.oak.effisto.Query.Execution.*;
import fr.inria.oak.effisto.Exception.*;


/**
 * Pre-post-depth element ID.
 * 
 * @author Ioana MANOLESCU
 *
 * @created 13/06/2005
 */
public class PrePostDepthElementID implements ElementID, Serializable {

	private static final long serialVersionUID = -7955485825469353904L;
	
	private static final String DELIMITER = Parameters.getProperty("delimiter");
	
	public int pre;
	public int post;
	public int depth;
	
	public static PrePostDepthElementID theNull = new PrePostDepthElementID(-1, -1);
	
	public PrePostDepthElementID(int pre, int depth){
		this.pre = pre;
		this.depth = depth;
	}
	
	public String toString(){
		//return new String("[" + pre + "," + post + "," + depth + "]");
		if (this == theNull){
			return TupleMetadataType.NULL.toString();
		}
		//Parameters.logger.debug(PrePostDepthElementIDScheme.delimiter);
		
		return new String(pre + DELIMITER + post + DELIMITER + depth);
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#isParentOf(fr.inria.gemo.uload.IDs.ElementID)
	 */
	public boolean isParentOf(ElementID id2) throws VIP2PException {
		try{
			PrePostDepthElementID other = (PrePostDepthElementID)id2;
			if (other.pre > this.pre){
				if (other.post < this.post){
					if (other.depth == this.depth + 1){
						return true;
					}
				}
			}
			return false;
		}
		catch(Exception e){
			Parameters.logger.error("Exception: ",e);
			throw new VIP2PException(e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#isAncestorOf(fr.inria.gemo.uload.IDs.ElementID)
	 */
	public boolean isAncestorOf(ElementID id2) throws VIP2PException {
		try{
			PrePostDepthElementID other = (PrePostDepthElementID)id2;
			if (other.pre > this.pre){
				if (other.post < this.post){
					return true;
				}	
			}	
			return false;
		}
		catch(Exception e){
			Parameters.logger.error("Exception: ",e);
			throw new VIP2PException(e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#getParent()
	 */
	public ElementID getParent() throws VIP2PException {
		throw new VIP2PException("PrePostDepthElementID cannot infer parent");
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
			PrePostDepthElementID id2 = (PrePostDepthElementID)o;
			return (this.pre == id2.pre && this.post == id2.post && this.depth == id2.depth);
		}
		catch(ClassCastException cce){
			Parameters.logger.warn("ClassCastException: ", cce);
			return false;
		}
	}
	
	public int hashCode(){
		return ((new Integer(this.pre)).hashCode());
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#startsAfter(fr.inria.gemo.uload.IDs.ElementID)
	 */
	public boolean startsAfter(ElementID id2) throws VIP2PException {
		PrePostDepthElementID other = null;
		try {	
			other = (PrePostDepthElementID) id2;
		} catch (ClassCastException e) {
			throw new VIP2PException("Cannot apply startAfter on different ID schemes");
		}
		return (this.pre > other.pre);
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.ElementID#endsAfter(fr.inria.gemo.uload.IDs.ElementID)
	 */
	public boolean endsAfter(ElementID id2) throws VIP2PException {
		PrePostDepthElementID other = null;
		try {	
			other = (PrePostDepthElementID) id2;
		} catch (ClassCastException e) {
			throw new VIP2PException("Cannot apply startAfter on different ID schemes");
		}
		return (this.post > other.post);
	}

	@Override
	public int getType() {
		return 0;
	}


	public void setPost(int post2) {
		this.post = post2;
	}
	
}