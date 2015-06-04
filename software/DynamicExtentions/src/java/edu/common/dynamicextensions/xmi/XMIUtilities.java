/*
 * Created on Aug 13, 2007
 * @author
 *
 */

package edu.common.dynamicextensions.xmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jmi.xmi.XmiReader;
import javax.jmi.xmi.XmiWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.mdr.NBMDRepositoryImpl;
import org.omg.uml.foundation.core.Attribute;
import org.omg.uml.foundation.core.Generalization;
import org.omg.uml.foundation.core.ModelElement;
import org.omg.uml.foundation.core.UmlClass;
import org.openide.util.Lookup;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.xmi.importer.AbstractXMIImporter;
import edu.wustl.common.util.logger.Logger;

/**
 * @author preeti_lodha
 * @author ashish_gupta
 *
 * Utility functions for XMI import/XMI
 */
public class XMIUtilities
{/*
//Common Utility functions
*/

	/**
	 * @return MDRepository object
	 */
	public static MDRepository getRepository(String storageFileName)
	{
		if(AbstractXMIImporter.rep == null) {
			XMIUtilities.cleanUpRepository(AbstractXMIImporter.STORAGE_FILE_NAME);
			Map<String, String> repositoryConfigMap = new HashMap<String, String>();
			try {
				repositoryConfigMap.put("storage","org.netbeans.mdr.persistence.btreeimpl.btreestorage.BtreeFactory");
				repositoryConfigMap.put("org.netbeans.mdr.persistence.btreeimpl.filename", storageFileName);
				AbstractXMIImporter.rep = new NBMDRepositoryImpl(repositoryConfigMap);
				AbstractXMIImporter.reader = (XmiReader) Lookup.getDefault().lookup(XmiReader.class);
			}catch(Exception e ) {
				Logger.out.info("XMIUtilities :: getRepository ::Exception ::"+e.getMessage());
			}
		}
		return  AbstractXMIImporter.rep;
	}
			
	public static XmiWriter getXMIWriter()
	{
		return (XmiWriter) Lookup.getDefault().lookup(XmiWriter.class);

	}

	public static String getClassNameForEntity(EntityInterface entity)
	{
		String name = null;
		if (entity != null)
		{
			name = entity.getName();
		}
		return name;
	}

	/**
	 * @return
	 */
	public static String getAttributeName(AttributeInterface attribute)
	{
		String name = null;
		if (attribute != null)
		{
			name = attribute.getName();
		}
		return name;
	}

	/***
	 * Finds and returns the first model element having the given
	 * <code>name</code> in the <code>umlPackage</code>, returns
	 * <code>null</code> if not found.
	 *
	 * @param umlPackage The modelPackage to search
	 * @param name the name to find.
	 * @return the found model element.
	 */
	public static Object find(org.omg.uml.modelmanagement.UmlPackage umlPackage, final String name)
	{
		return CollectionUtils.find(umlPackage.getOwnedElement(), new Predicate()
		{

			public boolean evaluate(Object object)
			{
				return StringUtils.trimToEmpty(((ModelElement) object).getName()).equals(name);
				//return true;
			}
		});
	}

	/**
	 * @param sourceXmiFileName source XMI file name
	 * @param targetXmiFileName target XMI file name
	 * @param xsltFileStream
	 * @throws TransformerException
	 * @throws FileNotFoundException
	 */
	public static void transform(String sourceXmiFileName, String targetXmiFileName,
			InputStream xsltFileStream) throws TransformerException, FileNotFoundException
	{
		if (sourceXmiFileName != null)
		{
			File sourceXmiFile = new File(sourceXmiFileName);

			Source xmlSource = new StreamSource(sourceXmiFile);
			Source xsltSource = new StreamSource(xsltFileStream);
			FileOutputStream targetFile = new FileOutputStream(targetXmiFileName);
			Result result = new StreamResult(targetFile);
			//create an instance of TransformerFactory
			TransformerFactory transFact = TransformerFactory.newInstance();
			if ((transFact != null) && (xsltSource != null) && (xmlSource != null))
			{

				Transformer trans = transFact.newTransformer(xsltSource);
				Logger.out.info("Transforming");
				trans.transform(xmlSource, result);
				Logger.out.info("Done");
			}
		}
	}

	/**
	 * This method gets the super class for the given UmlClass
	 * @param klass
	 * @return
	 */
	public static UmlClass getSuperClass(UmlClass klass)
	{
		UmlClass superClass = null;
		List superClasses = getSuperClasses(klass);
		if (!superClasses.isEmpty())
		{
			superClass = (UmlClass) superClasses.iterator().next();
		}
		return superClass;
	}

	/**
	 * This method gets all super classes for the given UmlClass
	 * @param klass
	 * @return
	 */
	public static List getSuperClasses(UmlClass klass)
	{
		List superClasses = new ArrayList();
		for (Iterator i = klass.getGeneralization().iterator(); i.hasNext();)
		{
			superClasses.add(((Generalization) i.next()).getParent());
		}
		return superClasses;
	}

	/**
	 * This method deletes unwanted repository files
	 */
	/**
	 * This method deletes unwanted repository files
	 * @param storageFileName
	 */
	public static void cleanUpRepository(String storageFileName)
	{
		if (new File(storageFileName + ".btd").exists())
		{
			(new File(storageFileName + ".btd")).delete();
		}
		if (new File(storageFileName + ".btx").exists())
		{
			(new File(storageFileName + ".btx")).delete();
		}
		if (new File(storageFileName + ".log").exists())
		{
			(new File(storageFileName + ".log")).delete();
		}
	}

	/**
	 * This method returns the UmlAttributes for the given Umlclass from XMI.
	 * boolean includeInherited - Specifies whether inherited attributes should be included or not.
	 * @param klass
	 * @param includeInherited
	 * @return
	 */
	public static Collection getAttributes(UmlClass klass, boolean includeInherited)
	{
		Collection atts = new ArrayList();
		if (includeInherited)
		{
			atts = getAttributeCollectionWithInheritedAttr(klass);
		}
		else
		{
			for (Iterator i = klass.getFeature().iterator(); i.hasNext();)
			{
				Object object = i.next();
				if (object instanceof Attribute)
				{
					atts.add(object);
				}
			}
		}
		return atts;
	}

	private static Collection getAttributeCollectionWithInheritedAttr(UmlClass klass)
	{
		Collection atts;
		Map attsMap = new HashMap();
		UmlClass superClass = klass;
		do
		{
			for (Iterator i = superClass.getFeature().iterator(); i.hasNext();)
			{
				Object obj = i.next();
				if (obj instanceof Attribute)
				{
					Attribute att = (Attribute) obj;
					if (attsMap.get(att.getName()) == null)
					{
						attsMap.put(att.getName(), att);
					}
				}
			}
			superClass = getSuperClass(superClass);
		}
		while (superClass != null);

		atts = attsMap.values();
		return atts;
	}
}
