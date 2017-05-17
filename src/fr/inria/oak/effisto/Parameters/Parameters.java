package fr.inria.oak.effisto.Parameters;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import fr.inria.oak.effisto.Exception.*;
import fr.inria.oak.effisto.Logger.*;
import fr.inria.oak.effisto.Node.*;

/**
 * This file loads and saves the ViP2P parameters. When we want to know the
 * value of a property in ViP2P, we should access it through this class.
 * 
 * @author Alin TILEA
 * @author Asterios KATSIFODIMOS
 */
public class Parameters {

	private static final String CONFIG_FILE = "ViP2P.conf";

	private static boolean initialized = false;
	private static PropertiesConfiguration propertiesConfiguration;
	private static Map<String, String> parameters = new HashMap<String, String>(); // This is used to hold all the parameters and their respective values.

	
	/**
	 * The logger for a ViP2P peer, ready to be used.
	 */
	public static VIP2PLogger logger = VIP2PLoggerFactory.getLogger("simple");

	
	/**
	 * Method that initializes the parameters using <ViP2P home>/ViP2P.conf.
	 * 
	 * @throws VIP2PException
	 */
	public static void init() throws VIP2PException {
		init(CONFIG_FILE);
	}

	/**
	 * To be used in the JUnit tests (if parameters were previously initialized, this method does not
	 * throw an exception, but will call {@link #destroy()}).
	 * 
	 * @param vip2pConfigFile absolute path to the file containing all the ViP2P parameters to use
	 * @param destroyIfInitialized boolean flag to trigger destroy if parameters were previously initialized 
	 * @throws VIP2PException
	 */
	public static void init(String vip2pConfigFile, boolean destroyIfInitialized) throws VIP2PException {
		if (destroyIfInitialized && initialized){			
			destroy();
		}
		init(vip2pConfigFile);
	}
	
	/**
	 * Method that initializes the parameters given the path of the ViP2P.conf file that contains
	 * them.
	 * 
	 * @param vip2pConfigFile absolute path to the file containing all the ViP2P parameters to use
	 * @throws VIP2PException
	 */
	public static void init(String vip2pConfigFile) throws VIP2PException {
		assert (!initialized) : "Do not re-initialize parameters. Use destroy first";
		
		File f = new File(vip2pConfigFile);
		if (! f.exists() || ! f.isFile()) {
			logger.error("The file " + f.getAbsolutePath() + " was not found. Please make sure to have this file in the deploy location.");
			logger.error("Will shutdown now.");
			System.exit(0);
		}	
		
		try {
			propertiesConfiguration = new PropertiesConfiguration(vip2pConfigFile);
		} catch (ConfigurationException e) {
			logger.error("ConfigurationException while reading " + vip2pConfigFile + ": " + e.getMessage());
			logger.error("Will shutdown now.");
			System.exit(0);
		}

		Iterator<String> keysIterator = propertiesConfiguration.getKeys();
		while(keysIterator.hasNext()){
			String key = keysIterator.next();
			parameters.put(key.toUpperCase(), propertiesConfiguration.getProperty(key).toString());
		}

		if ( !Parameters.getProperty("logMode").equals("simple") ){
			// factory call in order to get proper logger object
			logger.info("Updating logger to match choice from *" + vip2pConfigFile + "*: ");
			logger = VIP2PLoggerFactory.getLogger((String) Parameters.getProperty("logMode"));
		}
		
		initialized = true;
	}

	/**
	 * If the parameters were initialized, one can use this method to reinitialize
	 * the parameters (e.g if he/she wishes to change the file form which it reads
	 * the parameter values).
	 * 
	 * @throws VIP2PException
	 */
	public static void destroy() throws VIP2PException {
		assert (initialized != false && propertiesConfiguration != null) : "Parameters not initialized!";
		
		initialized = false;
		parameters = new HashMap<String, String>();
		propertiesConfiguration = null;
	}

	/**
	 * Method that will get a property defined for ViP2P given its name.
	 * 
	 * @param  propName	the name of the property to get 
	 * @return the value of the requested parameter if it was found as a String, or null if not.
	 * @throws VIP2PException
	 */
	public static String getProperty(String propName) {
		String propValue = parameters.get(propName.toUpperCase());
		
		if (propValue == null)
			logger.error("Attention, property "+propName+" is not defined in ViP2P.conf!");
		 
		return propValue;
	}

	/**
	 * Sets the property value. If the property is not found, it is added in the 
	 * properties instance and also in the configuration file.
	 * 
	 * @param  propName the name of the property to get
	 * @param  propValue the value of the property to set
	 * @throws VIP2PException
	 */
	public static synchronized void setProperty(String propName, String propValue) throws VIP2PException{
		if (initialized && getProperty(propName.toUpperCase())==null){
			propertiesConfiguration.addProperty(propName.toUpperCase(), propValue);
		} else if (initialized) {
			propertiesConfiguration.setProperty(propName.toUpperCase(), propValue);
		}
		parameters.put(propName.toUpperCase(), propValue);
	}

	/**
	 * Gets the iterator over the keys contained in the ViP2P properties.
	 * 
	 * @return the iterator over a list of String objects
	 */
	public static Iterator<String> getKeys() {
		return propertiesConfiguration.getKeys();
	}
	
	/**
	 * Saves the parameters into <ViP2P home>/ViP2P.conf.
	 */
	public static void save(){
		save(Parameters.CONFIG_FILE);
	}
	
	/**
	 * Saves the parameters into the specified configuration file.
	 * 
	 * @param  filename the name of the file in which the properties will be saved
	 */
	public static void save(String filename){
		try {
			propertiesConfiguration.save(filename);
		} catch (ConfigurationException e) {
			Parameters.logger.error(e.toString());
		}
	}

	/**
	 * Returns the number of parameters that have been defined for ViP2P.
	 * 
	 * @return the number of parameters
	 * @throws VIP2PException
	 */
	public static int getSize() throws VIP2PException {
		if (parameters == null) {
			throw new VIP2PException("Properties are null. Are you sure you initialized them before calling me?");
		}
		return parameters.size();
	}

}