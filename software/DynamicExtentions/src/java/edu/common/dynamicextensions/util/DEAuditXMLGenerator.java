/*
 *
 */

package edu.common.dynamicextensions.util;

import edu.wustl.common.audit.util.AuditXMLGenerator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.AuditException;

/**
 * This class is to generate audit XML for DE models.
 * The Class DEAuditXMLGenerator.
 *
 * @author suhas_khot
 */
public class DEAuditXMLGenerator extends AuditXMLGenerator
{

	/** The LOGGER. */
	private static Logger LOGGER = Logger.getCommonLogger(DEAuditXMLGenerator.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 *
	 * @throws AuditException the audit exception
	 */
	public static void main(String[] args) throws AuditException
	{
		DEAuditXMLGenerator deAuditXMLGenerator = new DEAuditXMLGenerator();
		int classCounter = deAuditXMLGenerator.generateAuditMetadataXML(args);
		LOGGER.info("Total number of classes:" + classCounter);
	}


	/**
	 * Instantiates a new dE audit xml generator.
	 */
	public DEAuditXMLGenerator()
	{
		super(new DEAuditXMLTagGenerator());
	}
}
