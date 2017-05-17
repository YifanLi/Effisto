package fr.inria.oak.effisto.Exception;

public class InvalidArgumentException extends Exception{
	/**
	 * the general invalid argument exception for Effisto
	 */
	private static final long serialVersionUID = 1L;

	public InvalidArgumentException(String message){
	     super(message);
	  }

}
