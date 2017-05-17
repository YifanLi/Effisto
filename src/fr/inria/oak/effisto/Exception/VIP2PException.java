package fr.inria.oak.effisto.Exception;


/**
 * Class that defines a ViP2P Exception. ViP2P Exceptions will be thrown
 * when ViP2P is supposed to crash.
 * (Also used in Effisto.)
 * 
 * @author Ioana MANOLESCU
 * @created 13/06/2005
 */
public class VIP2PException extends Exception {

	private static final long serialVersionUID = -3819688241961585279L;

	public VIP2PException(String s){
		super(s);
	}

}

