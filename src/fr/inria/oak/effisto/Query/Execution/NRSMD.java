package fr.inria.oak.effisto.Query.Execution;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fr.inria.oak.effisto.Parameters.*;
import fr.inria.oak.effisto.IDs.*;
import fr.inria.oak.effisto.Exception.*;
import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.*;

/**
 * NRSMD stands for "Nested Result Set MetaData". The NRSMD represents the
 * metadata of the tuples (which kind of data we are going to find and in which
 * position in the tuple. The methods in this class help us manage the tuples
 * metadata.
 * 
 * @author Ioana MANOLESCU
 * @author Spyros ZOUPANOS
 * 
 */
public class NRSMD implements Serializable {

	private static final long serialVersionUID = -7830817757813434255L;

	/* Constants */
	/**
	 * Warning: here, this constant is set true in Effisto!
	 */
	//private static final boolean STORE_DERIVATION_COUNT = Boolean.parseBoolean(Parameters.getProperty("updatesAlgorithm.storeDerivationCount"));
	private static final boolean STORE_DERIVATION_COUNT  = true;
	/**
	 * Needed when naming columns while traversing the XAM. For finding required attributes
	 */

	private int nodeCount;
	/**
	 * estimated number of tuples 
	 */
	public int cardinality;

	public int cddIDsNo;

	/**
	 * The Strings which constitute the column names
	 */
	public String[] colNames;

	/**
	 * the number of columns
	 */
	public int colNo;

	public int idsNo;

	public int integerNo;

	NRSMD[] nestedChildren;

	public int nestedNo;

	public int prePostIDsNo;

	public int stringNo;
	
	public int nullNo;

	/**
	 * for each column state the actual data type of the column
	 */
	public TupleMetadataType[] types;

	public int uriNo;

	
	/**
	 * To be used for un-nested RSMDs
	 * 
	 * @param colNo
	 * @param types
	 */
	public NRSMD(int colNo, TupleMetadataType[] types) throws VIP2PException {
		this.colNo = colNo;
		this.types = types;
		this.colNames = new String[colNo];
		int iNested = 0;
		for (int i = 0; i < types.length; i++) {
			if (types[i] == TupleMetadataType.TUPLE_TYPE) {
				iNested++;
			}
		}
		
		this.nestedChildren = new NRSMD[iNested];
		countTypes(types);
	}

	/**
	 * Construct a new NRSMD object
	 * 
	 * @param colNo -
	 *            no of columns in the tuple
	 * @param types -
	 *            for each column the code of the data-type describing that
	 *            field
	 * 
	 * @param colNames -
	 *            colum names
	 * @param nestedChildren -
	 *            nested metadata children
	 */
	public NRSMD(int colNo,	TupleMetadataType[] types, String[] colNames, NRSMD[] nestedChildren) throws VIP2PException {
		this.colNo = colNo;
		this.types = types;
		this.colNames = colNames;
		this.nestedChildren = nestedChildren;
		countTypes(types);
		this.nestedNo = nestedChildren.length;
		
		//added by nttho
		//if don't have statistic
		//This is used in the logical Operator, so the statistic is not necessary
	}

	// deep copy
	/*
	 * This constructor lacks of the init for statistic
	 * and it is used only for Logical Operator
	 */
	public NRSMD(TupleMetadataType[] types, NRSMD[] newNestedChildren) throws VIP2PException {
		this.colNo = types.length;
		// sometimes there are 0 columns, for non-contributing nodes. Hopefully
		// not a problem...
		// if (this.colNo == 0){
		//  	throw new ULoadExecutionException("O columns");
		// }
		this.types = new TupleMetadataType[types.length];
		for (int i = 0; i < this.types.length; i++) {
			this.types[i] = types[i];
		}
		
		this.nestedChildren = new NRSMD[newNestedChildren.length];
		
		for (int i = 0; i < nestedChildren.length; i++) {
			this.nestedChildren[i] =
				new NRSMD(
					newNestedChildren[i].types,
					newNestedChildren[i].nestedChildren);
		}
		this.colNames = new String[colNo];
		countTypes(types);
		this.nestedNo = nestedChildren.length;		
	}

	private void countTypes(TupleMetadataType[] types) throws VIP2PException {
		//Parameters.logger.debug("Counting metadata types");
		this.stringNo = 0;
		this.uriNo = 0;
		this.prePostIDsNo = 0;
		this.idsNo = 0;
		this.nestedNo = 0;
		this.cddIDsNo = 0;
		this.integerNo = 0;
		for (int i = 0; i < types.length; i++) {
			switch (types[i]) {
				case STRING_TYPE :
					this.stringNo++;
					//Parameters.logger.debug("Counting: at " + i + " string");
					break;
				case URI_TYPE :
					this.uriNo++;
					break;
				case INTEGER_TYPE :
					this.integerNo++;
					break;
				case ORDERED_ID :
				case UNIQUE_ID :
					this.idsNo++;
					break;
				case UPDATE_ID :
					this.cddIDsNo++;
					break;
				case STRUCTURAL_ID :
					this.prePostIDsNo++;
					break;
				case TUPLE_TYPE :
					this.nestedNo++;
					break;
				case NULL:
					this.nullNo++;
					break;
				default :
					//Parameters.logger.info(Constants.decodeConstant(types[i]));

					throw new VIP2PException(
						"Unknown type at " + i + ": " + types[i]);
			}
		}
	}
	
	public boolean equals(Object o) {
		NRSMD other = null;
		try {
			other = (NRSMD) o;
		} catch (ClassCastException cce) {
			Parameters.logger.error("Exception: ",cce);
			return false;
		}

		if (this.colNo != other.colNo) {
			//Parameters.logger.debug("Unequal 1");
			return false;
		}
		for (int i = 0; i < colNo; i++) {
			if (this.types[i] != other.types[i]) {
				//Parameters.logger.debug("Unequal 2 " + i);
				return false;
			}
		}
		for (int i = 0; i < this.nestedNo; i++) {
			if (!this.nestedChildren[i].equals(other.nestedChildren[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Computes the metadata of a tuple that only holds the direct attributes
	 * of this one (erasing all nested children).
	 * 
	 * @return @throws
	 *         ULoadExecutionException
	 */
	public NRSMD flatProjection() throws VIP2PException {
		int k = this.colNo - this.nestedChildren.length;
		TupleMetadataType[] newTypes = new TupleMetadataType[k];
		//Parameters.logger.debug("There will be " + k + " columns in the result");
		String[] newNames = new String[k];
		int j = 0;
		for (int i = 0; i < this.colNo; i++) {
			if (types[i] != TupleMetadataType.TUPLE_TYPE) {
				newTypes[j] = types[i];
				//Parameters.logger.debug("Type at " + j + " " +
				// Constants.decodeConstant(newTypes[j]));
				newNames[j] = colNames[i];
				j++;
			}
		}
		return new NRSMD(k, newTypes, newNames, new NRSMD[0]);
	}

	public final String[] getColNames() {
		return colNames;
	}
	public int getColNo() {
		return colNo;
	}

	/**
	 * @param k
	 *            the index in the total columns
	 * @return the nested child metadata at that index, if any
	 */
	public NRSMD getNestedChild(int k) {
		//Parameters.logger.debug("\n\nNRSMD: getting nested child at " + k + 
			//	" out of " + this.nestedNo);
		int iNested = 0;
		for (int i = 0; i < types.length; i++) {
			if (types[i] == TupleMetadataType.TUPLE_TYPE) {
				if (i == k) {
					//this.nestedChildren[iNested].display();
					return this.nestedChildren[iNested];
				} else {
					iNested++;
				}
			}
		}
		Parameters.logger.error("No nested child at " + k);
		return null;
	}

	/**
	 * @param aux
	 * @return
	 */
	public NRSMD getNestedChild(int[] aux) {
		return recGetNestedChild(aux, 0);
	}
	
	public int[] getResultSetIndex(int col){
		int index =0;
		try {
			for(int i=0;i<colNo;i++){
				if(col==i){
					int[] indices;
					if(this.types[i]==TupleMetadataType.STRUCTURAL_ID){
						indices = new int[3];
						indices[0]=index+1;
						indices[1]=index+2;
						indices[2]=index+3;
					}
					else{
						indices = new int[1];
						indices[0] = index+1;
					}
					return indices;
				}
				switch (this.types[i]) {
				case STRING_TYPE :
				case UPDATE_ID :
				case ORDERED_ID :
				case UNIQUE_ID :
					index++;
					break;
				case STRUCTURAL_ID :
					index+=3;
					break;
				case TUPLE_TYPE :
					throw new VIP2PException("Nested tuples cannot be constructed out of SQL flat result sets");
				}
				
			}
		} catch (VIP2PException e) {
			Parameters.logger.error("Exception: ",e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param k - the absolute number of the column 
	 * @return the column that holds the URI which corresponds to column k 
	 */
	public int getRespectiveUriCol(int k){
		
		for(int i = k;i >= 0; i--){
			if(types[i] == TupleMetadataType.URI_TYPE){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the array with the metadata of the columns of the NRSMD.
	 * @return an array with the types of the columns of the NRSMD
	 */
	public TupleMetadataType[] getColumnsMetadata() {
		return types;
	}
	
	/**
	 * Get the string representation for the metadata of the selected column.
	 * @param col the column
	 * @return a string with the type of the selected column 
	 */
	public String getColumnMetadata(int col){
		if(col < colNo && col >= 0){
			return types[col].toString();
		}else{
			return null;
		}
	}
	
	private NRSMD recGetNestedChild(int[] aux, int n) {
		assert (n < aux.length) : "NRSMD: getting nested child at " + n +
					"-th position in an array of length " + aux.length + 
					" out of " + this.nestedNo + " nested children";
		if (n == aux.length - 1) {
			return this.getNestedChild(aux[n]);
		} else {
			int k = aux[n];
			return this.getNestedChild(k).recGetNestedChild(aux, (n + 1));
		}
	}
	
	private TupleMetadataType recGetType(int[] idx, int from) {
		if (from == idx.length - 1) {
			return types[idx[from]];
		} else {
			return this.getNestedChild(idx[from]).recGetType(idx, from + 1);
		}
	}
	
	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	void setNestedChild(int k, NRSMD nrsmd) {
		int iNested = 0;
		for (int i = 0; i < types.length; i++) {
			if (types[i] == TupleMetadataType.TUPLE_TYPE) {
				if (i == k) {
					this.nestedChildren[iNested] = nrsmd;
					return;
				} else {
					iNested++;
				}
			}
		}
		Parameters.logger.error("No nested child at " + k);
	}
	
	/**
	 * Returns the string representation of the NRSMD.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		recToString(sb);
		return new String(sb);
	}
	
	/*
	 * Recursive method that builds the string representation of the NRSMD. 
	 * @param sb the string buffer that will be filled with the string
	 * representation of NRSMD
	 */
	private void recToString(StringBuffer sb) {
		sb.append("(");
		assert (types != null) : "Null types";
		int iNested = 0;
		for (int i = 0; i < types.length; i++) {
			//Parameters.logger.debug(i + ": " + Constants.decodeConstant(types[i]) + " ");
			if (i > 0) {
				sb.append(" ");
			}
			if (types[i] != TupleMetadataType.TUPLE_TYPE) {
				sb.append(i + ":" + types[i].toString());
			} else {
				sb.append("[");
				//Parameters.logger.debug("Out of " + nestedChildren.length 
				//		+ " children MDs, looking at " +  iNested);
				if (this.nestedChildren[iNested] == null) {
					sb.append("null");
				} else {
					this.nestedChildren[iNested].recToString(sb);
				}
				iNested++;
				sb.append("]");
			}
		}
		sb.append(")");
	}
	
	/**
	 * Method for displaying the NRSMD through the logger.
	 */
	public void display() {
		Parameters.logger.info("NRSMD> " + this.toString());
	}
	
	/**
	 * @param nrsmd
	 * @param ancPath
	 * @param nrsmd2
	 * @return
	 */
	public static NRSMD addNestedField(NRSMD n1, int[] ancPath, NRSMD n2) throws VIP2PException {

//				Parameters.logger.info("\nAdding ");
//				n2.display();
//				Parameters.logger.info(" as nested field of ");
//				n1.display();
		//		Parameters.logger.info(" on " + OrderUtility.print(ancPath));
		//				
		if (ancPath.length == 0) {
			return NRSMD.appendNRSMD(n1, n2);
		} else {
			// make a deep copy of n1
			// Should add the statistic information of n1
			NRSMD newN1 = new NRSMD(n1.types, n1.nestedChildren);

			// go modify in the first child
			NRSMD child = newN1.getNestedChild(ancPath[0]);
			int[] aux = new int[ancPath.length - 1];
			for (int i = 1; i < ancPath.length; i++) {
				aux[i - 1] = ancPath[i];
			}
			// put the transformed child in place
			NRSMD transformedChild = addNestedField(child, aux, n2);
			newN1.setNestedChild(ancPath[0], transformedChild);
			return newN1;
		}
	}
	
	//Statistic
	public static NRSMD addNestedField(NRSMD n1, NRSMD n2) throws VIP2PException {
		int colNo = n1.colNo + 1;
		TupleMetadataType[] types = new TupleMetadataType[colNo];
		String[] colNames = new String[colNo];
		NRSMD[] nestedChildren = new NRSMD[n1.nestedChildren.length + 1];

		for (int i = 0; i < n1.colNo; i++) {
			types[i] = n1.types[i];
			colNames[i] = n1.colNames[i];
		}
		types[n1.colNo] = TupleMetadataType.TUPLE_TYPE;
		colNames[n1.colNo] = n2.colNames[0];
		for (int i = 0; i < n1.nestedChildren.length; i++) {
			nestedChildren[i] = n1.nestedChildren[i];
		}
		nestedChildren[n1.nestedChildren.length] = n2;
		
		
		return new NRSMD(colNo, types, colNames, nestedChildren);
	}
	private static ArrayList<Integer> append(ArrayList<Integer> v1, ArrayList<Integer> v2) {
		ArrayList<Integer> v3 = new ArrayList<Integer>();

		Iterator<Integer> it1 = v1.iterator();
		while (it1.hasNext()) {
			v3.add(it1.next());
		}
		Iterator<Integer> it2 = v2.iterator();
		while (it2.hasNext()) {
			v3.add(it2.next());
		}
		return v3;
	}
	public static NRSMD appendNRSMD(NRSMD n1, NRSMD n2) throws VIP2PException {
		int colNo = n1.colNo + n2.colNo;
		TupleMetadataType[] types = new TupleMetadataType[colNo];
		String[] colNames = new String[colNo];
		NRSMD[] nestedChildren =
			new NRSMD[n1.nestedChildren.length + n2.nestedChildren.length];

		for (int i = 0; i < n1.colNo; i++) {
			types[i] = n1.types[i];
			colNames[i] = n1.colNames[i];
		}
		for (int i = n1.colNo; i < colNo; i++) {
			types[i] = n2.types[i - n1.colNo];
			colNames[i] = n2.colNames[i - n1.colNo];
		}
		for (int i = 0; i < n1.nestedChildren.length; i++) {
			nestedChildren[i] = n1.nestedChildren[i];
		}
		for (int i = n1.nestedChildren.length;
			i < nestedChildren.length;
			i++) {
			nestedChildren[i] = n2.nestedChildren[i - n1.nestedChildren.length];
		}
			return new NRSMD(colNo, types, colNames, nestedChildren);
	}
	/**
	 * Gets a list of NRSMDs, appends all NRSMDs found in it and returns the new NRSMD
	 * 
	 * @param nrsmdList the list with NRSMDs that will be appended
	 * @return the NRSMD that emerges from appending the elements of nrsmdList
	 * @author Konstantinos KARANASOS
	 */
	public static NRSMD appendNRSMDList(ArrayList<NRSMD> nrsmdList) throws VIP2PException  {
		if (nrsmdList.size() == 0)
			return null;
		
		Iterator<NRSMD> nrsmdIter = nrsmdList.iterator();
		//initially, the current result contains the NRSMD of the first element of the nrsmdList
		NRSMD crtResult = nrsmdIter.next();
		NRSMD prevResult = null;
		
		while (nrsmdIter.hasNext()) {
			prevResult = crtResult;
			crtResult = appendNRSMD(prevResult, nrsmdIter.next());
		}
		
		return crtResult;
	}
	
	public static NRSMD emptyNRSMD() throws VIP2PException {
		int colNo = 0;
		TupleMetadataType[] types = new TupleMetadataType[colNo];
		return new NRSMD(colNo, types);
	}
	/**
	 * Gather information about this and all children in two ArrayLists of types
	 * and nested children
	 * 
	 * @param node
	 * @param nTypes
	 * @param nNested
	 * @throws VIP2PException
	 * @throws VIP2PException
	 */
	private static void enrichNRSMD(
		QueryTreeNode node,
		boolean derivationCount,
		ArrayList<TupleMetadataType> nTypes,
		ArrayList<NRSMD> nNested,
		HashMap<Integer, HashMap<String, ArrayList<Integer>>> mappings,
		ArrayList<Integer> currentAddress,
		boolean nestedInParent)
		throws VIP2PException, VIP2PException {

		HashMap<String, ArrayList<Integer>> thisNodesAttributes = new HashMap<String, ArrayList<Integer>>();
		int thisNodesAttributeCount = 0;
		if(derivationCount)
			thisNodesAttributeCount = 1;
		int thisNodesCount = 0;
		

		//Parameters.logger.debug("\nENRICH ON " + node.tag + " node address is: ");
		//display(currentAddress);

		if (nestedInParent) {
			if (currentAddress.size() > 0) {
				thisNodesCount =
					((Integer) currentAddress.get(0)).intValue();
			}
		} else { // not nested
			if (currentAddress.size() > 0) {
				thisNodesCount =
					((Integer) currentAddress
						.get(currentAddress.size() - 1))
						.intValue();
				thisNodesAttributeCount = thisNodesCount;
				currentAddress.remove(currentAddress.size() - 1);
			}
		}

		//Parameters.logger.debug("STARTING WITH ATTRCOUNT " +
		//	thisNodesAttributeCount + " AND NODE COUNT "+ thisNodesCount);

		if (mappings != null) {
			mappings.put(
				(new Integer(node.getNodeCode())),
				thisNodesAttributes);
		}
		if (nestedInParent) {
			thisNodesAttributes.put("Node", currentAddress);
		}

		// here we attempt to properly take into account doc IDs.
		// If we are in a setting with docIDs: 
		// - When traversing the root for metadata, nothing will be collected
		//   other than the various children. It is an unique occasion to 
		//   shift indices so that the docID column will be counted at column
		//   zero, before anything else.
		//   In the types, the docID column has already
		//   been added; now we only build the mappings table.
		// - When traversing the other children, we no longer shift any address
		//   (shift was needed only once by the docID).
		if (node.getTag() != null && node.getTag().length() > 0){
			ArrayList<Integer> v1 = new ArrayList<Integer>();
			v1.add(0);
			thisNodesAttributes.put("docID", v1);
			if(derivationCount) {
				ArrayList<Integer> v2 = new ArrayList<Integer>();
				v2.add(1);
				thisNodesAttributes.put("derivationCount", v2);
			}
			//Parameters.logger.debug("Put on docID  " + v1);
			//Parameters.logger.debug("Put on derivation count  " + v2);
		}
		else{
			thisNodesAttributeCount++;
			thisNodesCount++;
		}
		
		if (node.storesID()) {
			TupleMetadataType idType = IDTypes(IDSchemeAssignator.getIDScheme(node));
			nTypes.add(idType);
			ArrayList<Integer> v = new ArrayList<Integer>();
			v.add(new Integer(thisNodesAttributeCount));
			thisNodesAttributes.put("ID", append(currentAddress, v));
			if(node.requiresID()){
				thisNodesAttributes.put("R", append(currentAddress, v));
			}
			//Parameters.logger.debug("Put on ID " + thisNodesAttributes.get("ID"));
			thisNodesAttributeCount++;
			if (!nestedInParent) {
				thisNodesCount++;
			}
		}

		if (node.storesTag()) {
			nTypes.add(TupleMetadataType.STRING_TYPE);

			ArrayList<Integer> v = new ArrayList<Integer>();
			v.add(new Integer(thisNodesAttributeCount));
			thisNodesAttributes.put("Tag", append(currentAddress, v));
			if(node.requiresTag()){
				thisNodesAttributes.put("R", append(currentAddress, v));
			}
			thisNodesAttributeCount++;
		}
		
		// The commented line below was the fix for the bug #9640.
		// However, it causes problems when we try to select on the
		// value of a pattern node (a selection is never inserted at the
		// logical & physical plan)
		// Comment by Jesus: This has been solved with r547 by Ioana.
		if (node.storesValue()) {
			nTypes.add(TupleMetadataType.STRING_TYPE);

			ArrayList<Integer> v = new ArrayList<Integer>();
			v.add(new Integer(thisNodesAttributeCount));
			thisNodesAttributes.put("Val", append(currentAddress, v));
			//Parameters.logger.debug("Put on Val " + thisNodesAttributes.get("Val"));
			if(node.requiresVal()){
				thisNodesAttributes.put("R", append(currentAddress, v));
			}
			thisNodesAttributeCount++;
		}
		if (node.storesContent()) {
			nTypes.add(TupleMetadataType.STRING_TYPE);

			ArrayList<Integer> v = new ArrayList<Integer>();
			v.add(new Integer(thisNodesAttributeCount));
			thisNodesAttributes.put("Cont", append(currentAddress, v));
			thisNodesAttributeCount++;
		}

		int k = 0;
		for(QueryTreeEdge pe: node.getEdges()) {
			if (pe.isNested()) {

				ArrayList<Integer> v = new ArrayList<Integer>();
				v.add(new Integer(thisNodesAttributeCount + k));
				v = append(currentAddress, v);

				NRSMD aux = getNRSMD(pe.n2,derivationCount,mappings, v, true);
				nNested.add(aux);
				nTypes.add(TupleMetadataType.TUPLE_TYPE);
				k++;
			} else {

				ArrayList<Integer> v = new ArrayList<Integer>();
				v.add(new Integer(thisNodesAttributeCount + k));
				v = append(currentAddress, v);

				enrichNRSMD(pe.n2, derivationCount, nTypes, nNested, mappings, v, false);
				k += flatAttributeNumber(pe.n2);
			}
		}
		//Parameters.logger.debug("ENRICH ON " + node.tag + " OVER");
	}
	
	/**
	 * @param node
	 * @return
	 */
	private static int flatAttributeNumber(QueryTreeNode node) {
		int n = 0;
		if (node.storesID()) {
			n++;
		}
		if (node.storesTag()) {
			n++;
		}
		if (node.storesValue()) {
			n++;
		}
		if (node.storesContent()) {
			n++;
		}
		for(QueryTreeEdge e2: node.getEdges()) {
			if (!e2.isNested()) {
				int k = flatAttributeNumber(e2.n2);
				n += k;
			} else {
				n++;
			}
		}
		return n;
	}

	/**
	 * This unpickles and returns an NRSMD from the given DataInputStream, assuming that it
	 * is in the same format as output by {@link #toDataOutput(NRSMD, DataOutputStream)}.
	 * 
	 * @param in the DataInputStream to unpickle from
	 * @return the unpickled NRSMD
	 * @throws IOException if the format is unexpected
	 */
	public static NRSMD fromDataInput(DataInputStream in) throws IOException {
		int colNo = in.readInt();
		TupleMetadataType[] types = new TupleMetadataType[colNo];
		for ( int i = 0; i < colNo; i++ ) {
			// Assumption: Type constants are less than 255
			types[i] = TupleMetadataType.getTypeEnum(in.readUTF());
		}
		try {
			return new NRSMD(colNo, types);
		} catch (VIP2PException e) {
			throw new IOException("Error unpickling NRSMD", e);
		}
	}

	/**
	 * Produces the NRSMD for a given XamNode.
	 * 
	 * Adds in mappings: 
	 * 
	 * -- for every hash code of a XAM node from which the
	 * xam node originated 
	 * 
	 *     -- a hash map containing: 
	 *            
	 *              -- on "ID", a ArrayList which is the address, inside the NRSMD, 
	 *                 of the node's ID, if the node's ID is stored 
	 * 
	 *              -- on "Tag", a ArrayList which is the address, inside the
	 *                 NRSMD, of the node's Tag, if the node's Tag is stored 
	 * 
	 *              -- on "Val", a ArrayList which is the address, inside the NRSMD, 
	 *                 of the node's Value if the node's Val is stored 
	 * 
	 *              -- on "Cont", a ArrayList which is the address, inside the NRSMD, 
	 *                 of the node's Cont, if the node's Cont is stored
	 * 
	 * @param node
	 * @return @throws
	 *         ULoadException
	 * @throws VIP2PException
	 */

	//TODO Note that the derivationCount that we pass as a parameter already exists in the class as a 
	//a variable. There are times that we want to return the NRSMD with the count and sometimes that
	//we don't. At some point a refactoring is needed to handle derivationCount more uniform.
	public static NRSMD getNRSMD(QueryTreeNode node, boolean derivationCount, HashMap<Integer, HashMap<String, ArrayList<Integer>>> mappings) throws VIP2PException, VIP2PException {
		return getNRSMD(node, STORE_DERIVATION_COUNT && derivationCount, mappings, new ArrayList<Integer>(), false);
	}

	/**
	 * Extract the required columns along with their Type from the given node's NRSMD
	 *  
	 * @param node
	 * @return the columns and the NRSMD types of these columns that are (R)equired for this node
	 * @throws VIP2PException
	 * @throws VIP2PException
	 */
	public static HashMap<Integer,TupleMetadataType> getNRSMDReqCols(QueryTreeNode node) throws VIP2PException, VIP2PException{
		HashMap<Integer, HashMap<String, ArrayList<Integer>>> mappings = new HashMap<Integer, HashMap<String,ArrayList<Integer>>>();
		NRSMD nrsmd = getNRSMD(node,STORE_DERIVATION_COUNT, mappings);
		HashMap<Integer,TupleMetadataType> rColumnsNTypes = new HashMap<Integer, TupleMetadataType>();
		
		for(Integer i:mappings.keySet()){
			ArrayList<Integer> nodeRs = mappings.get(i).get("R");
			if(nodeRs != null){
				for(Integer s:nodeRs){
					rColumnsNTypes.put(s, nrsmd.types[s]);
				}
			}
		}
		return rColumnsNTypes;
	}
	
private static NRSMD getNRSMD(
		QueryTreeNode node,
		boolean derivationCount,
		HashMap<Integer, HashMap<String, ArrayList<Integer>>> mappings,
		ArrayList<Integer> address,
		boolean nestedInParent)
		throws VIP2PException, VIP2PException {
		//Parameters.logger.debug("*");
		ArrayList<TupleMetadataType> nTypes = new ArrayList<TupleMetadataType>();
		ArrayList<NRSMD> nNested = new ArrayList<NRSMD>();
		/*
		 * At the distributed version (when the code runs normally in a ViP2P peer)
		 * the ntuples have in front of them an extra string which is the doc id of the
		 * document from which the tuple was extracted. Because this doc id contains
		 * an uri we use Constants.URI_TYPE as its type at the NRSMDs.
		 */
		nTypes.add(TupleMetadataType.URI_TYPE);
		if(derivationCount)
			nTypes.add(TupleMetadataType.INTEGER_TYPE);
		
		//Parameters.logger.debug("\nGET ON "+ node.tag + " ADDRESS IS: ");
		//display(address);

		TupleMetadataType[] types;
		NRSMD[] nestedChildren;

		// gather information for this and all children into types and
		// nestedChildren
		enrichNRSMD(node, derivationCount, nTypes, nNested, mappings, address, nestedInParent);

		types = new TupleMetadataType[nTypes.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = nTypes.get(i);
		}
		nestedChildren = new NRSMD[nNested.size()];
		for (int i = 0; i < nestedChildren.length; i++) {
			nestedChildren[i] = (NRSMD) nNested.get(i);
		}
		
		//This is used only for the Leaf Operator, so the
		//statistic will be initialized in the class XamEstimation
		NRSMD aux = new NRSMD(types, nestedChildren);
		return aux;
	}
	

	/**
	 * Computes the NRSMD associated strictly to the current node (does not
	 * look into nested children)/
	 * 
	 * @param node
	 * @return @throws
	 *         ULoadExecutionException
	 * @throws VIP2PException
	 */
	public static NRSMD getStrictNRSMD(QueryTreeNode node) throws VIP2PException, VIP2PException {
		//Parameters.logger.debug("Making strict node RSMD for " + node.tag);
		ArrayList<TupleMetadataType> nTypes = new ArrayList<TupleMetadataType>();
		TupleMetadataType[] types;
		NRSMD[] nestedChildren;

		if (node.storesID()) {
			TupleMetadataType idType = IDTypes(IDSchemeAssignator.getIDScheme(node));
			nTypes.add(idType);
			//Parameters.logger.debug("In metadata for " + node.tag + " added ID type: " +
			//		idType);

		}
		if (node.storesTag()) {
			TupleMetadataType type = TupleMetadataType.STRING_TYPE;
			nTypes.add(type);
		}
		if (node.storesValue()) {
			TupleMetadataType type = TupleMetadataType.STRING_TYPE;
			nTypes.add(type);
		}
		if (node.storesContent()) {
			TupleMetadataType type = TupleMetadataType.STRING_TYPE;
			nTypes.add(type);
		}
		types = new TupleMetadataType[nTypes.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = nTypes.get(i);
		}
		nestedChildren = new NRSMD[0];
		
		//Not used in the estimation -> 
		return new NRSMD(types, nestedChildren);
	}
	
	private static final TupleMetadataType IDTypes(IDScheme sch) throws VIP2PException {
		if (sch instanceof fr.inria.oak.effisto.IDs.OrderedIntegerIDScheme) {
			return TupleMetadataType.UNIQUE_ID;
		}
		if (sch instanceof fr.inria.oak.effisto.IDs.PrePostDepthElementIDScheme) {
			return TupleMetadataType.STRUCTURAL_ID;
		}
		
		if (sch instanceof fr.inria.oak.effisto.IDs.PrePostElementIDScheme) {
				return TupleMetadataType.STRUCTURAL_ID;
			}
		
		if (sch instanceof fr.inria.oak.effisto.IDs.CompactDynamicDeweyScheme) {
			return TupleMetadataType.UPDATE_ID;
		}
		throw new VIP2PException(
			"Could not find metadata type for ID scheme "
				+ sch.getClass().getName());
	}
	
//	public static void main(String[] argv) throws Exception {
//		Parameters.init();
//		TreePattern p = (new fr.inria.oak.effisto.xam.XAMParserUtility()).getTreePatternFromFile(argv[0]);
//		HashMap<Integer, HashMap<String, ArrayList<Integer>>> map = new HashMap<Integer, HashMap<String, ArrayList<Integer>>>();
//		NRSMD n1 = NRSMD.getNRSMD((PatternNode) p.getRoot(), map);
//		n1.display();
//		
//		ProjMask m3 = new ProjMask();
//		m3.addChild(1);
//		m3.addChild(3);
//		//Parameters.logger.info("M3: " + m3.toString());
//			
//		ProjMask m2 = new ProjMask();
//		m2.addChild(0);
//		m2.addChild(1);
//		m2.addChild(2);
//			
//		m2.addChildMask(2, m3);
//		//Parameters.logger.info("M2: " + m2.toString());
//	
//		ProjMask m1 = new ProjMask();
//		m1.addChild(0);
//		m1.addChild(2);
//		
//		m1.addChildMask(1, m2);
//		//Parameters.logger.info("M1: " + m1.toString());
//		
//		NRSMD nProj = NRSMD.makeProjectRSMD(n1, m1);
//		
//		nProj.display();
//		
//		if (argv.length > 0){
//			return;
//		}
//				
//		int[] unnestMap = new int[2];
//		unnestMap[0] = 2;
//		unnestMap[1] = 2;
//		NRSMD n2 = NRSMD.unnestField(n1, unnestMap);
//		n2.display();
//
//		Iterator<Integer> it = map.keySet().iterator();
//		while (it.hasNext()) {
//			Integer XAMKey = it.next();
//			HashMap<String, ArrayList<Integer>> forThisNode = map.get(XAMKey);
//			if (forThisNode == null) {
//				//Parameters.logger.info("Nothing for " + XAMKey);
//			} else {
//				ArrayList<Integer> nodeAddress = forThisNode.get("Node");
//				if (nodeAddress == null) {
//					//Parameters.logger.info("No node for " + XAMKey);
//				} else {
//					//Parameters.logger.info("Node address for " + XAMKey + ":");
//					display(nodeAddress);
//				}
//
//				ArrayList<Integer> idAddress = forThisNode.get("ID");
//				if (idAddress == null) {
//					//Parameters.logger.info("No ID for " + XAMKey);
//				} else {
//					//Parameters.logger.info("ID address for " + XAMKey + ":");
//					display(idAddress);
//				}
//
//				ArrayList<Integer> tagAddress = forThisNode.get("Tag");
//				if (tagAddress == null) {
//					//Parameters.logger.info("No tag for " + XAMKey);
//				} else {
//					//Parameters.logger.info("Tag address for " + XAMKey + ":");
//					display(tagAddress);
//				}
//
//				ArrayList<Integer> valAddress = forThisNode.get("Val");
//				if (valAddress == null) {
//					//Parameters.logger.info("No val for " + XAMKey);
//				} else {
//					//Parameters.logger.info("Val address for " + XAMKey + ":");
//					display(valAddress);
//				}
//
//				ArrayList<Integer> contAddress = forThisNode.get("Cont");
//				if (contAddress == null) {
//					//Parameters.logger.info("No cont for " + XAMKey);
//				} else {
//					//Parameters.logger.info("Cont address for " + XAMKey + ":");
//					display(contAddress);
//				}
//			}
//		}
//
//	}
	
	
//	/**
//	 * Performs a first-level (simple) projection
//	 * 
//	 * @param nrsmd
//	 * @param keepColumns
//	 * @return
//	 */
//	public static NRSMD makeProject2RSMD(NRSMD nrsmd, int[] keepColumns)
//		throws VIP2PException {
//		if (!initialized){
//			initialize();
//		}
//		int[] newTypes = new int[keepColumns.length];
//		int iChildren = 0;
//		NRSMD[] childrenPositions = new NRSMD[keepColumns.length];
//	
//		for (int i = 0; i < keepColumns.length; i++) {
//			int kAux2 = keepColumns[i];
//			int kAux = nrsmd.types[kAux2];
//			newTypes[i] = kAux;
//			
//			if (newTypes[i] == Constants.TUPLE_TYPE) {
//				childrenPositions[iChildren] =
//					nrsmd.getNestedChild(keepColumns[i]);
//				iChildren++;
//			}
//		}
//		NRSMD[] children = new NRSMD[iChildren];
//		for (int i = 0; i < children.length; i++) {
//			children[i] = childrenPositions[i];
//		}
//		
//		return new NRSMD(newTypes, children);
//	}
//
//	/**
//	 * Performs a complex projection
//	 * 
//	 * @param nrsmd
//	 * @param keepColumns
//	 * @return
//	 */
//	public static NRSMD makeProject2RSMD(NRSMD nrsmd, ProjMask m)
//		throws VIP2PException {
//		if (!initialized){
//			initialize();
//		}
//		//Parameters.logger.debug("\nProjecting ");
//		//nrsmd.display();
//		//Parameters.logger.debug(" on " + m.toString());
//		int[] aux = new int[m.columns.size()];
//		for (int i = 0; i < aux.length; i ++){
//			aux[i] = ((Integer)m.columns.get(i)).intValue();
//		}
//		NRSMD res = makeProject2RSMD(nrsmd, aux);
//		//Parameters.logger.debug("Intermediary result:");
//		//res.display();
//		// now we have carved correctly at top level. Maybe we need to carve inside.
//		Iterator<Integer> it = m.keepFromChildren.keySet().iterator();
//		while (it.hasNext()){
//			Integer indexOfKeptChild = it.next();
//			int idx = indexOfKeptChild.intValue();
//			ProjMask keepForThisChild = (ProjMask)m.keepFromChildren.get(indexOfKeptChild);
//			NRSMD existingNestedNRSMD = res.getNestedChild(idx);
//			res.setNestedChild(idx, makeProject2RSMD(existingNestedNRSMD, keepForThisChild));
//		}
//		//Parameters.logger.debug("Returning:");
//		//res.display();
//		return res;
//	}

	// This method cannot be written, because: ULoad types have been diversely
	// mapped to elementary types and we may not be happy with the outcome.
	// public NRSMD(ResultSetMetaData rsmd) throws ULoadExecutionException {

	/**
	 * Performs a first-level (simple) projection
	 * 
	 * @param nrsmd
	 * @param keepColumns
	 * @return
	 */
	public static NRSMD makeProjectRSMD(NRSMD nrsmd, int[] keepColumns) throws VIP2PException {		
		/*Parameters.logger.debug("in NRSMD.makeProjectRSMD");
		Parameters.logger.debug("child.nrsmd.types.length: " + nrsmd.types.length);*/
		if(nrsmd.types.length == 0){keepColumns = new int[0];}
		
		TupleMetadataType[] newTypes = new TupleMetadataType[keepColumns.length];
		int iChildren = 0;
		NRSMD[] childrenPositions = new NRSMD[keepColumns.length];
		//Parameters.logger.debug("\nProjecting from ");
		//nrsmd.display();
		/*Parameters.logger.debug(" on ");
		for (int i = 0; i < keepColumns.length; i ++){
			Parameters.logger.debug(keepColumns[i]);
		}*/
		for (int i = 0; i < keepColumns.length; i++) {
			int kAux2 = keepColumns[i];
			TupleMetadataType kAux = nrsmd.types[kAux2];
			newTypes[i] = kAux;
			if (newTypes[i] == TupleMetadataType.TUPLE_TYPE) {
				childrenPositions[iChildren] =
					nrsmd.getNestedChild(keepColumns[i]);
				iChildren++;
			}
		}
		NRSMD[] children = new NRSMD[iChildren];
		for (int i = 0; i < children.length; i++) {
			children[i] = childrenPositions[i];
		}
		
		return new NRSMD(newTypes, children);
	}

	/**
	 * Performs a complex projection
	 * 
	 * @param nrsmd
	 * @param keepColumns
	 * @return
	 */
	/*
	public static NRSMD makeProjectRSMD(NRSMD nrsmd, ProjMask m) throws VIP2PException {
		//Parameters.logger.debug("\nProjecting ");
		//nrsmd.display();
		//Parameters.logger.debug(" on " + m.toString());
		int[] aux = new int[m.columns.size()];
		for (int i = 0; i < aux.length; i ++){
			aux[i] = ((Integer)m.columns.get(i)).intValue();
		}
		NRSMD res = makeProjectRSMD(nrsmd, aux);
		//Parameters.logger.debug("Intermediary result:");
		//res.display();
		// now we have carved correctly at top level. Maybe we need to carve inside.
		Iterator<Integer> it = m.keepFromChildren.keySet().iterator();
		while (it.hasNext()){
			Integer indexOfKeptChild = (Integer)it.next();
			int idx = indexOfKeptChild.intValue();
			ProjMask keepForThisChild = (ProjMask)m.keepFromChildren.get(indexOfKeptChild);
			NRSMD existingNestedNRSMD = res.getNestedChild(idx);
			res.setNestedChild(idx, makeProjectRSMD(existingNestedNRSMD, keepForThisChild));
		}
		//Parameters.logger.debug("Returning:");
		//res.display();
		return res;
	}
	*/
	public static NRSMD makeRequiredNRSMD(QueryTreeNode pn)
		throws VIP2PException {

		return requiredNRSMD(pn,0);
	}
	
	/**
	 * @param nrsmd
	 * @param vector
	 * @param vector2
	 * @return
	 */
	public static NRSMD nestNRSMD(NRSMD nrsmd, ArrayList<Integer> groupByColumns, ArrayList<Integer> groupedColumns) throws VIP2PException {
		TupleMetadataType[] newTypes = new TupleMetadataType[nrsmd.colNo - groupedColumns.size() + 1];
		int nestedChildrenNo = 1;
		
		int[] inNestedGroupMask = new int[groupedColumns.size()];
		for (int i = 0; i < inNestedGroupMask.length; i ++){
			inNestedGroupMask[i] = groupedColumns.get(i).intValue();
			//Parameters.logger.debug("NRSMD.nest: grouped column " + inNestedGroupMask[i]);
		}
		NRSMD nestedGroupNRSMD = NRSMD.makeProjectRSMD(nrsmd, inNestedGroupMask);
		
		// there is going to be at least the newly nested child
		for (int i = 0; i < nrsmd.colNo; i ++){
			if (nrsmd.types[i] == TupleMetadataType.TUPLE_TYPE){
				if (groupedColumns.indexOf(new Integer(i)) == -1){
					nestedChildrenNo ++;
				}
			}
		}
		
		//Parameters.logger.debug("There will be " + nestedChildrenNo + " nested children");
		NRSMD[] newChildren = new NRSMD[nestedChildrenNo];
		
		return new NRSMD(newTypes, newChildren);
	}

	/**
	 * @param nrsmd
	 * @param is
	 * @param vector
	 * @param vector2
	 * @return
	 */
	public static NRSMD nestNRSMD(NRSMD nrsmd, int[] ancPath, ArrayList<Integer> groupByColumns, ArrayList<Integer> groupedColumns) throws VIP2PException {
		return recNestNRSMD(nrsmd, ancPath, groupByColumns, groupedColumns, 0);
	}

	/**
	 * @param nrsmd
	 * @param is
	 * @param groupByColumns
	 * @param groupedColumns
	 * @param i
	 * @return
	 */
	private static NRSMD recNestNRSMD(NRSMD nrsmd, int[] ancPath, ArrayList<Integer> groupByColumns, ArrayList<Integer> groupedColumns, int from) throws VIP2PException {
//		//Parameters.logger.info("RecNestNRSMD at position " + from);
//		nrsmd.display();
		if (from == ancPath.length){
			NRSMD aux = NRSMD.nestNRSMD(nrsmd, groupByColumns, groupedColumns);
//			//Parameters.logger.info("Obtained: ");
//			aux.display();
			return aux;
		}
		else{
			NRSMD thisChild = nrsmd.getNestedChild(ancPath[from]);
			NRSMD thisChildNested = recNestNRSMD(thisChild, ancPath, groupByColumns, groupedColumns, from + 1);
			
			// must find the index in nrsmd's nested children of thisChild
			int thisChildIndex = -1;
			int iAux = 0;
			for (int i = 0; i < nrsmd.types.length; i ++){
				if (nrsmd.types[i] == TupleMetadataType.TUPLE_TYPE){
					if (i == ancPath[from]){
						thisChildIndex = iAux;
						break;
					}
					iAux ++;
				}
			}
				
			TupleMetadataType[] types = new TupleMetadataType[nrsmd.types.length];
			for (int i = 0; i < nrsmd.types.length; i ++){
				types[i] = nrsmd.types[i];
			}
			NRSMD[] children = new NRSMD[nrsmd.nestedChildren.length];
			for (int i = 0; i < children.length; i ++){
				if (i == thisChildIndex){
					children[i] =  thisChildNested;
				}
				else{
					children[i] = nrsmd.nestedChildren[i];
				}
			}			
			return new NRSMD(types, children);
		}
	}
	/**
	 * @param nrsmd
	 * @param ancPath
	 * @param i
	 * @return
	 */
	private static NRSMD recUnnestField(NRSMD nrsmd, int[] ancPath, int from)
		throws VIP2PException {
		//Parameters.logger.debug("\nREC-UNNEST-FIELD FROM " + from);
		//nrsmd.display();
		if (from == ancPath.length - 1) {
			NRSMD res = unnestField(nrsmd, ancPath[from]);
			//Parameters.logger.debug("RETURNING 1:");
			//res.display();
			return res;
		} else {
			NRSMD child = nrsmd.getNestedChild(ancPath[from]);
			//Parameters.logger.debug("AT " + ancPath[from]  + " FOUND NESTED CHILD:");
			//child.display();
			NRSMD childResult = recUnnestField(child, ancPath, from + 1);
			//Parameters.logger.debug("UNNESTED CHILD: ");
			//childResult.display();
			NRSMD res = new NRSMD(nrsmd.types, nrsmd.nestedChildren);
			//Parameters.logger.debug("PRELIMINARY RESULT: ");
			//res.display();
			res.setNestedChild(ancPath[from], childResult);
			//Parameters.logger.debug("RESULT: ");
			//res.display();
			return res;
		}
	}

	/**
	 * Constructs the NRSMD of only the required fields (and includes hierarchy
	 * needed in order to have those required fields).
	 * 
	 * @param pn:
	 *            Xam Node
	 * @return @throws
	 *         ULoadExecutionException
	 */
	private  static NRSMD requiredNRSMD(QueryTreeNode pn,int nodeCount)
		throws VIP2PException {
		//Parameters.logger.debug("\nMaking required RSMD for " + pn.tag);

		NRSMD res = null;

		int iString = 0;
		int iID = 0;
		int iNested = 0;

		if (pn.requiresID()) {
			//Parameters.logger.debug(pn.tag + " requires ID !");
			iID++;
		} else {
			//Parameters.logger.debug(pn.tag + " does not require ID !");
		}
		if (pn.requiresTag()) {
			iString++;
		}
		if (pn.requiresVal()) {
			//Parameters.logger.debug(pn.tag + " requires value !");
			iString++;
		} else {
			//Parameters.logger.debug(pn.tag + " does not require value !");
		}

		boolean noMore = false;
		if (pn.getEdges() == null) {
			noMore = true;
		}
		if (pn.getEdges().size() == 0) {
			noMore = true;
		}

		int newColNo = iString + iID + iNested;
		TupleMetadataType[] newTypes = new TupleMetadataType[newColNo];
		String[] newNames = new String[newColNo];

		// Collect whatever we had so far.
		if (iString + iID + iNested > 0) {
			//Parameters.logger.debug("Collecting fields !");
			int j = 0;
			if (iID > 0) {
				if (pn.isIdentityIDType()) {
					newTypes[j] = TupleMetadataType.UNIQUE_ID;
					newNames[j] = "id" + nodeCount;
				} else {
					if (pn.isOrderIDType()) {
						newTypes[j] = TupleMetadataType.ORDERED_ID;
						newNames[j] = "id" + nodeCount;
					} else {
						if (pn.isStructIDType()) {
							newTypes[j] = TupleMetadataType.STRUCTURAL_ID;
							newNames[j] = "id" + nodeCount;
						} else {
							if (pn.isUpdateIDType()) {
								newTypes[j] = TupleMetadataType.UPDATE_ID;
								newNames[j] = "id" + nodeCount;
							}
						}
					}
				}
				//Parameters.logger.debug( "Collected an ID type at "+ j + " in required metadata of " + pn.tag);
				j++;
			}
			if (iString > 0) {
				if (pn.requiresTag()) {
					newTypes[j] = TupleMetadataType.STRING_TYPE;
					newNames[j] = "tag" + nodeCount;
					j++;
				}
				if (pn.requiresVal()) {
					newTypes[j] = TupleMetadataType.STRING_TYPE;
					newNames[j] = "val" + nodeCount;
					//Parameters.logger.debug(pn.tag + " requires value ! String field at " + j);
				} else {
					//Parameters.logger.debug(pn.tag + " does not, after all, require value");
				}
			}
		}

		if (noMore) {
			NRSMD[] nestedChildren = new NRSMD[0];
			//Parameters.logger.debug("No children, returning new RSMD");
			NRSMD vn = new NRSMD(newColNo, newTypes, newNames, nestedChildren);
			//vn.display();
			//Parameters.logger.debug("End of required RSMD for " + pn.tag + " 1\n");
			return vn;
		} else {
			res = new NRSMD(newColNo, newTypes, newNames, new NRSMD[iNested]);
		}

		// this node has children; collect also what is required for them
		//Parameters.logger.debug("Prior to examining children of " + pn.tag + " we had:");
		//res.display();
		//Parameters.logger.debug("Looking at children of " + pn.tag);
		for(QueryTreeEdge pe: pn.getEdges()) {
			QueryTreeNode n2 = pe.n2;
			if (n2.requiresSomething()) {
				//Parameters.logger.debug(	"Gathering required metadata from " + pn.tag + ". Looking "+ " at "+ n2.tag+ " which also needs some fields.");
				nodeCount++;
				NRSMD childRequiredNRSMD = requiredNRSMD(n2,nodeCount);
				//Parameters.logger.debug("Child metadata for " + n2.tag + " is: ");
				if (childRequiredNRSMD == null) {
					//Parameters.logger.debug("null");
				} else {
					//childRequiredNRSMD.display();
				}
				if (childRequiredNRSMD != null) {
					if (res == null) {
						// this is the first added thing
						if (pe.isNested()) {
							res =
								NRSMD.addNestedField(
									NRSMD.emptyNRSMD(),
									childRequiredNRSMD);
						} else {
							res = childRequiredNRSMD;
						}
						//Parameters.logger.debug("Copied RSMD from child " + n2.tag);
					} else {
						// there was something else
						if (pe.isNested()) {
							// nest this NRSMD in the parent one, and
							// collect it
							res = NRSMD.addNestedField(res, childRequiredNRSMD);
							iNested++;
							//	Parameters.logger.info("Non-null required RSMD for nested child "+ n2.tag);
						} else {
							// just do a cartesian product metadata
							res = NRSMD.appendNRSMD(res, childRequiredNRSMD);
							//	Parameters.logger.info("Null required RSMD for unnested child "+ n2.tag);
						}
					}
				} else { // if this child's required NRSMD is null, ignore it nothing
				}
			}
		}
		//Parameters.logger.debug("Returning: ");
		//res.display();
		//Parameters.logger.debug("End of requiredRSMD for " + pn.tag + " 2\n");
		return res;
	}
	
	/**
	 * <p>This pickles an NRSMD into the given DataOutputStream. It only saves the
	 * column types. The format is:</p>
	 * 
	 * <ul>
	 * <li>Int <i>colNo</i></li>
	 * <li><i>colNo</i> times: <ul>
	 * <li>Byte <i>colType</i></li>
	 * </ul></li>
	 * </ul>
	 * 
	 * TODO: output nested NRSMDs.
	 * 
	 * @param nrsmd the NRSMD to pickle
	 * @param out the DataOutputStream to write to
	 * @throws IOException from the DataOutputStream's output methods
	 */
	public static void toDataOutput(NRSMD nrsmd, DataOutputStream out) throws IOException {
		out.writeInt(nrsmd.colNo);
		for ( int i = 0; i < nrsmd.colNo; i++ ) {
			// Assumption: Type constants are less than 255
			out.writeUTF(nrsmd.types[i].toString());
		}
	}
	
	/**
	 * This method unnests the field fld. It replaces the field fld with all
	 * the fields of the nested tuples appearing there.
	 */
	public static NRSMD unnestField(NRSMD nrsmd, int fld) throws VIP2PException {
		//Parameters.logger.debug("\nUNNEST FIELD " + fld + " FROM");
		//nrsmd.display();

		NRSMD childNRSMD = nrsmd.getNestedChild(fld);

		TupleMetadataType[] auxTypes = new TupleMetadataType[nrsmd.colNo - 1 + childNRSMD.colNo];
		NRSMD[] auxNRSMDs = new NRSMD[nrsmd.nestedNo - 1 + childNRSMD.nestedNo];
	
		int iAuxTypes = 0;
		int iAuxNRSMDs = 0;

		// copying the first fields, before the unnesting column
		for (int i = 0; i < fld; i++) {
			auxTypes[iAuxTypes] = nrsmd.types[i];
			iAuxTypes++;
			if (nrsmd.types[i] == TupleMetadataType.TUPLE_TYPE) {
				auxNRSMDs[iAuxNRSMDs] = nrsmd.nestedChildren[iAuxNRSMDs];
				iAuxNRSMDs++;
			} 
		}
		// now copy all fields from the freshly unnested child
		int jChildNestedNRSMD = 0;
		for (int j = 0; j < childNRSMD.colNo; j++) {
			auxTypes[iAuxTypes] = childNRSMD.types[j];
			iAuxTypes++;
			if (childNRSMD.types[j] == TupleMetadataType.TUPLE_TYPE) {
				auxNRSMDs[iAuxNRSMDs] =
					childNRSMD.nestedChildren[jChildNestedNRSMD];
				iAuxNRSMDs++;//????
				jChildNestedNRSMD++;
			} 
		}
		
		// now copy the remaining fields
		for (int i = fld + 1; i < nrsmd.colNo; i++) {
			auxTypes[iAuxTypes] = nrsmd.types[i];
			iAuxTypes++;
			if (nrsmd.types[i] == TupleMetadataType.TUPLE_TYPE) {
				auxNRSMDs[iAuxNRSMDs] = nrsmd.nestedChildren[iAuxNRSMDs + 1];
				iAuxNRSMDs++;
			} 
		}

		NRSMD auxNRSMD = new NRSMD(auxTypes, auxNRSMDs);
		//auxNRSMD.display();
		return auxNRSMD;

	}

	public static NRSMD unnestField(NRSMD nrsmd, int[] ancPath) throws VIP2PException {
		return recUnnestField(nrsmd, ancPath, 0);
	}

}
