/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright: (c) Washington University, School of Medicine 2004</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *@author Aarti Sharma
 *@version 1.0
 */

package edu.common.dynamicextensions.util.global;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Variables.
 *
 * @author aarti_sharma
 */
public class Variables
{

	/** The dynamic extensions home. */
	public static String dynamicExtensionsHome = "";

	/** The database definitions. */
	public static List databaseDefinitions = new ArrayList();

	/** The database driver. */
	public static String databaseDriver = "";

	/** The databasenames. */
	public static String[] databasenames;

	/** The container flag. */
	public static boolean containerFlag = true;

	/** The application cvs tag. */
	public static String applicationCvsTag = "";

	/** The hibernate cfg file name. */
	public static String hibernateCfgFileName = "hibernate.cfg.xml";
	/**
	 * URL of Jboss.
	 */
	public static String jbossUrl = "";
	public static String serverUrl="";

}
