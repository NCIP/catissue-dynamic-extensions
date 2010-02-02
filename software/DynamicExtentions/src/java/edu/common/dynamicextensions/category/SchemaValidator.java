
package edu.common.dynamicextensions.category;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SchemaValidator.
 * @author rajesh_vyas
 */
public class SchemaValidator
{

	private static final Logger LOGGER = Logger.getLogger(SchemaValidator.class.getName());

	protected static class Handler extends DefaultHandler
	{

		/**
		 *
		 * @param sAXParseException
		 * @throws SAXException
		 */
		public void error(final SAXParseException sAXParseException) throws SAXException
		{
			LOGGER.error(sAXParseException + "at line :" + sAXParseException.getLineNumber());
		}

		/**
		 *
		 * @param sAXParseException
		 * @throws SAXException
		 */
		public void fatalError(final SAXParseException sAXParseException) throws SAXException
		{
			LOGGER.error(sAXParseException + "at line :" + sAXParseException.getLineNumber());
		}

		/**
		 *
		 * @param sAXParseException
		 * @throws SAXException
		 */
		public void warning(final org.xml.sax.SAXParseException sAXParseException)
				throws org.xml.sax.SAXException
		{
			LOGGER.warn(sAXParseException + "at line :" + sAXParseException.getLineNumber());
		}
	}

	protected static class Resolver implements LSResourceResolver
	{

		/**
		 *
		 * @param str
		 * @param str1
		 * @param str2
		 * @param str3
		 * @param str4
		 * @return
		 */
		public org.w3c.dom.ls.LSInput resolveResource(final String str, final String str1,
				final String str2, final String str3, final String str4)
		{
			LOGGER.debug("Resolving : " + str + ":" + str1 + ":" + str2 + ":" + str3 + ":" + str4);
			return null;
		}
	}

	/**
	 *
	 * @param args
	 */
	/**
	 * @param schemaFile
	 * @param xmlFile
	 * @throws SAXException
	 * @throws IOException
	 */
	public void validateAgainstSchema(final File schemaFile, final File xmlFile)
			throws SAXException, IOException
	{

		final Handler handler = new Handler();

		final SchemaFactory schemaFactory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		schemaFactory.setErrorHandler(handler);
		//create a grammar object.
		final Schema schemaGrammar = schemaFactory.newSchema(schemaFile);

		LOGGER.debug("Created Grammar object for schema : " + xmlFile);

		final Resolver resolver = new Resolver();
		//create a validator to validate against grammar sch.
		final Validator schemaValidator = schemaGrammar.newValidator();
		schemaValidator.setResourceResolver(resolver);
		schemaValidator.setErrorHandler(handler);

		LOGGER.info("Validating " + xmlFile + " against grammar " + schemaFile);
		//validate xml instance against the grammar.
		schemaValidator.validate(new StreamSource(xmlFile));
	}
}
