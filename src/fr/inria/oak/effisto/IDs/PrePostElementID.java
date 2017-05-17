package fr.inria.oak.effisto.IDs;
import fr.inria.oak.effisto.Parameters.*;
import fr.inria.oak.effisto.Query.Execution.*;
import fr.inria.oak.effisto.Exception.*;

/**
 * Pre-post element ID. 
 * 
 * @author Alin TILEA
 */
public class PrePostElementID implements ElementID{

	private static final long serialVersionUID = -8248867143045592758L;
	
	/* Constants */
	private static final String DELIMITER = Parameters.getProperty("delimiter");
	
	public int pre;
	public int post;
	
	public static PrePostElementID theNull = new PrePostElementID(-1);
	
	public PrePostElementID(int pre){	
		this.pre = pre;
	}
	
	public final void setPost(int post){
		this.post = post;
	}
		
	public int getType() {
		return 0;
	}

	public String toString(){
		if (this == theNull){
			return TupleMetadataType.NULL.toString();
		}
		
		return new String(pre + DELIMITER + post);
	}

	public boolean isParentOf(ElementID id2) throws VIP2PException {
			return false;
	}

	public boolean isAncestorOf(ElementID id2) throws VIP2PException {
		try{
			PrePostElementID other = (PrePostElementID)id2;
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

	public ElementID getParent() throws VIP2PException {
		throw new VIP2PException("PrePostElementID cannot infer parent");
	}
	
	public ElementID getNull() {
		return theNull;
	}
	
	public boolean isNull(){
		return (this == getNull());
	}
		
	public boolean equals(Object o){
		try{
			PrePostElementID id2 = (PrePostElementID)o;
			return ( this.pre == id2.pre && this.post == id2.post );
		}
		catch(ClassCastException cce){
			Parameters.logger.warn("ClassCastException: ", cce);
			return false;
		}
	}
	
	public int hashCode(){
		return ((new Integer(this.pre)).hashCode());
	}

	public boolean startsAfter(ElementID id2) throws VIP2PException {
		PrePostElementID other = null;
		try {	
			other = (PrePostElementID) id2;
		} catch (ClassCastException e) {
			throw new VIP2PException("Cannot apply startAfter on different ID schemes");
		}
		return (this.pre > other.pre);
	}

	public boolean endsAfter(ElementID id2) throws VIP2PException {
		PrePostElementID other = null;
		try {	
			other = (PrePostElementID) id2;
		} catch (ClassCastException e) {
			throw new VIP2PException("Cannot apply startAfter on different ID schemes");
		}
		return (this.post > other.post);
	}
		
}