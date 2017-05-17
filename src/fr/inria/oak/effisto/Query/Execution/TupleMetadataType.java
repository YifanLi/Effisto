package fr.inria.oak.effisto.Query.Execution;

/**
 * Enumerated type representing the possible types of metadata in a tuple.
 * 
 * @author Jesus CAMACHO RODRIGUEZ
 *
 * @created 16/12/2010
 */
public enum TupleMetadataType {
	ORDERED_ID,
	UNIQUE_ID,
	STRUCTURAL_ID,
	UPDATE_ID,
	NULL_ID,
    TUPLE_TYPE,
    STRING_TYPE,
    URI_TYPE,
    INTEGER_TYPE,
    NULL;
    
	/**
	 * Gets a String and returns the corresponding member of the enumerated type.
	 * @return the type that corresponds to the value passed to the method
	 */
	public static TupleMetadataType getTypeEnum(String stringType) {
		if (stringType.equals("ORDERED_ID"))
			return ORDERED_ID;
		else if (stringType.equals("UNIQUE_ID"))
			return UNIQUE_ID;
		else if (stringType.equals("STRUCTURAL_ID"))
			return STRUCTURAL_ID;
		else if (stringType.equals("UPDATE_ID"))
			return UPDATE_ID;
		else if (stringType.equals("NULL_ID"))
			return NULL_ID;
		else if (stringType.equals("TUPLE_TYPE"))
			return TUPLE_TYPE;
		else if (stringType.equals("STRING_TYPE"))
			return STRING_TYPE;
		else if (stringType.equals("URI_TYPE"))
			return URI_TYPE;
		else if (stringType.equals("INTEGER_TYPE"))
			return INTEGER_TYPE;
		else if (stringType.equals("null"))
			return NULL;
		else
			return null;
	}
	
	/**
	 * Returns the string representation of the current value of the enumerated type.
	 * @return string representation for the correspondent type
	 */
	public String toString() {
		switch(this) {
			case ORDERED_ID:
				return "ORDERED_ID";
			case UNIQUE_ID:
				return "UNIQUE_ID";
			case STRUCTURAL_ID:
				return "STRUCTURAL_ID";
			case UPDATE_ID:
				return "UPDATE_ID";
			case NULL_ID:
				return "NULL_ID";
			case TUPLE_TYPE:
				return "TUPLE_TYPE";
			case STRING_TYPE:
				return "STRING_TYPE";
			case URI_TYPE:
				return "URI_TYPE";
			case INTEGER_TYPE:
				return "INTEGER_TYPE";
			case NULL:
				return "null";
			default:
				return null;
		}
	}
}
