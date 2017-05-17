package fr.inria.oak.effisto.Query.Execution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.inria.oak.effisto.Parameters.*;
import fr.inria.oak.effisto.Exception.VIP2PException;
import fr.inria.oak.effisto.IDs.*;
import fr.inria.oak.effisto.Query.Execution.*;
import fr.inria.oak.effisto.Node.*;

/**
 * Class that represents each of the tuples that will be used by the
 * ViP2P execution engine.
 * 
 * @author Ioana MANOLESCU
 * @author Spyros ZOUPANOS
 */
public class NTuple implements Comparable<NTuple>, Serializable {

	private static final long serialVersionUID = 4002470539187558978L;

	/* Constant */
	/**
	 * Warning: here, this constant is set true in Effisto!
	 */
	//private static final boolean USE_DEPTH = Parameters.getProperty("childAxis").compareTo("true") == 0;
	private static final boolean USE_DEPTH = true;
	/*
	 * This attribute has to be static in order to build the String
	 * representation of the NTuple correctly when there exist 
	 * nested tuples. This String representation is created
	 * calling the method createStringBuffer().
	 */
	private StringBuffer stringRepresentation;

	
	
	/**
	 * These have to be interpreted according to the metadata, which specifies
	 * which kind of IDs we have.
	 */
	public ElementID[] idFields;
	
	/**
	 * Integer pointer to the first ID position available
	 * at idFields.
	 */
	public int iID;

	/**
	 * Integer pointer to the first Nested position available
	 * at nestedFields.
	 */
	public int iNested;

	/**
	 * Tuples for which isNull == true are useful as markers of empty lists.
	 * isNull DOES NOT mean that the fields of the tuples are set to real
	 * nulls.
	 */
	public boolean isNull;
	
	/**
	 * The strings fields in the tuple.
	 */
	public char[][] stringFields;
	
	/**
	 * Integer pointer to the first String position available
	 * at stringFields.
	 */
	public int iString;

	/**
	 * The URI fields in the tuple.
	 */
	public char[][] uriFields;
	
	/**
	 * Integer pointer to the first URI position available
	 * at uriFields.
	 */
	public int iUri;
	
	/**
	 * The integer fields in the tuple.
	 */
	public int[] integerFields;
	
	/**
	 * Integer pointer to the first Integer position available
	 * at integerFields.
	 */
	public int iInteger;
	
	/**
	 * the json-doc's ID, which output the tuple
	 */
	public String idOfJsonObj;

	/**
	 * At every position, a ArrayList containing all tuples nested there
	 */
	public ArrayList<NTuple>[] nestedFields;

	public NRSMD nrsmd;
	
	

	public NTuple(
			int colNo,
			TupleMetadataType[] types,
			NRSMD[] childrenNRSMD,
			char[][] stringFields,
			char[][] uriFields,
			ElementID[] idFields,
			int[] integerFields,
			ArrayList<NTuple>[] nestedFields)
	throws VIP2PException {
		this.nrsmd = new NRSMD(colNo, types);
		this.nrsmd.nestedChildren = childrenNRSMD;
		this.stringFields = stringFields;
		this.uriFields = uriFields;
		this.idFields = idFields;
		this.integerFields = integerFields;
		this.nestedFields = nestedFields;

		if(this.stringFields != null)
		{
			this.iString = 0;
			for(int i=0; i<this.nrsmd.stringNo && this.stringFields[i]!=null; i++)
				this.iString++;
		}

		if(this.uriFields != null)
		{
			this.iUri = 0;
			for(int i=0; i<this.nrsmd.uriNo && this.uriFields[i]!=null; i++)
				this.iUri++;
		}

		if(this.idFields != null)
		{
			this.iID = 0;
			for(int i=0; i<this.nrsmd.idsNo && this.idFields[i]!=null; i++)
				this.iID++;
		}
		
		if(this.integerFields != null)
		{
			this.iInteger = 0;
			for(int i=0; i<this.nrsmd.integerNo; i++)
				this.iInteger++;
		}

		if(this.nestedFields != null)
		{
			this.iNested = 0;
			for(int i=0; i<this.nrsmd.nestedNo && this.nestedFields[i]!=null; i++)
				this.iNested++;
		}
	}
	
	
	
	/**
	 * Produces a tuple by allocating the place as required by the metadata
	 * 
	 * @param rsmd
	 *            The tuple's metadata
	 */
	public NTuple(NRSMD rsmd) {
		
		this.nrsmd = rsmd;
		this.idFields =	new ElementID[nrsmd.idsNo + nrsmd.prePostIDsNo + nrsmd.cddIDsNo];
		//Parameters.logger.debug("Created tuple from metadata with " + this.idFields.length + " IDs");
		this.stringFields = new char[nrsmd.stringNo][];
		this.uriFields = new char[nrsmd.uriNo][];
		this.integerFields = new int[nrsmd.integerNo];
		this.nestedFields = new ArrayList[nrsmd.nestedNo];
		//Parameters.logger.debug("Created NTuple with " + nrsmd.nestedNo + " nested children");
		this.iID = 0;
		this.iString = 0;
		this.iUri = 0;
		this.iInteger = 0;
		this.iNested = 0;
	}

	public NTuple(
			NRSMD nrsmd,
			char[][] stringFields,
			char[][] uriFields,
			ElementID[] idFields,
			int[] integerFields,
			ArrayList<NTuple>[] nestedFields)
	throws VIP2PException {
		this.nrsmd = nrsmd;
		this.stringFields = stringFields;
		this.uriFields = uriFields;
		this.idFields = idFields;
		this.integerFields = integerFields;
		this.nestedFields = nestedFields;

		if(this.stringFields != null)
		{
			this.iString = 0;
			for(int i=0; i<this.nrsmd.stringNo && this.stringFields[i]!=null; i++)
				this.iString++;
		}

		if(this.uriFields != null)
		{
			this.iUri = 0;
			for(int i=0; i<this.nrsmd.uriNo && this.uriFields[i]!=null; i++)
				this.iUri++;
		}

		if(this.idFields != null)
		{
			this.iID = 0;
			for(int i=0; i<this.nrsmd.idsNo && this.idFields[i]!=null; i++)
				this.iID++;
		}
		
		if(this.integerFields != null)
		{
			this.iInteger = 0;
			for(int i=0; i<this.nrsmd.integerNo; i++)
				this.iInteger++;
		}

		if(this.nestedFields != null)
		{
			this.iNested = 0;
			for(int i=0; i<this.nrsmd.nestedNo && this.nestedFields[i]!=null; i++)
				this.iNested++;
		}
	}

	/**
	 * @param nrsmd:
	 *            nested tuple metadata, constructed from the XAM
	 * @param rs:
	 *            SQL result set, thus, in fact, no nesting is allowed here
	 * @throws VIP2PException
	 */
	public NTuple(NRSMD nrsmd, ResultSet rs) throws VIP2PException {
		//Parameters.logger.debug("Constructing tuple ! Width = " + nrsmd.colNo);
		this.nrsmd = nrsmd;

		int iString = 0;
		int iID = 0;
		String aux = "";

		this.idFields =
			new ElementID[nrsmd.idsNo
			              + nrsmd.prePostIDsNo
			              + nrsmd.cddIDsNo];
		this.stringFields = new char[nrsmd.stringNo][];
		this.uriFields = new char[nrsmd.uriNo][];
		this.integerFields = new int[nrsmd.integerNo];
		this.nestedFields = new ArrayList[nrsmd.nestedNo];
		
		int iSQLColumn = 1;

		try {
			for (int i = 0; i < nrsmd.colNo; i++) {
				switch (nrsmd.types[i]) {
				case STRING_TYPE :
					//Parameters.logger.debug("STRING TYPE AT "+ i);
					String s2 = rs.getString(iSQLColumn);
					if (s2 != null){
						stringFields[iString] = s2.toCharArray();
					}
					else{
						stringFields[iString] = null;
					}
					iString++;
					iSQLColumn++;
					//Parameters.logger.debug("After STRING type, SQL column is " + iSQLColumn);
					break;
				case UPDATE_ID :
					// CompactDynamicDewey
					//Parameters.logger.debug("CompactDynamicDewey (STRING) TYPE AT " + i);
					aux = rs.getString(iSQLColumn);
					if (aux.compareTo(CompactDynamicDeweyID.theNull.getPathAsString()) == 0){
						idFields[iID] = CompactDynamicDeweyID.theNull;
					}
					else{
						aux.replaceAll("'", "");
						String[] idAndTag = aux.split(" ");
						int[] tempPath = new int[idAndTag[0].length()];
						for(int j = 0; j<idAndTag[0].length(); j++) {
							if(Character.isDigit(idAndTag[0].charAt(j))){
								tempPath[j] = idAndTag[0].charAt(j);
							}
						}
						int[] tempTag = new int[idAndTag[1].length()];
						for(int j = 0; j<idAndTag[1].length(); j++) {
							if(Character.isDigit(idAndTag[1].charAt(j))){
								tempTag[j] = idAndTag[1].charAt(j);
							}
						}
						idFields[iID] = new CompactDynamicDeweyID(tempPath, tempTag);
					}
					iID++;
					iSQLColumn++;
					//Parameters.logger.debug("After Compact Dynamic Dewey ID, SQL column is " + iSQLColumn);
					break;
				case ORDERED_ID :
				case UNIQUE_ID :
					// integer ID
					//Parameters.logger.debug("NUMERIC ID (INTEGER) AT " + i);
					int n = rs.getInt(iSQLColumn);
					if (n == OrderedIntegerElementID.theNull.n){
						idFields[iID] = OrderedIntegerElementID.theNull;
					}
					else{
						idFields[iID] = new OrderedIntegerElementID(n);
					}
					iID++;
					iSQLColumn++;
					//Parameters.logger.debug("After integer ID, SQL column is " + iSQLColumn);
					break;
				case STRUCTURAL_ID :
					assert (!NTuple.USE_DEPTH) : "No code for this yet";
					//Parameters.logger.debug("THREE NUMBERS (STRUCTURAL ID) AT " + i);
					PrePostElementID ppd = (PrePostElementID) ElementIDFactory.getElementID(rs.getInt(iSQLColumn), rs.getInt(iSQLColumn + 2));
					ppd.setPost(rs.getInt(iSQLColumn + 1));
					if (ppd.pre == PrePostElementID.theNull.pre){
						ppd = PrePostElementID.theNull;
					}					
					idFields[iID] = ppd;
					iSQLColumn += 3;
					iID++;
					//Parameters.logger.debug("After 3-valued ID, SQL column is " + iSQLColumn);
					break;
				case TUPLE_TYPE :
					throw new VIP2PException("Nested tuples cannot be constructed out of SQL flat result sets");
				default :
					throw new VIP2PException(
							"Unknown type in metadata: " + nrsmd.types[i]);

				}
			}
		} catch (SQLException se) {
			Parameters.logger.error("Exception: ",se);
			throw new VIP2PException(
					"Could not get new tuple from result set " + se.toString());
		}
	}

	/**
	 * This method is useful to gradually fill in a tuple.
	 * 
	 * @param id
	 */
	public void addID(ElementID id) {
		//Parameters.logger.debug("\nAdding ID at " + iID + " out of " + idFields.length);
		//Parameters.logger.debug("RSMD: " + nrsmd.integerIDsNo + " integer IDs and " + nrsmd.prePostIDsNo + " pre-post IDs and " + nrsmd.cddIDsNo + " compact dynamic dewey IDs");
		this.idFields[iID] = id;
		iID++;
	}
	
	public void addInteger(int i) {
		this.integerFields[iInteger] = i;
		iInteger++;
	}

	/**
	 * This method is useful to gradually fill in a tuple.
	 * 
	 * @param v
	 */
	public void addNestedField(ArrayList<NTuple> v) {
		this.nestedFields[iNested] = v;
		iNested++;
	}

	/**
	 * This method is useful to gradually fill in a tuple.
	 * 
	 * @param s
	 */
	public void addString(char[] s) {
		this.stringFields[iString] = s;
		iString++;
	}

	/**
	 * This method is useful to gradually fill in a tuple.
	 * 
	 * @param tag
	 */
	public void addString(String tag) {
		//Parameters.logger.debug("iString: " + iString);
		//Parameters.logger.debug("this.stringFields.length: " + this.stringFields.length);
		this.stringFields[iString] = tag.toCharArray();
		iString++;
	}
	
	/**
	 * add content to the same tuple.
	 * 
	 * @param tag
	 */
	public void addStringToSame(String tag) {
		if (this.stringFields[iString] == null){
			this.stringFields[iString] = tag.toCharArray();
		}else{
			String newSF = new String(this.stringFields[iString]) + tag;
			this.stringFields[iString] = newSF.toCharArray();
		}
	}
	
	/**
	 * This method is useful to gradually fill in a tuple.
	 * 
	 * @param uri
	 */
	public void addUri(char[] uri) {
		this.uriFields[iUri] = uri;
		iUri++;
	}

	/**
	 * This method is useful to gradually fill in a tuple.
	 * 
	 * @param uri
	 */
	public void addUri(String uri) {
		this.uriFields[iUri] = uri.toCharArray();
		iUri++;
	}


	/**
	 * This is an ad-hoc recursive cartesian product to compute simple tuples.
	 * 
	 * @param v0
	 *            The "root tuple" vector, containing only fields which are
	 *            fine so far.
	 * @param args
	 *            ArrayLists of nested tuples (former children). These are already
	 *            fully unnested. The goal is to cart-product with the root
	 *            tuples.
	 * @param n
	 *            Minimum position from which to exploit the tuples of nested
	 *            children.
	 * @param v
	 *            The total vector.
	 * @throws VIP2PException
	 */
	private void cartProd(ArrayList<NTuple> v0, ArrayList<NTuple>[] args, int n, ArrayList<NTuple> v)
	throws VIP2PException {
		if (n == args.length) {
			// nothing more to multiply, just copy root tuples into v and
			// return
			Iterator<NTuple> i0 = v0.iterator();
			while (i0.hasNext()) {
				v.add(i0.next());
			}
			return;
		}

		// build into aux the cart prod of v0 and the first child tuple vector.
		ArrayList<NTuple> aux = new ArrayList<NTuple>();

		Iterator<NTuple> i0 = v0.iterator();
		while (i0.hasNext()) {
			NTuple t0 = (NTuple) i0.next();
			Iterator<NTuple> i1 = args[n].iterator();
			while (i1.hasNext()) {
				NTuple t1 = (NTuple) i1.next();
				// this child tuple should no longer need to be unnested; this
				// has been
				// taken care of previously
				// just append this to the root tuple
				NRSMD newRSMD = NRSMD.appendNRSMD(t0.nrsmd, t1.nrsmd);
				aux.add(NTuple.append(newRSMD, t0, t1));
			}
		}
		cartProd(aux, args, n + 1, v);
	}

	public final void clean() throws VIP2PException {
		//Parameters.logger.debug("Cleaning ");
		//display();
		for (int i = 0; i < this.nestedFields.length; i++) {
			//Parameters.logger.debug("At " + i);
			ArrayList<NTuple> thisChild = this.nestedFields[i];
			boolean doneI = false;

			if (thisChild.size() == 1) {
				if (((NTuple) thisChild.get(0)).isNull) {
					thisChild.remove(0);
					doneI = true;
					//Parameters.logger.debug("Removed a null");
				}
			}
			if (!doneI) {
				//Parameters.logger.debug("Work to do still");
				Iterator<NTuple> it = thisChild.iterator();
				while (it.hasNext()) {
					NTuple tChild = (NTuple) it.next();
					tChild.clean();
				}
			}
		}
	}
	
	
			
	/**
	 * The goal is to compare them in document order dictated by the dominant
	 * ID. We conjecture that the dominant ID is the first one encountered...
	 * 
	 * @param t2
	 * @return
	 */
	public int compareTo(NTuple t2) {
		assert (this.nrsmd.equals(t2.nrsmd)) : "Comparing tuples with different metadata";
		//this.nrsmd.display();
		TupleMetadataType[] types = nrsmd.types;
		boolean over = false;
		int res = 0;

		for (int i = 0; i < types.length && !over; i++) {
			switch (types[i]) {
			case UNIQUE_ID :
			case ORDERED_ID :
			case STRUCTURAL_ID :
			case UPDATE_ID :
				ElementID thisID = this.getIDField(i);
				ElementID otherID = t2.getIDField(i);
			/*	Parameters.logger.debug("Comparing: ");
				try{
				this.display();
				t2.display();
				}catch(VIP2PException e){}*/
				//Parameters.logger.debug("At " + i + " types[i] is " + Constants.decodeConstant(types[i]));
				if (thisID == null){
					if (otherID != null){
						// the other one is not null, so this one is smaller
						//Parameters.logger.debug("At " + i + " firstID null, the other one non-null");
						res = -1;
						over = true;
					}
				}
				else{
					if (otherID == null){
						//Parameters.logger.debug("At " + i + " fistID non null, the other one null");
						// the other one is null, so the other one is smaller
						res = 1;
						over = true;
					}
					else{
						if  (thisID.isNull()){
							if (!otherID.isNull()){
								//Parameters.logger.debug("At " + i + " firstID is null, the other one is not");
								res = -1;
								over = true;
							}
						}
						else{// thisID is not null
							if (otherID.isNull()){
								//Parameters.logger.debug("At " + i + " firstID is not null, while the other is");
								res = 1;
								over = true;
							}
							else{
								try{
									if (thisID.startsAfter(otherID)) {
										//Parameters.logger.debug("At " + i + " firstID starts later");
										res = 1;
										over = true;
									} else if (otherID.startsAfter(thisID)) {
										//Parameters.logger.debug("At " + i + " otherID starts later");
										res = -1;
										over = true;
									}
								} catch (Exception e) {
									Parameters.logger.error("Exception: ",e);
								}
							}
						}
					}
				}
				break;
			case STRING_TYPE:
				char[] c1 = this.getStringField(i);
				char[] c2 = t2.getStringField(i);
				for (int j = 0; j < c1.length && j < c2.length && !over; j ++){
					if (c1[j] < c2[j]){
						res = -1;
						over = true;
					}
					else{
						if (c1[j] > c2[j]){
							res = 1;
							over = true;
						}
					}
				}
				if (!over){
					if (c1.length > c2.length){
						res = 1;
						over = true;
					}
					else{
						if (c2.length > c1.length){
							res = -1;
							over = true;
						}
					}
				}
				//Parameters.logger.debug("Comparing " + c1 + " " + c2 + " obtained " + res);

				break;
			case URI_TYPE:
				char[] uri1 = this.getUriField(i);
				char[] uri2 = t2.getUriField(i);
				for (int j = 0; j < uri1.length && j < uri2.length && !over; j ++){
					if (uri1[j] < uri2[j]){
						res = -1;
						over = true;
					}
					else{
						if (uri1[j] > uri2[j]){
							res = 1;
							over = true;
						}
					}
				}
				if (!over){
					if (uri1.length > uri2.length){
						res = 1;
						over = true;
					}
					else{
						if (uri2.length > uri1.length){
							res = -1;
							over = true;
						}
					}
				}
				break;
			case TUPLE_TYPE:
				// nothing

			default :
				// nothing
			}
		}		
		return res;
	}

	private final void createStringBuffer() throws VIP2PException {
		////Parameters.logger.debug("\nTUPLE DISPLAY " + this.hashCode()  + " " + this.nestedFields.length + 
		//" NESTED FIELDS");
		//nrsmd.display();

		stringRepresentation.append('(');
		int iString = 0;
		int iUri = 0;
		int iID = 0;
		int iInteger = 0;
		int klNested = 0;

		if (this.uriFields.length
				+ this.integerFields.length
				+ this.stringFields.length
				+ this.idFields.length
				+ this.nestedFields.length
				> 0) {
			for (int i = 0; i < nrsmd.colNo; i++) {
				//Parameters.logger.debug("Buffer is: " + forDisplay);
				//Parameters.logger.debug("At column: " + i + " out of " + nrsmd.colNo +"  type: " + Constants.decodeConstant(nrsmd.types[i]));
				switch (nrsmd.types[i]) {
				case STRING_TYPE :
					//Parameters.logger.debug("String field at " + iString + " out of " + stringFields.length);
					//if (this.stringFields[iString] == null) {
					if (this.stringFields[iString].length == 0) {
						stringRepresentation.append(TupleMetadataType.NULL.toString());
					} else {
                                                int index = 0;
                                                for(char c: this.stringFields[iString]){
                                                    if(c=='\n'){
                                                        this.stringFields[iString][index]=' ';
                                                    }
                                                    index++;
                                                }
                                                stringRepresentation.append("\"");
                                                stringRepresentation.append(this.stringFields[iString]);
                                                stringRepresentation.append("\"");
					}
					//Parameters.logger.debug("Now buffer is: " + forDisplay);
					iString++;
					break;
				case URI_TYPE :
					if (this.uriFields[iUri] == null) {
						stringRepresentation.append(TupleMetadataType.NULL);
					} else {
						stringRepresentation.append("\"");
                                                stringRepresentation.append(this.uriFields[iUri]);
                                                stringRepresentation.append("\"");
					}
					iUri++;
					break;
				case INTEGER_TYPE :
                    stringRepresentation.append(this.integerFields[iInteger]);
					iInteger++;
					break;
				case UPDATE_ID :
					stringRepresentation.append(
							(this.idFields[iID] == null)
							? TupleMetadataType.NULL.toString()
									: idFields[iID]);
					iID++;
					break;
				case ORDERED_ID :
					stringRepresentation.append(
							(this.idFields[iID] == null)
							? TupleMetadataType.NULL.toString()
									: idFields[iID]);
					iID++;
					break;
				case UNIQUE_ID :
					stringRepresentation.append(
							(this.idFields[iID] == null)
							? TupleMetadataType.NULL.toString()
									: idFields[iID]);
					iID++;
					break;
				case STRUCTURAL_ID :
					//Parameters.logger.info("AT " + iID + " STRUCTURAL ID OF CLASS " + this.idFields[iID].getClass().getName());
					stringRepresentation.append(
							(this.idFields[iID] == null)
							? TupleMetadataType.NULL.toString()
									: idFields[iID]);
					iID++;
					break;
				case TUPLE_TYPE :
					//Parameters.logger.debug("Looking at attribute " + i + " of nested type");
					//this.nrsmd.display();
					stringRepresentation.append("[");
					//Parameters.logger.debug("At nested index " + iNested);
					if (this.nestedFields[klNested] == null) {
						//Parameters.logger.debug("There are " + this.nestedFields.length + " nested fields and at " + iNested + " there is: ");
						stringRepresentation.append(TupleMetadataType.NULL.toString());
					} else {
						if (this.nestedFields != null) {
							Iterator<NTuple> it =
								this.nestedFields[klNested].iterator();
							int kIndexNested = 0;
							while (it.hasNext()) {
								NTuple ntc = it.next();
								//Parameters.logger.debug("MOVING TO  " + kIndexNested + "TH TUPLE INTO " + klNested + "TH  CHILD OF " + this.hashCode());
								ntc.createStringBuffer();
								//Parameters.logger.debug("COMING BACK FROM NESTED CHILD INTO " + this.hashCode() );
								kIndexNested ++;
							}
						}
					}
					stringRepresentation.append("]");
					klNested++;
					break;
				default :
					throw new VIP2PException("Unknown type");
				}
				if (i < nrsmd.colNo - 1) {
					stringRepresentation.append("\b");
				}
			}
		}
		stringRepresentation.append(')');
	}

	/**
	 * @return
	 */
	public NTuple deepCopy() {
		NTuple t2 = new NTuple(this.nrsmd);
		for (int i = 0; i < this.idFields.length; i ++){
			t2.addID(this.idFields[i]);
		}
		for (int i = 0; i < this.stringFields.length; i ++){
			t2.addString(this.stringFields[i]);
		}
		for (int i = 0; i < this.uriFields.length; i ++){
			t2.addUri(this.uriFields[i]);
		}
		for (int i = 0; i < this.integerFields.length; i ++){
			t2.addInteger(this.integerFields[i]);
		}
		for (int i = 0; i < this.nestedFields.length; i ++){
			ArrayList<NTuple> v = new ArrayList<NTuple>();
			ArrayList<NTuple> v1 = this.nestedFields[i];
			Iterator<NTuple> i1 = v1.iterator();
			while (i1.hasNext()){
				v.add(i1.next().deepCopy());				
			}
			t2.addNestedField(v);
		}
		return t2;
	}

	public final void display() throws VIP2PException {
		stringRepresentation = new StringBuffer();
		//this.nrsmd.display();
		createStringBuffer();
		Parameters.logger.info(new String(stringRepresentation).replace("\b", ", "));
	}

	public final void display(String caller) throws VIP2PException {
		stringRepresentation = new StringBuffer();
		//this.nrsmd.display();
		createStringBuffer();
		Parameters.logger.info(caller + new String(stringRepresentation).replace("\b", ", "));
	}

	/**
	 * @param o
	 *            Object with which to compare
	 * @return True if the tuples have the same useful contents, false
	 *         otherwise
	 */
	public boolean equals(Object o) {
		try {
			NTuple nt2 = (NTuple) o;		

			if (!this.nrsmd.equals(nt2.nrsmd)) {
				return false;
			}

			if (this.isNull){
				if (!nt2.isNull){
					return false;
				}
				else{
					return true;
				}
			}
			else{
				if (nt2.isNull){
					return false;
				}
			}


			for (int i = 0; i < this.idFields.length; i++) {
				if (this.idFields[i] == null){
					if (nt2.idFields[i]  != null){
						return false;
					}
				}
				else{
					if (nt2.idFields[i] == null){
						return false;
					}
					else{
						if (!this.idFields[i].equals(nt2.idFields[i])) {
							//Parameters.logger.debug("Unequal 3 " + i + this.idFields[i].getClass().getName());
							return false;
						}
					}
				}
			}
			for (int i = 0; i < this.stringFields.length; i++) {
				if (this.stringFields[i].length
						!= nt2.stringFields[i].length) {
					//Parameters.logger.debug("Unequal 4.1 " + i);
					return false;
				}
				for (int j = 0; j < stringFields[i].length; j++) {
					if (stringFields[i][j] != nt2.stringFields[i][j]) {
						//Parameters.logger.debug("Unequal 4.2 " + i + " " + j );
						return false;
					}
				}
			}
			for (int i = 0; i < this.uriFields.length; i++) {
				if (this.uriFields[i].length
						!= nt2.uriFields[i].length) {
					//Parameters.logger.debug("Unequal 4.1 " + i);
					return false;
				}
				for (int j = 0; j < uriFields[i].length; j++) {
					if (uriFields[i][j] != nt2.uriFields[i][j]) {
						//Parameters.logger.debug("Unequal 4.2 " + i + " " + j );
						return false;
					}
				}
			}
			for (int i = 0; i < this.nestedFields.length; i++) {
				ArrayList<NTuple> v1 = this.nestedFields[i];
				ArrayList<NTuple> v2 = this.nestedFields[i];
				if (v1.size() != v2.size()) {
					//Parameters.logger.debug("Unequal 5 " + i);
					return false;
				}
				Iterator<NTuple> it1 = v1.iterator();
				Iterator<NTuple> it2 = v2.iterator();
				while (it1.hasNext()) {
					NTuple nt1 = it1.next();
					NTuple nt3 = it2.next();
					if (!nt1.equals(nt3)) {
						//Parameters.logger.debug("Unequal 6 " + i);
						return false;
					}
				}
			}
			return true;
		} catch (Exception cce) {
			Parameters.logger.error("Exception: ",cce);
			return false;
		}
	}

	public NTuple flatProjection() throws VIP2PException {
		NRSMD flatNRSMD = this.nrsmd.flatProjection();
		return new NTuple(flatNRSMD, stringFields, uriFields, idFields, integerFields, new ArrayList[0]);
	}

	/**
	 * @param k
	 *            the number of the column, counted in absolute terms (over all
	 *            columns)
	 * @return the element ID at the K column, if one exists there.
	 */
	public ElementID getIDField(int k) {
		int iID = 0;
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.UPDATE_ID
					|| nrsmd.types[i] == TupleMetadataType.UNIQUE_ID
					|| nrsmd.types[i] == TupleMetadataType.ORDERED_ID
					|| nrsmd.types[i] == TupleMetadataType.STRUCTURAL_ID) {
				//Parameters.logger.debug(iID + "-th ID is at column " + i);
				if (i == k) {
					return idFields[iID];
				}
				iID++;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param k - the number of the column, counted in absolute terms (over all
	 *            columns) which corresponds to ID field
	 *            
	 * @return a string concatenation of the Uri with the ID at the column K if they exist 
	 */
	public String getFullIDField(int k){
		ElementID eid = getIDField(k);
		System.err.println("TRYING TO RETRIEVE FULLID");
		int iUri = -1;
		for (int i = 0; i<=k && i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.URI_TYPE) {
				iUri++;
			}
		}
		//Either the id or the uri does not exist
		if(eid == null || iUri == -1){
			return null;
		}else{
			String ret = uriFields[iUri].toString()+eid.toString();
			return ret;
		}
		
	}
	
	/**
	 * @param is
	 * @return
	 */
	public ArrayList<ElementID> getIDFields(int[] is) {
		ArrayList<ElementID> v = new ArrayList<ElementID>();
		recGetIDFields(is, v, 0);
		return v;
	}
	
	public int getIntegerField(int k) {
		int iInteger = 0;
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.INTEGER_TYPE) {
				//Parameters.logger.debug(iID + "-th ID is at column " + i);
				if (i == k) {
					return integerFields[iInteger];
				}
				iInteger++;
			}
		}
		return 0;
	}

	
	/**
	 * Returns the nested field at the K column, if one exists there.
	 * 
	 * @param k
	 * @return
	 */
	public ArrayList<NTuple> getNestedField(int k) {
		int iNested = 0;
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.TUPLE_TYPE) {
				//Parameters.logger.debug(iID + "-th ID is at column " + i);
				if (i == k) {
					return nestedFields[iNested];
				}
				iNested++;
			}
		}
		return null;
	}
	
	/**
	 * @param k
	 *            the number of the column from which we want the String
	 * @return the String at the K column, if one exists there.
	 */
	public char[] getStringField(int k) {
		int iString = 0;
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.STRING_TYPE) {
				//Parameters.logger.debug(iID + "-th ID is at column " + i);
				if (i == k) {
					return stringFields[iString];
				}
				iString++;
			}
		}
		return new char[0];
	}

	/**
	 * @param is
	 * @return
	 */
	public ArrayList<String> getStringFields(int[] is) {
		ArrayList<String> v = new ArrayList<String>();
		recGetStringFields(is, v, 0);
		return v;
	}
	
	/**
	 * This returns a vector of ArrayLists
	 * @param is
	 * @return
	 */
	public ArrayList<ArrayList<NTuple>> getTupleFields(int[] is){
		ArrayList<ArrayList<NTuple>> v = new ArrayList<ArrayList<NTuple>>();
		recGetTupleFields(is, v, 0);
		return v;
	}

	/**
	 * @param k
	 *            the number of the column from which we want the Uri
	 * @return the Uri at the K column, if one exists there.
	 */
	public char[] getUriField(int k) {
		int iUri = 0;
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.URI_TYPE) {
				if (i == k) {
					return uriFields[iUri];
				}
				iUri++;
			}
		}
		return new char[0];
	}

	/**
	 * @param is
	 * @return
	 */
	public ArrayList<String> getUriFields(int[] is) {
		ArrayList<String> v = new ArrayList<String>();
		recGetUriFields(is, v, 0);
		return v;
	}

	/**
	 * This method is important ! It must be preserved to have the desired behavior
	 * when inserting tuples into Map-like structures (which rely on the hashCode).
	 * If this method disappears, the default hashCode method is too restrictive 
	 * (based on pointer equality).
	 * We need this one to have value-based tuple equality.
	 */
	public int hashCode() {
		stringRepresentation = new StringBuffer();
		try {
			createStringBuffer();
		} catch (Exception e) {
			Parameters.logger.error("Exception: ",e);
		}
		return (new String(stringRepresentation)).hashCode();
	}
	
	private void incIString(){
		iString++;
	}

	public void setFirstIntegerField(int k) {
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.INTEGER_TYPE) {
				integerFields[0] = k;
				break;
			}
		}
	}
	
	private void recGetIDFields(int[] is, ArrayList<ElementID> v, int from) {
		if (from == is.length - 1) {
			v.add(this.getIDField(is[is.length - 1]));
		} else {
			Iterator<NTuple> it = this.getNestedField(is[from]).iterator();
			while (it.hasNext()) {
				it.next().recGetIDFields(is, v, from + 1);
			}
		}
	}

	private void recGetStringFields(int[] is, ArrayList<String> v, int from) {
		if (from == is.length - 1) {
			v.add(new String(this.getStringField(is[is.length - 1])));
		} else {
			Iterator<NTuple> it = this.getNestedField(is[from]).iterator();
			while (it.hasNext()) {
				it.next().recGetStringFields(is, v, from + 1);
			}
		}
	}
	private void recGetTupleFields(int[] is, ArrayList<ArrayList<NTuple>> v, int from){
		if (from == is.length - 1){
			ArrayList<NTuple> aux = getNestedField(is[from]);
			v.add(aux);
		}
		else{
			Iterator<NTuple> it = this.getNestedField(is[from]).iterator();
			while (it.hasNext()){
				((NTuple)it.next()).recGetTupleFields(is, v, from + 1);
			}
		}
	}

	private void recGetUriFields(int[] is, ArrayList<String> v, int from) {
		if (from == is.length - 1) {
			v.add(new String(this.getUriField(is[is.length - 1])));
		} else {
			Iterator<NTuple> it = this.getNestedField(is[from]).iterator();
			while (it.hasNext()) {
				it.next().recGetUriFields(is, v, from + 1);
			}
		}
	}
	/**
	 * from tracks the advancement in aux
	 * @param aux
	 * @param replacementOfInnerNestedTuples
	 * @param tupleIndex
	 * @param from
	 */
	private void recSet(int[] path, ArrayList<NTuple> v, int[] tupleIndex, int fromPath, int fromIndex){
		Parameters.logger.info(NodeInformation.localPID + ": Set on " + print(path) + " from " + fromPath + 
				", index " + print(tupleIndex) + " from index " + fromIndex);
		if (fromPath == path.length - 2){
			ArrayList<NTuple> parentOfNestedList = this.getNestedField(path[fromPath]);
			if (parentOfNestedList.size() >= tupleIndex[fromIndex]){
				NTuple tupleInNestedList = (NTuple)parentOfNestedList.get(tupleIndex[fromIndex]);
				tupleInNestedList.setNestedField(path[path.length - 1], v);
			}
			else{
				// do nothing
			}
			return;
		}
		else{
			ArrayList<NTuple> parentOfNestedList = this.getNestedField(path[fromPath]);
			Iterator<NTuple> it = parentOfNestedList.iterator();
			int iTuple = 0;
			while (it.hasNext()){
				NTuple nt = (NTuple)it.next();
				if (iTuple == tupleIndex[fromIndex]){
					nt.recSet(path, v, tupleIndex, (fromPath + 1), (fromIndex + 1));
				}
				iTuple ++;
			}
		}
	}

	private void recSetNestedFields(int[] path, ArrayList<NTuple> v, int from){
		assert (from == path.length - 1) : "Don't know how to do this yet";
		this.setNestedField(path[from], v);
	}

	public void replace(ArrayList<NTuple> v1, ArrayList<NTuple> v2){
		for (int i = 0; i < this.nestedFields.length; i ++){
			ArrayList<NTuple> v3 = this.nestedFields[i];
			if (v3 == v1){
				this.nestedFields[i] = v2;
			}
			else{
				Iterator<NTuple> it = this.nestedFields[i].iterator();
				while (it.hasNext()){
					NTuple t = (NTuple)it.next();
					t.replace(v1, v2);
				}
			}
		}
	}

	/**
	 * aux is horizontal navigation: navigates to undo nesting
	 * 
	 * @param replacementOfInnerNestedTuples
	 * @param nested
	 */
	public void set(int[] path, ArrayList<NTuple> v, int[] tupleIndex) {
		recSet(path, v, tupleIndex, 0, 0);
	}

	/**
	 * Returns the nested field at the K column, if one exists there.
	 * 
	 * @param k
	 * @return
	 */
	public void setNestedField(int k, ArrayList<NTuple> v) {
		int iNested = 0;
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.TUPLE_TYPE) {
				//Parameters.logger.debug(iID + "-th ID is at column " + i);
				if (i == k) {
					nestedFields[iNested] = v;
				}
				iNested++;
			}
		}
	}
	/**
	 * @param aux
	 * @param replacementOfInnerNestedTuples
	 */
	public void setNestedField(int[] aux, ArrayList<NTuple> v) {
		this.recSetNestedFields(aux, v, 0);
	}

	/**
	 * @param k
	 *            the number of the column from which we want the String
	 * @return the String at the K column, if one exists there.
	 */
	public void setStringField(int k, String value) {
		Parameters.logger.info("k: " + k);
		int iString = 0;
		//String temp = "";
		for (int i = 0; i < nrsmd.colNo; i++) {
			if (nrsmd.types[i] == TupleMetadataType.STRING_TYPE) {
				//Parameters.logger.debug(iID + "-th ID is at column " + i);
				if (i == k) {
					//temp = new String(stringFields[iString]);
					stringFields[iString] = value.toCharArray();
				}
				iString++;
			}
		}
	}

	/**
	 * Returns a String representation of an NTuple using , as a
	 * separator between fields.
	 * @return
	 */
	public final String toString(){
		stringRepresentation = new StringBuffer();
		try{
			createStringBuffer();
		}
		catch(Exception e){
			Parameters.logger.error("Exception: ", e);
		}
		return new String(stringRepresentation).replace("\b", ", ");
	}
	
	/**
	 * This method will return an array of strings. Each string will represent
	 * one column of the NTuple. This method is needed for displaying nicely the
	 * NTuples using the GUI.
	 * 
	 * @return an array of strings
	 * @throws VIP2PException
	 */
	public final String[] toStringArray() throws VIP2PException {
		
		String[] collumnArray = new String[nrsmd.colNo];
		int collumnArrayCounter = 0;
		
		int iString = 0;
		int iUri = 0;
		int iID = 0;
		int klNested = 0;

		for (int i = 0; i < nrsmd.colNo; i++) {
			////Parameters.logger.debug("Buffer is: " + forDisplay);
			////Parameters.logger.debug("At column: " + i + " out of " + nrsmd.colNo +"  type: " + Constants.decodeConstant(nrsmd.types[i]));
			switch (nrsmd.types[i]) {
			case STRING_TYPE :
				////Parameters.logger.debug("String field at " + iString + " out of " + stringFields.length);
				if (this.stringFields[iString] == null) {
					collumnArray[collumnArrayCounter] = TupleMetadataType.NULL.toString();
				} else {
					String aux = new String(this.stringFields[iString]);
					collumnArray[collumnArrayCounter] = aux.replaceAll("\n", "");;
				}
				////Parameters.logger.debug("Now buffer is: " + forDisplay);
				iString++;
				collumnArrayCounter++;
				break;
			case URI_TYPE :
				if (this.uriFields[iUri] == null) {
					collumnArray[collumnArrayCounter] = TupleMetadataType.NULL.toString();
				} else {
					String aux = new String(this.uriFields[iUri]);
					collumnArray[collumnArrayCounter] = aux.replaceAll("\n", "");
				}
				iUri++;
				collumnArrayCounter++;
				break;
			case UPDATE_ID :
				collumnArray[collumnArrayCounter] = (this.idFields[iID] == null) ? TupleMetadataType.NULL.toString() : idFields[iID].toString();
				iID++;
				collumnArrayCounter++;
				break;
			case ORDERED_ID :
				collumnArray[collumnArrayCounter] = (this.idFields[iID] == null) ? TupleMetadataType.NULL.toString() : idFields[iID].toString();
				iID++;
				collumnArrayCounter++;
				break;
			case UNIQUE_ID :
				collumnArray[collumnArrayCounter] = (this.idFields[iID] == null) ? TupleMetadataType.NULL.toString() : idFields[iID].toString();
				iID++;
				collumnArrayCounter++;
				break;
			case STRUCTURAL_ID :
				collumnArray[collumnArrayCounter] = (this.idFields[iID] == null) ? TupleMetadataType.NULL.toString() : idFields[iID].toString();
				iID++;
				collumnArrayCounter++;
				break;
			case TUPLE_TYPE :
				collumnArray[collumnArrayCounter] = "[";
				if (this.nestedFields[klNested] == null) {
					collumnArray[collumnArrayCounter] += "null";
				} else {
					if (this.nestedFields != null) {
						Iterator<NTuple> it =
							this.nestedFields[klNested].iterator();
						int kIndexNested = 0;
						while (it.hasNext()) {
							NTuple ntc = (NTuple) it.next();
							//		//Parameters.logger.debug("MOVING TO  " + kIndexNested + "TH TUPLE INTO " + klNested + "TH  CHILD OF " + this.hashCode());
							collumnArray[collumnArrayCounter] += ntc.toString();
							//		//Parameters.logger.debug("COMING BACK FROM NESTED CHILD INTO " + this.hashCode() );
							kIndexNested ++;
						}
					}
				}
				collumnArray[collumnArrayCounter] += "]";
				klNested++;
				collumnArrayCounter++;
				break;
			default :
				throw new VIP2PException("Unknown type");
			}
		}
		
		return collumnArray;
	}

	/**
	 * Returns a String representation of an NTuple using \b as a
	 * separator between fields.
	 * @return
	 */
	public final String toStringForFile() {
		stringRepresentation = new StringBuffer();
		try{
			createStringBuffer();
		}
		catch(Exception e){
			Parameters.logger.error("Exception: ", e);
		}
		return stringRepresentation.toString();
	}

	/**
	 * Adds to the ArrayList v all the fully unnested tuples that can be produced
	 * out of this one.
	 * 
	 * @param v
	 * @return
	 */
	public void unnest(ArrayList<NTuple> v) throws VIP2PException {
		//Parameters.logger.debug("\nUnnesting");
		//display();
		assert (v != null) : "Null vector";
		if (this.nestedFields.length == 0) {
			v.add(this);
			return;
		}
		// there are nested fields

		NTuple t2 = this.flatProjection();

		ArrayList<NTuple>[] childTuples = new ArrayList[this.nestedFields.length];
		boolean unnestable = false;

		for (int i = 0; i < this.nestedFields.length; i++) {
			if (this.nestedFields[i] == null) {
				Parameters.logger.info(NodeInformation.localPID + ": Cannot extract index key out of null nested field.\n" +
						"This may happen due to an R attribute in an optional node.\n" +
						"When no match is found, the tuple is ignored.\n" +
				"Skipped tuple");
				unnestable = true;
			}
		}

		if (unnestable) {
			return;
		}

		for (int i = 0; i < this.nestedFields.length; i++) {
			Iterator<NTuple> iChild = this.nestedFields[i].iterator();
			childTuples[i] = new ArrayList<NTuple>();
			//Parameters.logger.debug("At nested child #" + i);
			while (iChild.hasNext()) {
				NTuple tChild = (NTuple) iChild.next();
				tChild.unnest(childTuples[i]);
			}
		}

		// now all children have been properly unnested into their respective
		// vectors.
		// do a cartesian product.
		ArrayList<NTuple> v0 = new ArrayList<NTuple>();
		v0.add(t2);
		cartProd(v0, childTuples, 0, v);
	}

	/**
	 * Creates a tuple by appending two existing ones.
	 * 
	 * @param nrsmd
	 *            The metadata of the resulting tuple
	 * @param t1
	 *            The first tuple (these fields will come first, in order).
	 * @param t2
	 *            The second tuple (these fields will come next, in order). The
	 *            metadata should be computed by the corresponding append
	 *            method in NRSMD, from the metadata of t1 and t2.
	 * @return @throws
	 *         ULoadExecutionException
	 */
	public static NTuple append(NRSMD nrsmd, NTuple t1, NTuple t2)
	throws VIP2PException {
		NRSMD n1 = t1.nrsmd;
		NRSMD n2 = t2.nrsmd;
		ElementID[] idFields =
			new ElementID[t1.idFields.length + t2.idFields.length];
		int[] integerFields = new int[n1.integerNo + n2.integerNo];
		char[][] stringFields = new char[n1.stringNo + n2.stringNo][];
		char[][] uriFields = new char[n1.uriNo + n2.uriNo][];
		ArrayList<NTuple>[] nestedFields = new ArrayList[n1.nestedNo + n2.nestedNo];

		for (int i = 0; i < t1.idFields.length; i++) {
			idFields[i] = t1.idFields[i];
		}
		for (int i = t1.idFields.length;
		i < t1.idFields.length + t2.idFields.length;
		i++) {
			idFields[i] = t2.idFields[i - t1.idFields.length];
		}

		for (int i = 0; i < t1.integerFields.length; i++) {
			integerFields[i] = t1.integerFields[i];
		}
		for (int i = t1.integerFields.length;
		i < t1.integerFields.length + t2.integerFields.length;
		i++) {
			integerFields[i] = t2.integerFields[i - t1.integerFields.length];
		}
		
		for (int i = 0; i < t1.stringFields.length; i++) {
			stringFields[i] = t1.stringFields[i];
		}
		for (int i = t1.stringFields.length;
		i < t1.stringFields.length + t2.stringFields.length;
		i++) {
			stringFields[i] = t2.stringFields[i - t1.stringFields.length];
		}

		for (int i = 0; i < t1.uriFields.length; i++) {
			uriFields[i] = t1.uriFields[i];
		}
		for (int i = t1.uriFields.length;
		i < t1.uriFields.length + t2.uriFields.length;
		i++) {
			uriFields[i] = t2.uriFields[i - t1.uriFields.length];
		}

		for (int i = 0; i < t1.nestedFields.length; i++) {
			nestedFields[i] = t1.nestedFields[i];
		}
		for (int i = t1.nestedFields.length;
		i < t1.nestedFields.length + t2.nestedFields.length;
		i++) {
			//Parameters.logger.debug("Taking from t2's nested field at " + (i - t1.nestedFields.length));
			//t2.display();

			ArrayList<NTuple> vAux  = t2.nestedFields[i - t1.nestedFields.length];			
			nestedFields[i] = vAux;
		}
		return new NTuple(nrsmd, stringFields, uriFields, idFields, integerFields, nestedFields);
	}

	/**
	 * This unpickles and returns a collection of tuples from the given DataInputStream,
	 * assuming that they are in the same format as output by
	 * {@link #toDataOutput(Collection, DataOutputStream)}.
	 * 
	 * @param in the DataInputStream to unpickle from
	 * @return an ArrayList of unpickled NTuples
	 * @throws IOException if the format is unexpected
	 */
	public static ArrayList<NTuple> fromDataInput(DataInputStream in) throws IOException {
		// Read the number of tuples
		int size = in.readInt();
		ArrayList<NTuple> tuples = new ArrayList<NTuple>(size);
		if ( size > 0 ) {
			// Read the NRSMD
			NRSMD nrsmd = NRSMD.fromDataInput(in);
			for ( int ntup = 0; ntup < size; ntup++ ) {
				// Read and create a single tuple's data
				NTuple tup = new NTuple(nrsmd);

				for ( int i = 0; i < nrsmd.colNo; i++ ) {
					if (nrsmd.types[i] == TupleMetadataType.UNIQUE_ID
							|| nrsmd.types[i] == TupleMetadataType.ORDERED_ID
							|| nrsmd.types[i] == TupleMetadataType.STRUCTURAL_ID) {

						/* We don't know the ElementID subclass from the data stream, so
						 * we must obtain it from the parameter.
						 */
						if (USE_DEPTH) {
							//Parameters.logger.info("NTUPLE: reading from the data input a pre-post-depth");
							int pre = in.readInt();
							int post = in.readInt();
							int depth = in.readInt();
							PrePostDepthElementID id = new PrePostDepthElementID(pre, depth);
							id.setPost(post);
							tup.addID(id);
						} else {
							//Parameters.logger.info("NTUPLE: reading from the data input a pre-post");
							int pre = in.readInt();
							int post = in.readInt();
							PrePostElementID id = new PrePostElementID(pre);
							id.setPost(post);
							tup.addID(id);
						}
					} else if (nrsmd.types[i] == TupleMetadataType.UPDATE_ID) {
						List<Integer> path = new ArrayList<Integer>();
						List<Integer> tagPath = new ArrayList<Integer>();
						Integer integer;
						int pathLength = in.readInt();
						for(int j = 0; j<pathLength; j++) {
							integer = in.readInt();
							path.add(integer);
						}
						for(int j = 0; j<pathLength; j++) {
							integer = in.readInt();
							tagPath.add(integer);
						}
						int[] pathArray = new int[path.size()];
						for(int j = 0; j<path.size(); j++) {
							pathArray[j] = path.get(j);
						}
						
						int[] tagPathArray = new int[tagPath.size()];
						for(int j = 0; j<tagPath.size(); j++) {
							tagPathArray[j] = tagPath.get(j);
						}
						
						CompactDynamicDeweyID id = new CompactDynamicDeweyID(pathArray, tagPathArray);
						tup.addID(id);
					} else if (nrsmd.types[i] == TupleMetadataType.URI_TYPE) {
						tup.addUri(in.readUTF());
					} else if (nrsmd.types[i] == TupleMetadataType.INTEGER_TYPE) {
						tup.addInteger(Integer.parseInt(in.readUTF()));
					} else if (nrsmd.types[i] == TupleMetadataType.STRING_TYPE) {
						//first read the number of slices that the XML was divided into
						long slices = in.readLong();
						// read all the XML's slices and add them to the same tuple
						if(slices == 0)
							tup.addStringToSame("");
						else
						{
							StringBuffer strb = new StringBuffer();
							for (int ii=0; ii< slices; ii++){
								strb.append(in.readUTF());
							}
							tup.addStringToSame(strb.toString());
						}
						//increase iString field for the tuple 
						tup.incIString();
					} else {
						throw new IOException("NRSMD column type not yet supported: " + nrsmd.types[i].toString());
					}
				}
				tuples.add(tup);
			}
		}
		return tuples;
	}
	
	/**
	 * Adds a nested field to a tuple.
	 * @param nrsmd The metadata of the destination tuple (including the nested field).
	 * @param t1 The tuple from which to take all information but the nested field.
	 * @param v The nested field (vector of tuples).
	 * @return A new tuple with all the information.
	 * @throws VIP2PException
	 */
	public static NTuple nestField(NRSMD nrsmd, NTuple t1, ArrayList<NTuple> v)
	throws VIP2PException {
		NRSMD n1 = t1.nrsmd;

		ElementID[] idFields = new ElementID[t1.idFields.length];
		char[][] stringFields = new char[n1.stringNo][];
		char[][] uriFields = new char[n1.uriNo][];
		int[] integerFields = new int[nrsmd.integerNo];

		ArrayList<NTuple>[] nestedFields = new ArrayList[n1.nestedNo + 1];

		for (int i = 0; i < t1.idFields.length; i++) {
			idFields[i] = t1.idFields[i];
		}

		for (int i = 0; i < t1.stringFields.length; i++) {
			stringFields[i] = t1.stringFields[i];
		}

		for (int i = 0; i < t1.uriFields.length; i++) {
			uriFields[i] = t1.uriFields[i];
		}
		
		for (int i = 0; i < t1.integerFields.length; i++) {
			integerFields[i] = t1.integerFields[i];
		}

		for (int i = 0; i < t1.nestedFields.length; i++) {
			nestedFields[i] = t1.nestedFields[i];
		}

		nestedFields[t1.nestedFields.length] = new ArrayList<NTuple>();
		nestedFields[t1.nestedFields.length] = v;

		NTuple nestedTuple =
			new NTuple(nrsmd, stringFields, uriFields, idFields, integerFields, nestedFields);

		return nestedTuple;
	}

	public static NTuple nullTuple(NRSMD nrsmd)	throws VIP2PException {
		
		if(nrsmd==null){
			nrsmd = new NRSMD(0,new TupleMetadataType[0]);
		}
		
		char[][] stringFields = new char[nrsmd.stringNo][0];
		char[][] uriFields = new char[nrsmd.uriNo][0];
		ArrayList<NTuple>[] nestedFields = new ArrayList[nrsmd.nestedNo];
		for (int i = 0; i < nestedFields.length; i ++){
			nestedFields[i] = new ArrayList<NTuple>();
		}
		ElementID[] idFields = new ElementID[nrsmd.idsNo + nrsmd.cddIDsNo + 
		                                     nrsmd.prePostIDsNo];
		int[] integerFields = new int[nrsmd.integerNo];
		NTuple t = new NTuple(nrsmd, stringFields, uriFields, idFields, integerFields, nestedFields);
		t.isNull = true;
		t.clean();
		return t;
	}
	
	public boolean isNull(){
		return isNull;
	}
	
	public static String print(int[] n){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n.length; i ++){
			sb.append(n[i] + " ");
		}
		return new String(sb);
	}

	/**
	 * @param t
	 *            Tuple from which we want to project
	 * @param nrsmd
	 *            Metadata of the projected (resulting) tuple
	 * @param keepColumns
	 *            Columns of the old tuple which were kept in the new tuple
	 * @return
	 */
	public static NTuple project(NTuple t, NRSMD nrsmd, int[] keepColumns) {
		NTuple res = new NTuple(nrsmd);
		for (int i = 0; i < keepColumns.length; i++) {
			int k = keepColumns[i];
			try{
			switch (t.nrsmd.types[k]) {
			case STRING_TYPE :
				res.addString(t.getStringField(k));
				break;
			case URI_TYPE :
				res.addUri(t.getUriField(k));
				break;
			case INTEGER_TYPE :
				res.addInteger(t.getIntegerField(k));
				break;
			case UNIQUE_ID :
			case ORDERED_ID :
			case STRUCTURAL_ID :
			case UPDATE_ID :
				res.addID(t.getIDField(k));
				break;
			case TUPLE_TYPE :
				//Parameters.logger.debug("Adding nested field at " +
				// res.iNested +	" from column " + k);
				res.addNestedField(t.getNestedField(k));
				break;
			}
			}catch (Exception e) {
				e.printStackTrace();
				Parameters.logger.info("error: " + e);
			}
		}
		return res;
	}

	/*
	public static NTuple project(NTuple t, NRSMD nrsmd, ProjMask m) throws VIP2PException{
		int[] keepColumns = new int[m.columns.size()];
		for (int i = 0; i < keepColumns.length; i ++){
			keepColumns[i] = ((Integer)m.columns.get(i)).intValue();
		}
		NRSMD intermedNRSMD = NRSMD.makeProjectRSMD(t.nrsmd, keepColumns);

		NTuple res = project(t, intermedNRSMD, keepColumns);

		Iterator<Integer> it = m.keepFromChildren.keySet().iterator();
		while (it.hasNext()){
			Integer KeptChild = (Integer)it.next();
			int keptChildIdx = KeptChild.intValue();
			ProjMask mChild = (ProjMask)m.keepFromChildren.get(KeptChild);

			ArrayList<NTuple> childAtIdx = res.getNestedField(keptChildIdx);
			NRSMD nrsmdAtIdx = nrsmd.getNestedChild(keptChildIdx);

			ArrayList<NTuple> childReplace = new ArrayList<NTuple>();
			Iterator<NTuple> childIt = childAtIdx.iterator();
			while (childIt.hasNext()){
				NTuple tChild = (NTuple)childIt.next();
				childReplace.add(project(tChild, nrsmdAtIdx, mChild));
			}

			res.setNestedField(keptChildIdx, childReplace);
		}
		res.nrsmd = nrsmd;
		return res;

	}
*/
	/**
	 * <p>This pickles any collection of tuples into the given DataOutputStream.
	 * The output format is:</p>
	 * 
	 * <ul>
	 * <li>Int <i>size</i></li>
	 * <li>The common NRSMD of all NTuples, as output by
	 * {@link NRSMD#toDataOutput(NRSMD, DataOutputStream)}</li>
	 * <li><i>size</i> times:<br />the tuple data in the order of the NRSMD</li>
	 * </ul>
	 * 
	 * <p>URI and String fields are output as UTF strings. PrePostDepthElementIDs are
	 * output as (Int <i>pre</i>,Int <i>post</i>,Int <i>depth</i>); other IDs are not
	 * currently supported.</p>
	 * 
	 * @param data the collection of NTuples to pickle
	 * @param out the DataOutputStream to write to
	 * @throws IOException from the DataOutputStream's output methods
	 */
	public static void toDataOutput(Collection<NTuple> data, DataOutputStream out) throws IOException {
		// Output the number of tuples
		out.writeInt(data.size());
		if ( data.size() > 0 ) {
			// Assumption: all NTuples in a packet have the same NRSMD
			// Output the NRSMD
			NRSMD nrsmd = data.iterator().next().nrsmd;
			NRSMD.toDataOutput(nrsmd, out);
			for ( NTuple tup: data ) {
				// Output a single tuple's data
				int iID = 0;
				int iString = 0;
				int iInteger = 0;
				int iURI = 0;

				for ( int i = 0; i < nrsmd.colNo; i++ ) {
					if (nrsmd.types[i] == TupleMetadataType.STRUCTURAL_ID) {
						//Parameters.logger.info("At " + iID + " found " + zID.getClass().getName());
						//Parameters.logger.info("Use depth: " + USE_DEPTH);
						try {
							if (USE_DEPTH){
								// Output a PrePostDepthElementID as (Int,Int,Int) 
								PrePostDepthElementID id = (PrePostDepthElementID) tup.idFields[iID++];
								out.writeInt(id.pre);
								out.writeInt(id.post);
								out.writeInt(id.depth);
							}else{
								PrePostElementID id = (PrePostElementID) tup.idFields[iID++];
								out.writeInt(id.pre);
								out.writeInt(id.post);
							}
						} catch ( ClassCastException e ) {
							throw new IOException("This ElementID subclass is not yet supported.", e);
						}
					} else if (nrsmd.types[i] == TupleMetadataType.UPDATE_ID) {
						CompactDynamicDeweyID id = (CompactDynamicDeweyID) tup.idFields[iID++];
						int[] path = id.getPath();
						/*
						 * This is output so we know the length of the path and tag path to allow us to know how many ints
						 * to extract when translating back to NTuple
						 */ 
						out.writeInt(path.length);
						for(int j = 0; j<path.length; j++) {
							out.writeInt(path[j]);
						}
						int[] tagPath = id.getTag();
						for(int j = 0; j<tagPath.length; j++) {
							out.writeInt(tagPath[j]);
						}
					} else if (nrsmd.types[i] == TupleMetadataType.URI_TYPE) {
						out.writeUTF(new String(tup.uriFields[iURI++]));
					} else if (nrsmd.types[i] == TupleMetadataType.INTEGER_TYPE) {
						out.writeUTF(Integer.toString(tup.integerFields[iInteger++]));
					} else if (nrsmd.types[i] == TupleMetadataType.STRING_TYPE) {
						//next code is a workaround for the JVM limitation: Strings that are bigger than 64k bytes in the UTF-8 encoding are not to be used
						//max size of an UTF string object to write is 64k and 1 char = 2bytes
						//so 32000 X 2 = 64000 ( < 64K )
						final int SLICE_MAX_SIZE = 32000;
						//the number of slices this string containing XML will be divided into
						long slices = (long) Math.ceil( (double) tup.stringFields[iString].length / SLICE_MAX_SIZE);
						//save the number of slices
						out.writeLong(slices);

						for(int slice_no=0;slice_no<slices;slice_no++){
							int chars_to_read = SLICE_MAX_SIZE;

							//Do not exceed the size of the array
							if( ((slice_no+1)*SLICE_MAX_SIZE) > tup.stringFields[iString].length)
								chars_to_read = tup.stringFields[iString].length - slice_no*SLICE_MAX_SIZE;

							out.writeUTF(String.valueOf(tup.stringFields[iString], slice_no*SLICE_MAX_SIZE, chars_to_read));
						}
						out.flush();
						iString++;
					} else {
						throw new IOException("NRSMD column type not yet supported: " + nrsmd.types[i].toString());
					}
					out.flush();
				}
			}
			out.flush();
		}
	}
	
	/**
	 * @author Karan AGGARWAL
	 * 
	 * @return Returns a String representation of DocID found in the 1st URI of a tuple
	 */
	public final String getDocID(){
		if (this.uriFields[0] == null) {
			return TupleMetadataType.NULL.toString();
		} else {
			return new String(this.uriFields[0]);
		}

	}
	/**
	 * to print those strings fileds in this tuple
	 */
	public void printStrs(){
		//char[] str = null;
		for(int i=0; i<this.stringFields.length; i++){
			System.out.print(this.stringFields[i]);
			if(i != this.stringFields.length-1){
				System.out.print(" | ");
			}
		}
		System.out.print("\n");
	}

	/**
	 * to print those Integer fileds in this tuple
	 */
	public void printInts(){
		for(int i=0; i<this.integerFields.length; i++){
			System.out.print(this.integerFields[i]);
			if(i != this.integerFields.length-1){
				System.out.print(" | ");	
			}
			}
		System.out.print("\n");
	}

	
}
