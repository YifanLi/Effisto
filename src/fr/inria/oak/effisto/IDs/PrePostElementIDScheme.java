package fr.inria.oak.effisto.IDs;
import java.util.Stack;

/**
 * Pre-post element ID scheme.
 * 
 * @author Alin TILEA
 */
public class PrePostElementIDScheme implements IDScheme {

	int currentPre;
	int currentPost;
	
	Stack<PrePostElementID> s;
	
	PrePostElementID currentID;
	
	static String nullIDStringImage = "null null null";
	

	public PrePostElementIDScheme(){
		currentPre = 0;
		currentPost = 0;
		currentID = null;
		s = new Stack<PrePostElementID>();
	}
	
	public boolean isOrderPreserving() {
		return true;
	}

	public boolean isParentAncestorPreserving() {
		return true;
	}

	public boolean allowsParentNavigation() {
		return false;
	}

	public boolean allowsUpdates() {
		return false;
	}

	public void beginDocument() {
		currentPre = 0;
		currentPost = 0;
	}

	public void beginNode() {
		currentID = new PrePostElementID(currentPre);
		currentPre ++;
		s.push(currentID);
		//Parameters.logger.debug("Assigned and pushed " + currentID.toString());
	}

	public void beginNode(String tag) {
		// TODO Auto-generated method stub

	}
	
	public void endNode() {
		currentID = s.pop();
		currentID.setPost(currentPost);
		//Parameters.logger.debug("Completed and popped " + currentID.toString());
		currentPost ++;
	}

	public void endDocument() {
		// nothing
	}

	public ElementID getLastID() {
		//Parameters.logger.debug("Current ID: " + currentID.toString());
		return currentID;
	}
	
	public String getSignature(String suffix){
		return ("ID" + suffix + "Pre int, ID" + suffix + "Post int, ID");
	}
	
	public  String nullIDStringImage(){
		return nullIDStringImage;
	}


	/* (non-Javadoc)
	 * @see fr.inria.gemo.uload.IDs.IDScheme#getIndexSignature(java.lang.String)
	 */
	public String getIndexSignature(String suffix) {
		return ("ID" + suffix + "Pre, ID" + suffix + "Post, ID");
	}
}