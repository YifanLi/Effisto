package fr.inria.oak.effisto.IDs;
import java.util.Stack;

/**
 * Pre-post-depth element ID scheme.
 *
 * @author Ioana MANOLESCU
 *
 * @created 13/06/2005
 */
public class PrePostDepthElementIDScheme implements IDScheme {

	int currentPre;
	int currentPost;
	int currentDepth;

	static boolean initialized;
	
	Stack<PrePostDepthElementID> s;
	
	PrePostDepthElementID currentID;
	
	private static String nullIDStringImage = "null null null";

	public PrePostDepthElementIDScheme(){
		currentPre = 0;
		currentPost = 0;
		currentDepth = 0;
		currentID = null;
		s = new Stack<PrePostDepthElementID>();
	}
	
	
	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#isOrderPreserving()
	 */
	public boolean isOrderPreserving() {
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#isParentAncestorPreserving()
	 */
	public boolean isParentAncestorPreserving() {
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#allowsParentNavigation()
	 */
	public boolean allowsParentNavigation() {
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#allowsUpdates()
	 */
	public boolean allowsUpdates() {
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#initDocument()
	 */
	public void beginDocument() {
		currentPre = 0;
		currentPost = 0;
		currentDepth = 0;
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#initElement()
	 */
	public void beginNode() {
		currentID = (PrePostDepthElementID)ElementIDFactory.getElementID(currentPre, currentDepth);
		currentPre ++;
		currentDepth ++;
		s.push(currentID);
		//logger.debug("Assigned and pushed " + currentID.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see fr.inria.gemo.vip2p.IDs.IDScheme#beginNode(java.lang.String)
	 */
	public void beginNode(String tag) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#endElement()
	 */
	public void endNode() {
		currentID = (PrePostDepthElementID)(s.pop());
		currentID.setPost(currentPost);
		//logger.debug("Completed and popped " + currentID.toString());
		currentPost ++;
		currentDepth --;
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#endDocument()
	 */
	public void endDocument() {
		// nothing
	}

	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#getLastID()
	 */
	public ElementID getLastID() {
		//logger.debug("Current ID: " + currentID.toString());
		return currentID;
	}
	
	public String getSignature(String suffix){
		return ("ID" + suffix + "Pre int, ID" + suffix + "Post int, ID" + suffix + "depth int");
	}
	
	public  String nullIDStringImage(){
		return nullIDStringImage;
	}


	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#getIndexSignature(java.lang.String)
	 */
	public String getIndexSignature(String suffix) {
		return ("ID" + suffix + "Pre, ID" + suffix + "Post, ID" + suffix + "depth");
	}
}