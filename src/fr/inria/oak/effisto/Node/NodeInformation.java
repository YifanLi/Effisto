package fr.inria.oak.effisto.Node;

import java.util.List;
import java.util.Set;

import fr.inria.oak.effisto.IDs.*;



/**
 * In this class we will store information related to each node. Note that we
 * can have many nodes in each virtual machine.
 * 
 * @author Spyros ZOUPANOS
 * @author Stamatis ZAMPETAKIS
 * Class UPDATE to hold info about the arrival of schemas
 */

/*
 * Notice:
 * Most content of this class has been removed.
 */
public class NodeInformation{
	public static PeerID localPID;
	public static PeerID bootstrapPID;

	/**
	 * Here we store all the peers that we know that exist in
	 * our system. Bootstrap is informed by the joining peers
	 * directly and simple peers are informed by the bootstrap.
	 */
	private static Set<PeerID> allAvailPeerIDs;
	
	/*
	 * The following 2 variables are interesting and have values 
	 * only at the bootstrap peer.
	 */
	

	
	/**
	 * The lock for the allAvailNodeIDs, newArrivedPeers and oldArrivedPeers sets.
	 */
	private static Object peerSetLock;

	public static String baseDir;
	
	public static String summaryDirName;
	public static String berkeleyDBDirName;
	public static String logicalPlanDefDirName;
	public static String physicalPlanDefDirName;

	/**
	 * used for generating the logs with the same timestamp in their name
	 * 
	 */
	public static String nodeStartTime = null;

	public List<String> indexingStrat;
	public static String lookUpStrat;
	
	//public static PhysicalPlanMonitor localPhysicalPlanMonitor;
	
	/**
	 * will be used to count the number of nodes that are extracting data or
	 * sending these data to the appropriate peers at the moment.
	 */
	private int extractDataNodesNumber;
	private Object extractDataNodesNumberLock;
	
	/**
	 * The following counter is used to count the number of peers that are
	 * storing data at their BDBs.
	 */
	private int storeDataPeersNumber;

	/**
	 * We may have many rounds of extracting data from a document and storing
	 * these data in the database. When we get a notification that all of the
	 * peers have extracted and sent the needed data, a new storeDataRound
	 * begins. Rounds are numbered and completion messages (when a peer finishes
	 * storing data in its BDBs) are numbered in order not to mix messages of
	 * different rounds.
	 */
	private int storeDataRound;
	
	/**
	 * The lock of storeDataPeersNumber and storeDataRound.
	 */
	private Object storeDataPeersNumberLock;
	
	/**
	 * attribute uses to keep the state of the ring relative to the 
	 * view materialization action on all the peers
	 */
	private byte matStatus;
	private Object matStatusLock;
	
	/**
	 * store the timestamp when the first ViP2P peer started to materialize views
	 */
	private long ringStartMat;
	
	/**
	 * store the timestamp when the last ViP2P peer finished materializing views
	 */
	private long ringStopMat;
	
	/**
	 * The number of peers that are indexing views in the DHT.
	 */
	private int dhtIndexingPeers;
	private Object dhtIndexingPeersLock;
	
	/*
	 * Catalog for this node.
	 */
	//private DistributedCatalog catalog;
	
	/*
	 * The storage context for this node
	 */
	//private StorageContext storageContext;
	
	public int getDhtIndexingPeers(){
		return dhtIndexingPeers;
	}
}