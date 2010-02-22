/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author Vishvesh Mulay
 *@version 1.0
 */

package edu.common.dynamicextensions.ui.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;

public class SemanticPropertyBuilderUtil
{

	/**
	 * @param args args if any
	 */
	public static void main(String[] args)
	{
		String concept = "    Vishvesh Mulay, Priti munot, chetan patil , sujay Narkar ,    ";
		getSymanticPropertyCollection(concept);

	}

	/**This method builds the semantic property collection based on the string passed to it.
	 * The method splits the string using "," and then builds one instance of semantic property per string segment.
	 * @param conceptCodes Comma separated string of concept codes.
	 * @return Collection collection of semantic property objects.
	 */
	public static Collection<SemanticPropertyInterface> getSymanticPropertyCollection(
			String conceptCodes)
	{
		String codes = conceptCodes;
		if (codes == null || "".equals(codes.trim()))
		{
			codes = " ";
		}
		Collection<SemanticPropertyInterface> semanticPropertyCollection = new HashSet<SemanticPropertyInterface>();
		String[] individualConceptCodes = codes.split("[,]");

		for (int index = 0; index < individualConceptCodes.length; index++)
		{
			String conceptCode = individualConceptCodes[index];
			addSemanticProperty(semanticPropertyCollection, index, conceptCode);
		}
		return semanticPropertyCollection;
	}

	private static void addSemanticProperty(
			Collection<SemanticPropertyInterface> semanticPropertyCollection, int index,
			String conceptCode)
	{
		if (conceptCode != null && conceptCode.length() != 0)
		{
			DomainObjectFactory factory = DomainObjectFactory.getInstance();
			SemanticPropertyInterface semanticPropertyInterface = factory.createSemanticProperty();
			semanticPropertyInterface.setConceptCode(conceptCode);
			//TODO Check how to fetch the thesauras name and term for the semantic property.
			semanticPropertyInterface.setTerm(Constants.DEFAULT_TERM);
			semanticPropertyInterface.setThesaurasName(Constants.DEFAULT_THESAURAS_NAME);
			semanticPropertyInterface.setSequenceNumber(index + 1);
			semanticPropertyCollection.add(semanticPropertyInterface);
		}
	}

	/**
	 * Returns the ConceptCodeString.
	 * @param abstractMetadataInterface  AbstractMetadataInterface contains the SemanticPropertyCollection
	 * @return  String ConceptCodeString
	 */
	public static String getConceptCodeString(AbstractMetadataInterface abstractMetadataInterface)
	{
		String conceptCode;
		if (abstractMetadataInterface == null
				|| abstractMetadataInterface.getSemanticPropertyCollection() == null
				|| abstractMetadataInterface.getSemanticPropertyCollection().isEmpty())
		{
			conceptCode = "";
		}
		else
		{
			conceptCode = getConceptCodeString(abstractMetadataInterface
					.getOrderedSemanticPropertyCollection());
		}
		return conceptCode;
	}

	/**
	 * @param semanticPropertyCollection
	 * @return conceptCode
	 */
	public static String getConceptCodeString(
			Collection<SemanticPropertyInterface> semanticPropertyCollection)
	{
		StringBuffer conceptCode = new StringBuffer();
		if (semanticPropertyCollection != null)
		{
			Iterator iterator = semanticPropertyCollection.iterator();
			while (iterator.hasNext())
			{
				SemanticPropertyInterface semanticPropertyInterface = (SemanticPropertyInterface) iterator
						.next();
				conceptCode.append(semanticPropertyInterface.getConceptCode());
				if (iterator.hasNext())
				{
					conceptCode.append(',');
				}
			}
		}
		return conceptCode.toString();
	}
}
