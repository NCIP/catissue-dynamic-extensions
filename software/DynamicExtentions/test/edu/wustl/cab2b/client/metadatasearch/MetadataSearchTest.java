
package edu.wustl.cab2b.client.metadatasearch;

import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsBaseTestCase;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.cab2b.common.util.Constants;
import edu.wustl.cab2b.server.cache.EntityCache;

/**
 * @author Chandrakant Talele
 */
public class MetadataSearchTest extends DynamicExtensionsBaseTestCase
{

	/** Metadata search configuration parameter : Used when a permissible value is to be included in search*/
	public static final int PV = 3;

	private static final EntityCache entityCache = EntityCache.getInstance();

	public void testSearchAttributeBasedOnAttrName()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {Constants.ATTRIBUTE};
		String[] searchString = {"physicianName"};
		int basedOn = Constants.BASED_ON_TEXT;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("PhysicianInformation");
		}
		assertTrue(b);
	}

	public void testSearchEntityBasedOnClassName()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {Constants.CLASS};
		String[] searchString = {"Protein"};
		int basedOn = Constants.BASED_ON_TEXT;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("Protein");
		}
		assertTrue(b);
	}

	public void testSearchAttributeBasedOnDesc()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {Constants.ATTRIBUTE_WITH_DESCRIPTION};
		String[] searchString = {"Name of the doctor"};
		int basedOn = Constants.BASED_ON_TEXT;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("PhysicianInformation");
		}
		assertTrue(b);
	}

	public void testSearchEntityBasedOnDesc()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {Constants.CLASS_WITH_DESCRIPTION};
		String[] searchString = {"test3--ClinicalAnnotations"};
		int basedOn = Constants.BASED_ON_TEXT;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("ClinicalAnnotations");
		}
		assertTrue(b);
	}

	public void testSearchAttributeBasedOnConceptCode()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {Constants.ATTRIBUTE};
		String[] searchString = {"Y2211"};
		int basedOn = Constants.BASED_ON_CONCEPT_CODE;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("PhysicianInformation");
		}
		assertTrue(b);
	}

	public void testSearchEntityBasedOnConceptCode()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {Constants.CLASS};
		String[] searchString = {"C7738"};
		int basedOn = Constants.BASED_ON_CONCEPT_CODE;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("ClinicalAnnotations");
		}
		assertTrue(b);
	}

	public void testSearchInvalidTarget()
	{
		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {1234};
		String[] searchString = {"Romania"};
		boolean gotException = false;
		int basedOn = Constants.BASED_ON_TEXT;
		try
		{
			metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			gotException = true;
		}
		assertTrue(gotException);
	}

	public void testSearchInvalidTargetConceptCode()
	{
		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTarget = {1234};
		String[] searchString = {"C123"};
		boolean gotException = false;
		int basedOn = Constants.BASED_ON_CONCEPT_CODE;
		try
		{
			metadataSearch.search(searchTarget, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			gotException = true;
		}
		assertTrue(gotException);
	}

	public void testSearchPvBasedOnText()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {PV};
		String[] searchString = {"Irla Nursing Home & Polyclinic"};
		int basedOn = Constants.BASED_ON_TEXT;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("LabInfo");
		}
		assertTrue(b);
	}

	public void testSearchPvOnConceptCode()
	{
		MatchedClass resultMatchedClass = new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {PV};
		String[] searchString = {"7322"};
		int basedOn = Constants.BASED_ON_CONCEPT_CODE;
		try
		{
			resultMatchedClass = metadataSearch.search(searchTargetStatus, searchString, basedOn);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
			fail();
		}
		Set<EntityInterface> entities = resultMatchedClass.getEntityCollection();
		boolean b = false;
		for (EntityInterface eI : entities)
		{
			String result = eI.getName();
			b = b || result.contains("LabTest");
		}
		assertTrue(b);
	}

	public void testsearchNullTargetString()
	{
		new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {PV};
		int basedOn = Constants.BASED_ON_CONCEPT_CODE;
		try
		{
			metadataSearch.search(searchTargetStatus, null, basedOn);
			fail();
		}
		catch (CheckedException e)
		{
			assertTrue(true);
		}
	}

	public void testsearchNullSearchTargetStatus()
	{
		new MatchedClass();

		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		String[] searchString = {"C25228", "C62637"};
		int basedOn = Constants.BASED_ON_CONCEPT_CODE;
		try
		{
			metadataSearch.search(null, searchString, basedOn);
			fail();
		}
		catch (CheckedException e)
		{
			assertTrue(true);
		}
	}

	public void testsearchBasedOnOutOfBounds()
	{
		MetadataSearch metadataSearch = new MetadataSearch(entityCache);
		int[] searchTargetStatus = {PV};
		String[] searchString = {"C25228", "C62637"};
		try
		{
			metadataSearch.search(searchTargetStatus, searchString, 3);
			fail();
		}
		catch (CheckedException e)
		{
			assertTrue(true);
		}
	}
}