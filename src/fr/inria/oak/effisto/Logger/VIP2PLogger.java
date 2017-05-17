package fr.inria.oak.effisto.Logger;
/**
 * Interface declaring all the logging methods that a ViP2P logger
 * should implement.
 *
 * @author Alin TILEA
 */
public interface VIP2PLogger {
	
	public void debug(Object obj);

	public void debug(Object obj, Throwable thr); 

	public void error(Object obj);

	public void error(Object obj, Throwable thr);

	public void fatal(Object obj);

	public void fatal(Object obj, Throwable thr);

	public void info(Object obj);

	public void info(Object obj, Throwable thr);

	public void warn(Object obj);

	public void warn(Object obj, Throwable thr);
	
	public void setLevel(int i);
}