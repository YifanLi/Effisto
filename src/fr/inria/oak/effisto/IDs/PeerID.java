package fr.inria.oak.effisto.IDs;
import java.io.Serializable;

/**
 * This interface represents a network peer at the logical level.
 * It allows labeling a logical operator with an execution peer,
 * and understanding what is.
 *   
 * @author Ioana MANOLESCU
 *
 */
public abstract class PeerID implements Serializable, Comparable<PeerID> {
	
	private static final long serialVersionUID = -5859711954851848968L;

	public String peerURI;
	public int pastryPort;
	public int rmiPort;
	
	
	public String toString() {
		return new String(this.peerURI + ":" + this.pastryPort + "," + this.rmiPort);
	}
	
	public boolean equals(Object givenObj) {
		if(!(givenObj instanceof PeerID)) {
			return false;
		}
		
		PeerID givenPID = (PeerID) givenObj;

		if (this.pastryPort != givenPID.pastryPort) {
			return false;
		}
		
		if (this.rmiPort != givenPID.rmiPort) {
			return false;
		}
		
		if (!this.peerURI.equals(givenPID.peerURI)) {
			return false;
		}

		
		return true;
	}
	
	public int hashCode() {
		Long hc = new Long(this.peerURI.hashCode());
		hc += this.pastryPort;
		hc = hc % Integer.MAX_VALUE;
		hc += this.rmiPort;
		hc = hc % Integer.MAX_VALUE;
		
		return hc.intValue();
	}

	public int compareTo(PeerID o) {
		int peerURITest = this.peerURI.compareTo(o.peerURI);
		if (peerURITest == 0) {
			if (this.pastryPort == o.pastryPort) {
				if (this.rmiPort == o.rmiPort) {
					return 0;
				} else if(this.rmiPort < o.rmiPort) {
					return -1;
				} else {
					return 1;
				}
			} else if (this.pastryPort < o.pastryPort) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return peerURITest;
		}
	}

}