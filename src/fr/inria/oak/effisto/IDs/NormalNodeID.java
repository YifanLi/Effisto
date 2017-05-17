package fr.inria.oak.effisto.IDs;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import fr.inria.oak.effisto.Node.*;

/**
 * 
 * @author Ioana MANOLESCU
 *
 */
public class NormalNodeID extends PeerID {

	private static final long serialVersionUID = -4210315558924397685L;

	/* Constants */
	private static final String RANDOM_SERVER_URI = "www.google.com";
	private static final int RANDOM_PORT = 6666;
	
	/**
	 * A dummy constructor that just gives a xam number and
	 * lets the other fields (peerURI & port) unassigned for
	 * later assignment.
	 */
	public NormalNodeID() {
		if (NodeInformation.localPID == null) {
			this.peerURI = "initialisePeer!";
			this.pastryPort = -1;
			this.rmiPort = -1;
		} else {
			this.peerURI = NodeInformation.localPID.peerURI;
			this.pastryPort = NodeInformation.localPID.pastryPort;
			this.rmiPort = NodeInformation.localPID.rmiPort;
		}
	}
	
	public NormalNodeID(int givenPastryPort, int givenRmiPort) throws SocketException, UnknownHostException {
		this.peerURI = getLocalHost().getHostAddress();
		this.pastryPort = givenPastryPort;
		this.rmiPort = givenRmiPort;
	}

	public NormalNodeID(String givenPeerURI, int givenPastryPort, int givenRmiPort) {
		this.peerURI = givenPeerURI;
		this.pastryPort = givenPastryPort;
		this.rmiPort = givenRmiPort;
	}

	public NormalNodeID(PeerID givPeerID) {
		this.peerURI = givPeerID.peerURI;
		this.pastryPort = givPeerID.pastryPort;
		this.rmiPort = givPeerID.rmiPort;
	}

	public NormalNodeID(String givNodeIdStr) {
		int uriEnd = givNodeIdStr.indexOf(":");
		this.peerURI = givNodeIdStr.substring(0, uriEnd);
		int pastryPortEnd = givNodeIdStr.indexOf(",");
		this.pastryPort = Integer.parseInt(givNodeIdStr.substring(uriEnd + 1, pastryPortEnd));
		this.rmiPort = Integer.parseInt(givNodeIdStr.substring(pastryPortEnd + 1));
	}

	private InetAddress getLocalHost() throws SocketException, UnknownHostException {
		InetAddress srvIna = InetAddress.getByName(RANDOM_SERVER_URI);
		DatagramSocket sock = new DatagramSocket(RANDOM_PORT);
		sock.connect(srvIna, RANDOM_PORT);
		InetAddress addr = sock.getLocalAddress();
		sock.close();
		return addr;
	}
}