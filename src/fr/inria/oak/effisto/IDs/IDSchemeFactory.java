package fr.inria.oak.effisto.IDs;
import fr.inria.oak.effisto.Parameters.*;

/**
 * Factory class that creates the appropriate kind of ID scheme depending on the
 * properties defined for ViP2P.
 * 
 * @author Ioana MANOLESCU
 */
class IDSchemeFactory{
	private enum ElementIDSchemeEnum {
		PREPOSTDEPTH,
		PREPOST
	}
	private final static ElementIDSchemeEnum USE_ELEMENT_ID_SCHEME = Boolean.parseBoolean(Parameters.getProperty("childAxis"))?ElementIDSchemeEnum.PREPOSTDEPTH:ElementIDSchemeEnum.PREPOST;
	
	static IDScheme getElementIDScheme(){
		switch(USE_ELEMENT_ID_SCHEME) {
			case PREPOSTDEPTH:
				return new PrePostDepthElementIDScheme();
			case PREPOST:
				return new PrePostElementIDScheme();
			default:
				Parameters.logger.error("Don't know which ID scheme to use");
				return null;
		}
	}
}