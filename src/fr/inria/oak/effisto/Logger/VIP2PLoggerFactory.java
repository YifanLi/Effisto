package fr.inria.oak.effisto.Logger;
import java.io.File;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import fr.inria.oak.effisto.Parameters.*;
import fr.inria.oak.effisto.IDs.*;
import fr.inria.oak.effisto.Node.*;

/**
 * Factory that based on *logMode* parameter's value will create a {@link SimpleLogger} or a wrapping
 * over the Log4j logger with {@link Log4jLogger}.
 * 
 * @author Alin TILEA
 */
public class VIP2PLoggerFactory {

	public static VIP2PLogger getLogger(){
		return getLogger(Parameters.getProperty("logMode"));
	}
	
	public static VIP2PLogger getLogger(String logMode){
		VIP2PLogger logger = null;

		if(logMode.equalsIgnoreCase("simple")){
			System.out.println("Running in SIMPLE logging mode. ");
			/*
			 * the below comment-out is invalid for VIP2P !!!
			 */
			//logger = (VIP2PLogger) SimpleLogger.getLogger();
		}else{				
			if(logMode.equalsIgnoreCase("log4j") ){
				System.out.println("Running in Log4J logging mode. ");
				/*
				 * the below comment-out is invalid for VIP2P !!!
				 */
				//logger = new Log4jLogger("VIP2PLoggerFactory");
				assert (NodeInformation.localPID != null) : "To use Log4J logger, you need to run in peer network mode.\nSee startManyNodes.sh for this mode.";
				
				if(NodeInformation.localPID == null){
					NodeInformation.localPID = new NormalNodeID();
				}
				
				String completePeerName = NodeInformation.localPID.toString();
				if (completePeerName != null){
					makeLogPerFile(logger, completePeerName);
				}
			}else{
				System.out.println("WARNING : Runtime parameter *logMode* has an unrecognized value: *"+logMode+"*");	
			}
		}

		return logger;	    		
	}

	/**
	 * 
	 * @param logger - the logger that needs to log to different files
	 * @param completePeerName - output file of the log
	 */
	private static void makeLogPerFile(VIP2PLogger logger, String completePeerName){
		Date projDate = new Date(System.currentTimeMillis());
		StringBuffer dateStr = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		dateStr = sdf.format(projDate, dateStr, new FieldPosition(0));
		NodeInformation.nodeStartTime = dateStr.toString();

		String logFileName = "logs" + File.separator + completePeerName + ".log";

		Logger rootLogger = Logger.getRootLogger();
		Enumeration<Object> appenders = rootLogger.getAllAppenders();
		FileAppender fa = null;
		while (appenders.hasMoreElements()) {
			Appender currAppender = (Appender) appenders.nextElement();
			if (currAppender instanceof FileAppender) {
				fa = (FileAppender) currAppender;
			}
		}
		if (fa != null) {
			System.out.println(completePeerName + ": This instance's logs are here :" + logFileName);
			fa.setFile(logFileName);
			fa.activateOptions();
		} else {
			logger.info("No FileAppender found.");
		}
	}


}