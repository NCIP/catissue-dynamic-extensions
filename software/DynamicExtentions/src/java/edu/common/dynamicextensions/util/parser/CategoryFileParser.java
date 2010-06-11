
package edu.common.dynamicextensions.util.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.owasp.stinger.Stinger;

import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.FileReader;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.dao.exception.DAOException;

/**
 * @author kunal_kamble
 *
 */
public abstract class CategoryFileParser extends FileReader
{

	protected CategoryValidator categoryValidator;

	protected boolean inSignleLineDisplay;

	protected Stinger stingerValidator;

	/**
	 * @param filePath path  of the csv file
	 * @param baseDir base directory from which the filepath is mentioned.
	 * @param stinger the stinger validator object which is used to validate the pv strings.
	 * @throws DynamicExtensionsSystemException
	 */
	public CategoryFileParser(String filePath, String baseDirectory, Stinger stinger)
			throws DynamicExtensionsSystemException
	{
		super(filePath, baseDirectory);
		stingerValidator = stinger;
	}

	/**
	 * @return current line number in the category file
	 */
	public abstract long getLineNumber();

	/**
	 * @return false if end of category file
	 * otherwise reads the next line.
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException if problem in reading line
	 */
	public abstract boolean readNext() throws IOException, DynamicExtensionsSystemException;

	/**
	 * @return category name
	 */
	public abstract String getCategoryName();

	/**
	 * @return entity group name
	 */
	public abstract String getEntityGroupName();

	/**
	 * @return map with key as entity name and
	 * value as its complete path from the root
	 * @throws DynamicExtensionsSystemException
	 */
	public abstract Map<String, List<String>> getPaths() throws DynamicExtensionsSystemException;

	/**
	 * @return form label
	 */
	public abstract String getDisplyLable();

	/**
	 * @return
	 */
	public abstract boolean isShowCaption();

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public abstract String getEntityName() throws DynamicExtensionsSystemException, DAOException,
			ClassNotFoundException;

	/**
	 * @return attribute name
	 */
	public abstract String getAttributeName();

	/**
	 * @return type of control
	 */
	public abstract String getControlType();

	/**
	 * @return control label
	 */
	public abstract String getControlCaption();

	/**
	 * @return map with key as control property and
	 * value as value for the property
	 */
	public abstract Map<String, String> getControlOptions();

	/**
	 * @return getPermissibleValueOptions
	 * @throws DynamicExtensionsSystemException
	 */
	public abstract Map<String, String> getPermissibleValueOptions();

	/**
	 * Return a map of rules belonging to a category attribute.
	 * @return
	 */
	public abstract Map<String, Object> getRules(String attributeName)
			throws DynamicExtensionsSystemException;

	/**
	 * @return list of permissible values
	 * @throws DynamicExtensionsSystemException
	 */
	public abstract Map<String, Collection<SemanticPropertyInterface>> getPermissibleValues()
			throws DynamicExtensionsSystemException;

	/**
	 * @return
	 */
	public abstract boolean hasDisplayLable();

	/**
	 * @return
	 */
	public abstract boolean hasFormDefination();

	/**
	 * @return
	 */
	public abstract boolean hasSubcategory();

	/**
	 * @return target container caption
	 */
	public abstract String getTargetContainerCaption();

	/**
	 * @return multiplicity
	 */
	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public abstract String getMultiplicity() throws DynamicExtensionsSystemException;

	/**
	 * @return category path
	 */
	public abstract String[] getCategoryPaths();

	/**
	 * @return
	 */
	public CategoryValidator getCategoryValidator()
	{
		return categoryValidator;
	}

	/**
	 * @param categoryValidator
	 */
	public void setCategoryValidator(CategoryValidator categoryValidator)
	{
		this.categoryValidator = categoryValidator;
	}

	/**
	 * @return
	 */
	public abstract boolean hasRelatedAttributes();

	/**
	 * @return
	 */
	public abstract boolean hasSkipLogicAttributes();

	/**
	 * @return
	 */
	public abstract boolean hasInsatanceInformation();

	/**
	 * @return
	 */
	public abstract String getDefaultValueForRelatedAttribute();

	/**
	 * @return
	 */
	public abstract String getRelatedAttributeName();

	/**
	 * @return
	 */
	public abstract String getSkipLogicSourceAttributeName();

	/**
	 * @return
	 */
	public abstract String getSkipLogicSourceAttributeClassName();

	/**
	 * @return
	 */
	public abstract String getSkipLogicTargetAttributeName();

	/**
	 * @return
	 */
	public abstract String getSkipLogicTargetAttributeClassName();

	/**
	 * @return
	 */
	public abstract String getSkipLogicPermissibleValueName();

	/**
	 * @return
	 */
	public abstract String getDefaultValue();

	/**
	 * @return
	 */
	public abstract List<FormControlNotesInterface> getFormControlNotes(
			List<FormControlNotesInterface> controlNotes) throws DynamicExtensionsSystemException,
			IOException;

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws IOException
	 */
	public abstract String getHeading() throws DynamicExtensionsSystemException, IOException;

	/**
	 * @return
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException if problem in reading line
	 */
	public abstract boolean isSingleLineDisplayStarted() throws IOException, DynamicExtensionsSystemException;

	/**
	 * @return
	 * @throws IOException
	 */
	public abstract boolean isSingleLineDisplayEnd() throws IOException;

	/**
	 * @return
	 */
	public abstract boolean hasSeparator();

	/**
	 * @return
	 */
	public abstract String getSeparator();

	/**
	 * @return
	 */
	public abstract boolean hasCommonControlOptions();

	/**
	 * @return
	 */
	public abstract Map<String, String> getCommonControlOptions();

	/**
	 *This method closes the stream which was open to read the file
	 * @throws IOException
	 */
	public abstract void closeResources() throws DynamicExtensionsSystemException;

	/**
	 * This method will verify weather the file to which this parser
	 * object pointing is actually a category file or not.
	 * @return true if the file is category file.
	 * @throws DynamicExtensionsSystemException if problem reading line
	 */
	public abstract boolean isCategoryFile() throws IOException, DynamicExtensionsSystemException;

	/**
	 * This method will verify weather the file to which this parser
	 * object pointing is actually a category file or not.
	 * @return true if the file is category file.
	 * @throws DynamicExtensionsSystemException if problem in reading line
	 */
	public abstract boolean isPVFile() throws IOException, DynamicExtensionsSystemException;

}