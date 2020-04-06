package cs.usfca.edu.histfavcheckout.utils;

import java.io.IOException; 
import java.util.logging.FileHandler;
import java.util.logging.Level; 
import java.util.logging.Logger; 
import java.util.logging.*;
/**
 * Helps with logging to file
 * Only one instance should be created, after which the methods makeInfoLog(String message), makeWarningLog(String message) and makeSevereLog(String message)
 * can be accessed on class level 
 */
public class LoggerHelper {
	private final static Logger LOGGER =  
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); 
	private java.util.logging.Handler consoleHandler;
	private java.util.logging.Handler fileHandler;

	/**
	 * @param filename - should be in the format "./filename"
	 */
	public LoggerHelper(String filename)
	{
		try
		{
			//Creating consoleHandler and fileHandler
			consoleHandler = new ConsoleHandler();
			fileHandler  = new FileHandler(filename);
			//Assigning handlers to LOGGER object
			LOGGER.addHandler(consoleHandler);
			LOGGER.addHandler(fileHandler);
			//Setting levels to handlers and LOGGER
			consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);
			LOGGER.config("Configuration done.");
			//Console handler removed
			LOGGER.removeHandler(consoleHandler);

		}
		catch(IOException exception)
		{
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}

	}

	public static void makeInfoLog(String message) 
	{ 
		LOGGER.log(Level.INFO, message); 
	}

	public static void makeWarningLog(String message) 
	{ 
		LOGGER.log(Level.WARNING, message); 
	}

	public static void makeSevereLog(String message) 
	{ 
		LOGGER.log(Level.SEVERE, message); 
	}
}
