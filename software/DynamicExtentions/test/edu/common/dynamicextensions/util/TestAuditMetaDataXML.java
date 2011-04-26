/**
 *
 */

package edu.common.dynamicextensions.util;

import edu.common.dynamicextensions.domain.Entity;

/**
 * @author suhas_khot
 *
 */
public class TestAuditMetaDataXML extends DynamicExtensionsBaseTestCase
{

	public void testAuditMetaDataXML()
	{
		try
		{
			String[] args = {"test", "testMetadata.xml", "true"};
			DEAuditXMLGenerator.main(args);
			assertTrue("Audit metadata xml", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	public void testAuditMetaDataXML1()
	{
		try
		{
			DEAuditXMLTagGenerator xmlTagGenerator = new DEAuditXMLTagGenerator();
			String xmlTags = xmlTagGenerator.getAuditableMetatdataXMLString(Entity.class.getName());
			System.out.println(xmlTags);
			assertNotNull("xmlTags is not null", xmlTags);
			assertFalse("xmlTags is not empty", "".equals(xmlTags));
			assertTrue("xmlTags contains attribute tag", xmlTags.startsWith("<AuditableClass"));
			assertTrue("xmlTags contains attribute tag", xmlTags.contains("attribute"));
			assertTrue("xmlTags contains attribute tag", xmlTags.contains("entityGroup"));
			assertTrue("xmlTags contains attribute tag", xmlTags.endsWith("</AuditableClass>"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
