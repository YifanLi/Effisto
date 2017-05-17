package fr.inria.oak.effisto.IDs;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.*;

/**
 * Factory class that creates and returns the ID scheme specified.
 * 
 * @author Ioana MANOLESCU
 */
public class IDSchemeAssignator{
	
	/**
	 * Returns the kind of ID scheme that the node passed as a parameter
	 * specifies.
	 * @param pn the pattern node that we will use for getting the ID scheme
	 * @return the ID scheme
	 */
	public static final IDScheme getIDScheme(QueryTreeNode pn){
		if (pn.storesID()){
			if (pn.isIdentityIDType()){
				return getIdentityScheme();
			}
			if (pn.isOrderIDType()){
				return getOrderPreservingScheme();
			}
			if (pn.isStructIDType()){
				return getStructuralScheme();
			}
			if (pn.isUpdateIDType()){
				return getUpdateScheme();
			}
			return null;
		}
		else{
			return null;
		}
	}
	
	/**
	 * 
	 * @return an IDScheme whose only commitment is to respect node identity
	 */
	public static IDScheme getIdentityScheme(){
		return new OrderedIntegerIDScheme();
	}
	
	/**
	 * 
	 * @return an IDScheme which furthermore promises to respect order
	 */
	public static IDScheme getOrderPreservingScheme(){
		return new OrderedIntegerIDScheme();
	}
	
	/**
	 * 
	 * @return an IDScheme which allows, by comparing two IDs, to know whether they are 
	 * 	in an ancestor-descendant (or parent-child) relationship or not
	 */
	public static IDScheme getStructuralScheme(){
		return IDSchemeFactory.getElementIDScheme();
	}
	
	/**
	 * 
	 * @return an IDScheme which tolerates updates
	 */
	public static IDScheme getUpdateScheme(){
		return new CompactDynamicDeweyScheme();
	}
	
}