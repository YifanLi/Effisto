package fr.inria.oak.effisto.Query.DataExtractor;

import fr.inria.oak.effisto.Query.TreePattern.QueryTreePattern.QueryTreePattern;

public class Match {
	
	private boolean isMatched;
	private DataObject ContentOfTargetNode;
	
	public boolean checkMatched(){
		return this.isMatched;
	}
	
	public Match(QueryTreePattern qtp, DataObject DataObj){
		
		//
		//
		
		this.ContentOfTargetNode = null;
		
	}
	

}
