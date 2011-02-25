/**
 *
 */

package edu.common.dynamicextensions.util.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.GridViewColumn;
import edu.common.dynamicextensions.domaininterface.StaticCategoryInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.StaticCategoryManager;
import edu.common.dynamicextensions.entitymanager.StaticCategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.FileReader;
import edu.common.dynamicextensions.util.xml.FormDefinition;
import edu.common.dynamicextensions.util.xml.XMLToObjectConverter;
import edu.common.dynamicextensions.util.xml.FormDefinition.Form;
import edu.common.dynamicextensions.util.xml.FormDefinition.Form.Tag;
import edu.common.dynamicextensions.util.xml.FormDefinition.Form.GridView.GridDisplayColumn;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author suhas_khot
 *
 */
public class StaticCategoryGenerator
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}

	/**
	 * @param args
	 *            command line arguments
	 * @throws JAXBException
	 *             JAXB exception
	 * @throws FileNotFoundException
	 *             File Not Found Exception
	 * @throws DynamicExtensionsSystemException
	 *             Dynamic Extensions System Exception
	 * @throws DynamicExtensionsApplicationException
	 *             Dynamic Extensions Application Exception
	 * @throws SAXException
	 */
	public static void main(String[] args) throws JAXBException, FileNotFoundException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException, SAXException
	{
		final XMLToObjectConverter converter = new XMLToObjectConverter(FormDefinition.class
				.getPackage().getName(), null);

		final String xmlFilePath = args[0];

		final FileReader fileReader = new FileReader(xmlFilePath, "");
		final FormDefinition formDefination = (FormDefinition) converter
				.getJavaObject(new FileInputStream(fileReader.getFilePath()));
		final Form form = formDefination.getForm().get(0);
		StaticCategoryManagerInterface staticCategoryManager = StaticCategoryManager.getInstance();

		StaticCategoryInterface staticCategory = staticCategoryManager.getStaticCategoryByName(form
				.getName());

		if (staticCategory == null)
		{
			staticCategory = DomainObjectFactory.getInstance().createStaticCategory();
			validateStaticFormName(form.getName());
			staticCategory.setName(form.getName());
		}

		staticCategory.setFormURL(form.getUrl());
		Collection<TaggedValueInterface> taggedValueCollection = new HashSet<TaggedValueInterface>();
		Collection<GridViewColumn> gridColBeans = new HashSet<GridViewColumn>();
		List<Tag> taggedValueList = form.getTag();
		if (form.getGridView() != null)
		{
			List<GridDisplayColumn> columnDisplayList = form.getGridView().getGridDisplayColumn();

			for (GridDisplayColumn gridDisplayColumn : columnDisplayList)
			{
				GridViewColumn gridViewCol = new GridViewColumn();

				gridViewCol.setGridDisplayColumn(gridDisplayColumn.getDispalyLabel());
				gridViewCol.setGridTableColumn(gridDisplayColumn.getColumnName());
				gridViewCol.setDisplayOrder(gridDisplayColumn.getDisplayOrder());
				gridColBeans.add(gridViewCol);
			}
		if(staticCategory.getGridViewColumnList()!=null)
			staticCategory.getGridViewColumnList().clear();
			staticCategory.getGridViewColumnList().addAll(gridColBeans);
		}

		if (form.getDataQuery() != null)
		{
			staticCategory.setDataQuery(form.getDataQuery());
		}
		for (Tag tag : taggedValueList)
		{
			TaggedValueInterface taggedValue = DomainObjectFactory.getInstance()
					.createTaggedValue();
			taggedValue.setKey(tag.getName());
			taggedValue.setValue(tag.getValue());
			taggedValueCollection.add(taggedValue);
		}
		staticCategory.setTaggedValueCollection(taggedValueCollection);

		staticCategoryManager.persistStaticCategory(staticCategory);
		Logger.out.info("Static Category Id is " + staticCategory.getId());
		Logger.out
				.info("-----------Static Category created sucessfully---------------------------");
	}

	/**
	 * @param formName
	 *            static category form name
	 * @throws DynamicExtensionsApplicationException
	 *             If formName is empty
	 */
	private static void validateStaticFormName(final String formName)
			throws DynamicExtensionsApplicationException
	{
		if (formName == null || "".equals(formName))
		{
			throw new DynamicExtensionsApplicationException("Please specify form name in xml.");
		}
	}

}
