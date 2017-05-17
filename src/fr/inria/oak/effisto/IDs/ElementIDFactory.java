package fr.inria.oak.effisto.IDs;

import fr.inria.oak.effisto.Parameters.Parameters;

/**
 * Factory class that creates the appropriate kind of ID depending on the
 * properties defined for ViP2P.
 * 
 * @author Ioana MANOLESCU
 */
public class ElementIDFactory{
	
	private enum ElementIDEnum {
		PREPOSTDEPTH,
		PREPOST
	}
	private final static ElementIDEnum USE_ELEMENT_ID = Boolean.parseBoolean(Parameters.getProperty("childAxis"))?ElementIDEnum.PREPOSTDEPTH:ElementIDEnum.PREPOST;
	
	public static ElementID getElementID(int pre, int depth){
		switch(USE_ELEMENT_ID) {
			case PREPOSTDEPTH:
				return new PrePostDepthElementID(pre, depth);
			case PREPOST:
				return new PrePostElementID(pre);
			default:
				Parameters.logger.error("Not sure what kind of IDs I should use");
				return null;
		}
	}
}