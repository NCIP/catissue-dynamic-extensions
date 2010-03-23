
package edu.common.dynamicextensions.util.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.owasp.stinger.Stinger;

import au.com.bytecode.opencsv.CSVReader;
import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.FormControlNotes;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.domaininterface.SemanticPropertyInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.CategoryConstants;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.common.dynamicextensions.validation.category.CategoryValidator;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.exception.DAOException;

/**
 * @author kunal_kamble
 */
public class CategoryCSVFileParser extends CategoryFileParser
{

	private static final String DEFAULT_SEPERATOR=",";
	public static final String DEFAULT_ESCAPE_CHARACTER = "\"";
	protected CSVReader reader;

	private String[] line;

	protected long lineNumber = 0;

	/**
	 * @param filePath path  of the csv file
	 * @param baseDir base directory from which the filepath is mentioned.
	 * @param stinger the stinger validator object which is used to validate the pv strings.
	 * @throws DynamicExtensionsSystemException
	 * @throws FileNotFoundException
	 */
	public CategoryCSVFileParser(String filePath, String baseDirectory, Stinger stinger)
			throws DynamicExtensionsSystemException, FileNotFoundException
	{
		super(filePath, baseDirectory, stinger);
		reader = new CSVReader(new FileReader(getSystemIndependantFilePath(filePath)));
		categoryValidator = new CategoryValidator(this);
	}

	/**
	 * @return current line number
	 */
	public long getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * This method reads the next line
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public boolean readNext() throws IOException
	{
		//To skip the blank lines
		boolean flag = true;
		line = reader.readNext();
		while (line != null)
		{
			lineNumber++;
			if (line[0].length() != 0 && !line[0].startsWith("##"))
			{
				break;
			}
			line = reader.readNext();
		}

		if (line == null)
		{
			flag = false;
		}else
		{
			line = processEscapeCharacter(line, null, DEFAULT_ESCAPE_CHARACTER, DEFAULT_SEPERATOR);
		}
		return flag;

	}

	/**
	 * @return current line
	 */
	public String[] readLine()
	{
		return line;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#closeResources()
	 */
	public void closeResources() throws DynamicExtensionsSystemException
	{
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(
					"Exception occured while closing the resources", e);

		}
	}

	/**
	 * @return paths
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<String, List<String>> getPaths() throws DynamicExtensionsSystemException
	{
		Map<String, List<String>> entityNamePath = new LinkedHashMap<String, List<String>>();

		for (String entityNameAndPath : readLine())
		{
			List<String> path = new ArrayList<String>();
			StringBuffer entityPath = new StringBuffer();

			StringTokenizer stringTokenizer = new StringTokenizer(entityNameAndPath.split("~")[1],
					":");
			while (stringTokenizer.hasMoreTokens())
			{
				String entityName = stringTokenizer.nextToken();
				path.add(entityName);
				entityPath.append(entityName);
			}

			entityNamePath.put(entityPath.toString(), path);
		}
		return entityNamePath;
	}

	/**
	 * @return display label for the container
	 */
	public String getDisplyLable()
	{
		return processEscapeCharacter(readLine()[0].split(":"), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, ":")[1]
				.trim();
	}

	/**
	 * @return showCaption
	 */
	public boolean isShowCaption()
	{
		boolean showCaption = true;
		if (readLine().length > 1)
		{
			showCaption = Boolean.valueOf(readLine()[1].split("=")[1].trim());
		}
		return showCaption;
	}

	public String[] getCategoryPaths()
	{
		readLine()[0] = readLine()[0].replace("instance:", "");
		return readLine();
	}

	/**
	 * @return entity name
	 * @throws DynamicExtensionsSystemException
	 * @throws ClassNotFoundException
	 * @throws DAOException
	 */
	public String getEntityName() throws DynamicExtensionsSystemException, DAOException,
			ClassNotFoundException
	{
		categoryValidator.validateEntityName(readLine()[0].split(":")[0].trim());
		return readLine()[0].split(":")[0].trim();
	}

	/**
	 * @return attribute name
	 */
	public String getAttributeName()
	{
		return readLine()[0].split(":")[1].trim();
	}

	/**
	 * @return control type
	 */
	public String getControlType()
	{
		return readLine()[1].trim();
	}

	/**
	 * @return control label
	 */
	public String getControlCaption()
	{
		return readLine()[2].trim();
	}

	/**
	 * @return permissible values collection
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<String, Collection<SemanticPropertyInterface>> getPermissibleValues()
			throws DynamicExtensionsSystemException
	{
		//counter for to locate the start of the permissible values
		String[] nextLine = readLine();
		int counter;
		boolean permissibleValuesPresent = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		for (counter = 0; counter < nextLine.length; counter++)
		{
			if (nextLine[counter].toLowerCase(locale).startsWith(
					CategoryCSVConstants.PERMISSIBLE_VALUES.toLowerCase(locale))
					|| nextLine[counter].toLowerCase(locale).startsWith(
							CategoryCSVConstants.PERMISSIBLE_VALUES_FILE.toLowerCase(locale)))
			{
				permissibleValuesPresent = true;
				break;
			}
		}
		if (!permissibleValuesPresent)
		{
			return null;
		}

		Map<String, Collection<SemanticPropertyInterface>> pvVsSemanticPropertyCollection = new LinkedHashMap<String, Collection<SemanticPropertyInterface>>();

		int indexOfTilda = nextLine[counter].indexOf('~');
		String permissibleValueKey = nextLine[counter].substring(0, indexOfTilda);

		if (CategoryCSVConstants.PERMISSIBLE_VALUES.equalsIgnoreCase(permissibleValueKey))
		{
			String pvString = nextLine[counter].substring(indexOfTilda + 1);
			String originalPVString = pvString;
			int pvStringLength = 1;
			String[] pvListString = processEscapeCharacter(originalPVString.split(":"), originalPVString,
					DEFAULT_ESCAPE_CHARACTER, ":");
			for (String pv : pvListString)
			{

				int indexOFColon = pv.indexOf(':');
				int indexOfConceptCodeStart = pv.indexOf('{');
				if (indexOFColon < indexOfConceptCodeStart
						|| (indexOFColon == -1 && indexOfConceptCodeStart == -1))
				{
					indexOfConceptCodeStart = -1;
				}
				String permiValue = "";
				Collection<SemanticPropertyInterface> semanticPropertyCollection = null;

				if (indexOfConceptCodeStart != -1)
				{//Concept code is present
					semanticPropertyCollection = new HashSet<SemanticPropertyInterface>();
					permiValue = pv.substring(0, indexOfConceptCodeStart);
					pvStringLength = pvStringLength + permiValue.length();

					int conceptCodeEnd = pv.indexOf('}');
					if (pv.charAt(conceptCodeEnd + 1) == ':')
					{
						pvStringLength = pvStringLength + 1;
					}
					String tempCodesString = pv.substring(indexOfConceptCodeStart + 1,
							conceptCodeEnd);
					pvStringLength = pvStringLength + 2;

					String[] conceptString = tempCodesString.split(":");
					pvStringLength = pvStringLength + conceptString.length - 1;
					for (String conceptAttrString : conceptString)
					{//All concept codes for the pv
						int seqNo = 1;
						SemanticPropertyInterface semanticProperty = DomainObjectFactory
								.getInstance().createSemanticProperty();
						String[] conceptAttributes = conceptAttrString.split("#");
						pvStringLength = pvStringLength + conceptAttributes.length - 1;
						for (String conceptAttr : conceptAttributes)
						{
							String[] conceptCodeKeyValue = conceptAttr.split("~");
							populateSemanticProperty(semanticProperty, conceptCodeKeyValue[0],
									conceptCodeKeyValue[1]);
							pvStringLength = pvStringLength + conceptCodeKeyValue[0].length()
									+ conceptCodeKeyValue[1].length() + 1;
						}
						semanticProperty.setSequenceNumber(seqNo);
						seqNo++;
						semanticPropertyCollection.add(semanticProperty);
					}
				}
				else
				{//Concept Code not defined

					{
						permiValue = pv;
						pvStringLength = pvStringLength + permiValue.length();
					}
				}
				pvVsSemanticPropertyCollection.put(DynamicExtensionsUtility
						.getEscapedStringValue(permiValue), semanticPropertyCollection);
				/*validateStringForStinger(permiValue);*/
				pvString = originalPVString.substring(pvStringLength - 1);
			}
		}

		else if (CategoryCSVConstants.PERMISSIBLE_VALUES_FILE.equalsIgnoreCase(permissibleValueKey))
		{//PV from File
			String filePath = getSystemIndependantFilePath(nextLine[counter]
					.substring(indexOfTilda + 1));
			try
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(filePath)));
				String line = null;
				while ((line = reader.readLine()) != null)
				{
					if (line != null && line.trim().length() != 0)//skip the line if it is blank
					{
						Collection<SemanticPropertyInterface> semanticPropertyCollection = null;
						String pvString = line.trim();
						int indexOfConceptCodeStart = pvString.indexOf('{');
						int conceptCodeEnd = pvString.indexOf('}');
						String permissibleVale = "";
						if (indexOfConceptCodeStart != -1 && conceptCodeEnd != -1)
						{
							semanticPropertyCollection = new HashSet<SemanticPropertyInterface>();
							permissibleVale = pvString.substring(0, indexOfConceptCodeStart);
							String tempCodesString = pvString.substring(
									indexOfConceptCodeStart + 1, conceptCodeEnd);
							String[] conceptString = tempCodesString.split(":");
							for (String conceptAttrString : conceptString)
							{//All concept codes for the pv
								int seqNo = 1;
								SemanticPropertyInterface semanticProperty = DomainObjectFactory
										.getInstance().createSemanticProperty();
								String[] conceptAttributes = conceptAttrString.split("#");
								for (String conceptAttr : conceptAttributes)
								{
									String[] conceptCodeKeyValue = conceptAttr.split("~");
									populateSemanticProperty(semanticProperty,
											conceptCodeKeyValue[0], conceptCodeKeyValue[1]);
								}
								semanticProperty.setSequenceNumber(seqNo);
								seqNo++;
								semanticPropertyCollection.add(semanticProperty);
							}
						}
						else
						{
							permissibleVale = pvString;
						}
						validateStringForStinger(permissibleVale);
						pvVsSemanticPropertyCollection
								.put(DynamicExtensionsUtility
										.getEscapedStringValue(permissibleVale),
										semanticPropertyCollection);
					}
				}
			}
			catch (FileNotFoundException e)
			{
				throw new DynamicExtensionsSystemException(
						"Error while reading permissible values file " + filePath, e);
			}
			catch (IOException e)
			{
				throw new DynamicExtensionsSystemException(
						"Error while reading permissible values file " + filePath, e);
			}

		}

		return pvVsSemanticPropertyCollection;
	}

	/**
	 * This method will verify the string value provided with the Stringer object
	 * provided at the time of instantiation. If stinger object was null it will
	 * not validate the string.
	 * If stinger is provided will validate that and if that value contains some unsafe
	 * characters will throw a exception.
	 * @param value string to be validated.
	 * @throws DynamicExtensionsSystemException if string is invalid
	 */
	protected void validateStringForStinger(String value) throws DynamicExtensionsSystemException
	{
		if (stingerValidator != null && !stingerValidator.validate(value))
		{
			throw new DynamicExtensionsSystemException(ApplicationProperties
					.getValue(CategoryConstants.LINE_NUMBER)
					+ " "
					+ getLineNumber()
					+ " "
					+ ApplicationProperties.getValue("readingFile")
					+ getFilePath()
					+ ". "
					+ ApplicationProperties.getValue("dynExtn.validation.unsafe.character", value));
		}
	}

	/**
	 * @return getPermissibleValueOptions
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<String, String> getPermissibleValueOptions()
	{
		Map<String, String> permissibleValueOptions = new HashMap<String, String>();
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		for (String string : readLine())
		{
			if (string.toLowerCase(locale).startsWith(
					CategoryCSVConstants.PERMISSIBLE_VALUE_OPTIONS.toLowerCase(locale) + "~"))
			{
				String[] controlOptionsValue = string.split("~")[1].split(":");

				for (String optionValue : controlOptionsValue)
				{
					permissibleValueOptions.put(optionValue.split("=")[0],
							optionValue.split("=")[1]);
				}

			}
		}
		return permissibleValueOptions;
	}

	/**
	 * @param semanticProperty
	 * @param key
	 * @param value
	 */
	private void populateSemanticProperty(SemanticPropertyInterface semanticProperty, String key,
			String value)
	{
		if (key.equalsIgnoreCase(Constants.CONCEPT_CODE))
		{
			semanticProperty.setConceptCode(value);
		}
		else if (key.equalsIgnoreCase(Constants.CONCEPT_DEFINITION))
		{
			semanticProperty.setConceptDefinition(value);
		}
		else if (key.equalsIgnoreCase(Constants.PREFERRED_NAME))
		{
			semanticProperty.setThesaurasName(value);
		}
		else if (key.equalsIgnoreCase(Constants.DEFINITION_SOURCE))
		{
			semanticProperty.setTerm(value);
		}
	}

	/**
	 * @return
	 */
	public boolean hasDisplayLable()
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (readLine()[0].trim().toLowerCase().startsWith(
				CategoryCSVConstants.DISPLAY_LABLE.toLowerCase(locale)))
		{
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean hasFormDefination()
	{
		boolean flag = false;
		if (CategoryCSVConstants.FORM_DEFINITION.equalsIgnoreCase(readLine()[0].trim()))
		{
			flag = true;
		}
		return flag;
	}

	/**
	 * @return
	 */
	public String getCategoryName()
	{
		return readLine()[0].trim();
	}

	/**
	 * @return
	 */
	public String getEntityGroupName()
	{
		return readLine()[0].trim();
	}

	/**
	 * @return
	 */
	public boolean hasSubcategory()
	{
		boolean flag = false;
		if (readLine()[0].toLowerCase().contains("subcategory:"))
		{
			flag = true;
		}
		return flag;
	}

	/**
	 * @return
	 */
	public String getTargetContainerCaption()
	{
		return processEscapeCharacter(readLine()[0].split(":"), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, ":")[1]
				.trim();
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public String getMultiplicity() throws DynamicExtensionsSystemException
	{
		categoryValidator.validateMultiplicity();
		return processEscapeCharacter(readLine()[0].split(":"), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, ":")[2]
				.trim();
	}

	/**
	 * @return
	 */
	public Map<String, String> getControlOptions()
	{
		Map<String, String> controlOptions = new HashMap<String, String>();
		populateOptionsMap(controlOptions, CategoryCSVConstants.OPTIONS);
		return controlOptions;
	}

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	public Map<String, Object> getRules(String attributeName)
			throws DynamicExtensionsSystemException
	{
		Map<String, Object> rules = new HashMap<String, Object>();
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		for (String string : readLine())
		{
			if (string.trim().toLowerCase(locale).startsWith(
					CategoryCSVConstants.RULES.toLowerCase(locale) + "~"))
			{
				String[] rulesValues = string.trim().split("~")[1].split(":");

				for (String ruleValue : rulesValues)
				{
					if (ruleValue.trim().toLowerCase(locale).startsWith(
							CategoryCSVConstants.RANGE.toLowerCase(locale)))
					{
						String[] rangeValues = ruleValue.trim().split("-");
						for (String rangeValue : rangeValues)
						{
							if (!(rangeValue.trim().toLowerCase(locale)
									.startsWith(CategoryCSVConstants.RANGE.toLowerCase(locale))))
							{
								String[] minMaxValues = rangeValue.trim().split("&");
								boolean isDateRange = false;
								Map<String, Object> valuesMap = new HashMap<String, Object>();
								for (String value : minMaxValues)
								{
									if (value.trim().split("=")[1].contains("/"))
									{
										valuesMap.put(value.trim().split("=")[0], value.trim()
												.split("=")[1].replace("/", "-"));
										isDateRange = true;
									}
									else
									{
										valuesMap.put(value.trim().split("=")[0], value.trim()
												.split("=")[1]);
									}
								}

								if (isDateRange)
								{
									rules.put(CategoryCSVConstants.DATE_RANGE, valuesMap);
								}
								else
								{
									rules.put(CategoryCSVConstants.RANGE.toLowerCase(locale),
											valuesMap);
								}
							}
							else
							{
								// If rule name is not correctly spelled as 'range', then throw an exception.
								if (!CategoryCSVConstants.RANGE.equalsIgnoreCase(rangeValue.trim()))
								{
									throw new DynamicExtensionsSystemException(
											ApplicationProperties
													.getValue(CategoryConstants.CREATE_CAT_FAILS)
													+ ApplicationProperties
															.getValue("incorrectRuleName")
													+ attributeName);
								}
							}
						}
					}
					else if (ruleValue.trim().toLowerCase(locale).startsWith(
							CategoryCSVConstants.ALLOW_FUTURE_DATE.toLowerCase(locale)))
					{
						rules.put(ruleValue.trim().split("=")[0], true);
					}
					else
					{
						// If rule name is not correctly spelled as 'required', then throw an exception.
						if (!CategoryCSVConstants.REQUIRED.equalsIgnoreCase(ruleValue.trim()))
						{
							throw new DynamicExtensionsSystemException(ApplicationProperties
									.getValue(CategoryConstants.CREATE_CAT_FAILS)
									+ ApplicationProperties.getValue("incorrectRuleName")
									+ attributeName);
						}

						rules.put(ruleValue.trim().split("=")[0], null);
					}
				}
			}
		}
		Collection<String> ruleCollection = new HashSet<String>();
		for (String ruleName : rules.keySet())
		{
			ruleCollection.add(ruleName);
		}
		ValidatorUtil.checkForConflictingRules(ruleCollection, attributeName);
		return rules;
	}

	/**
	 * @return showCaption
	 * @throws IOException
	 */
	public boolean isOverridePermissibleValues() throws IOException
	{
		boolean flag = false;
		if (CategoryCSVConstants.OVERRIDE_PV.equals(readLine()[0].split("=")[0].trim()))
		{
			String string = readLine()[0].split("=")[1].trim();
			readNext();
			flag = Boolean.valueOf(string);

		}
		return flag;

	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#hasRelatedAttributes()
	 */
	public boolean hasRelatedAttributes()
	{
		boolean flag = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (readLine() != null
				&& readLine()[0].trim().toLowerCase(locale).startsWith(
						CategoryCSVConstants.RELATED_ATTIBUTE.toLowerCase(locale)))
		{
			flag = true;
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#hasSkipLogicAttributes()
	 */
	public boolean hasSkipLogicAttributes()
	{
		boolean flag = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (readLine() != null
				&& readLine()[0].trim().toLowerCase(locale).startsWith(
						CategoryCSVConstants.SKIP_LOGIC_ATTIBUTE.toLowerCase(locale)))
		{
			flag = true;
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#hasInsatanceInformation()
	 */
	public boolean hasInsatanceInformation()
	{
		boolean flag = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (readLine() != null
				&& readLine()[0].trim().toLowerCase(locale).startsWith(
						CategoryCSVConstants.INSTANCE.toLowerCase(locale)))
		{
			flag = true;
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#getDefaultValueForRelatedAttribute()
	 */
	public String getDefaultValueForRelatedAttribute()
	{
		return processEscapeCharacter(readLine()[0].split("="), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, "=")[1]
				.trim();
	}

	public String getRelatedAttributeName()
	{
		return readLine()[0].split("=")[0].split(":")[1].trim();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#getDefaultValue()
	 */
	public String getDefaultValue()
	{
		String defaultValue = null;
		for (String string : readLine())
		{
			if (string.startsWith(CategoryCSVConstants.DEFAULT_VALUE))
			{
				defaultValue = processEscapeCharacter(string.split("="), string,
						DEFAULT_ESCAPE_CHARACTER,
						"=")[1];
			}
		}
		return defaultValue;
	}

	@Override
	public List<FormControlNotesInterface> getFormControlNotes(
			List<FormControlNotesInterface> controlNotes) throws DynamicExtensionsSystemException,
			IOException
	{
		// Check if the heading information has been repeated.
		CategoryValidator.checkHeadingInfoRepeatation(readLine()[0], lineNumber);

		if (readLine()[0].startsWith(CategoryConstants.NOTE))
		{
			for (String string : readLine())
			{
				CategoryValidator.checkIfNoteIsAppropriate(processEscapeCharacter(string.trim()
						.split("~"), string, DEFAULT_ESCAPE_CHARACTER, "~")[1], lineNumber);

				String stringNotes = string.substring(string.indexOf("~") + 1);
				String[] notes = processEscapeCharacter(stringNotes.split(":"), stringNotes,
						DEFAULT_ESCAPE_CHARACTER, ":");
				FormControlNotesInterface formControlNote = new FormControlNotes();
				formControlNote.setNote(notes[0]);
				controlNotes.add(formControlNote);
			}

			if (readNext())
			{
				String[] nextLine = readLine();
				if (nextLine != null && nextLine.length != 0)
				{
					getFormControlNotes(controlNotes);
				}
			}
		}

		return controlNotes;
	}

	@Override
	public String getHeading() throws DynamicExtensionsSystemException, IOException
	{
		String heading = "";

		String[] headingDetails = readLine();

		if (headingDetails != null && headingDetails.length != 0
				&& headingDetails[0].startsWith(CategoryConstants.HEADING))
		{
			categoryValidator.checkIfHeadingIsAppropriate(headingDetails[0], lineNumber);

			heading = processEscapeCharacter(headingDetails[0].split("~"), headingDetails[0],
					DEFAULT_ESCAPE_CHARACTER, "~")[1];
			readNext();
		}

		return heading;
	}

	@Override
	public boolean isSingleLineDisplayEnd() throws IOException
	{
		boolean singleLineDisplayEnds = false;
		if (readLine().length > 0
				&& readLine()[0].equalsIgnoreCase(CategoryConstants.SINGLE_LINE_DISPLAY_END))
		{
			inSignleLineDisplay = false;
			singleLineDisplayEnds = true;
		}
		return singleLineDisplayEnds;
	}

	@Override
	public boolean isSingleLineDisplayStarted() throws IOException
	{
		if (readLine().length > 0
				&& readLine()[0].equalsIgnoreCase(CategoryConstants.SINGLE_LINE_DISPLAY_START))
		{
			inSignleLineDisplay = true;
			readNext();
		}
		return inSignleLineDisplay;
	}

	@Override
	public boolean hasSeparator()
	{
		boolean flag = false;
		if (CategoryCSVConstants.SEPARATOR.equalsIgnoreCase(readLine()[0].split(":")[0].trim()))
		{
			flag = true;
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#getSeparator()
	 * Separator:'<separator_string>'
	 */
	public String getSeparator()
	{
		String separator = readLine()[0].substring(readLine()[0].indexOf(":") + 2, readLine()[0]
				.length() - 1);
		return separator;
	}

	@Override
	public boolean hasCommonControlOptions()
	{
		boolean flag = false;
		if (CategoryCSVConstants.COMMON_OPTIONS
				.equalsIgnoreCase(readLine()[0].split("~")[0].trim()))
		{
			flag = true;
		}
		return flag;
	}

	@Override
	public Map<String, String> getCommonControlOptions()
	{
		Map<String, String> controlOptions = new HashMap<String, String>();
		populateOptionsMap(controlOptions, CategoryCSVConstants.COMMON_OPTIONS);
		return controlOptions;
	}

	private void populateOptionsMap(Map<String, String> controlOptions, String optionConstant)
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		for (String string : readLine())
		{
			if (string.toLowerCase(locale).startsWith(optionConstant.toLowerCase(locale) + "~"))
			{
				String[] controlOptionsValue = string.split("~")[1].split(":");

				for (String optionValue : controlOptionsValue)
				{
					controlOptions.put(optionValue.split("=")[0], optionValue.split("=")[1]);
				}
			}
		}

	}

	/**
	 *
	 */
	public String getSkipLogicSourceAttributeClassName()
	{
		return readLine()[0].split(":")[0].trim();
	}

	/**
	 *
	 */
	public String getSkipLogicSourceAttributeName()
	{
		return readLine()[0].split(":")[1].trim();
	}

	/**
	 *
	 */
	public String getSkipLogicTargetAttributeClassName()
	{
		return readLine()[1].split("~")[1].split(":")[0].trim();
	}

	/**
	 *
	 */
	public String getSkipLogicTargetAttributeName()
	{
		return readLine()[1].split("~")[1].split(":")[1].trim();
	}

	/**
	 *
	 */
	public String getSkipLogicPermissibleValueName()
	{
		return readLine()[0].split(":")[2].trim();
	}

	/**
	 * This method will verify weather the file to which this parser
	 * object pointing is actually a category file or not.
	 * @return true if the file is category file.
	 */
	public boolean isCategoryFile() throws IOException
	{
		boolean isCategory = false;
		if (readNext() && hasFormDefination())
		{
			isCategory = true;
		}
		return isCategory;
	}

	public boolean isPVFile() throws IOException
	{
		boolean isPVFile = false;
		if (readNext() && hasEntityGroup())
		{
			isPVFile = true;
		}
		return isPVFile;
	}

	private boolean hasEntityGroup() throws IOException
	{
		return readLine()[0].trim().contains("Entity_Group");
	}

	/**
	 * This method merges the tokens enclosed in within specified separator
	 * @param tokenizedString
	 * @param originalString
	 * @param escapeCharacter
	 * @param separator
	 * @return
	 */
	public String[] processEscapeCharacter(String[] tokenizedString, String originalString,
			String escapeCharacter, String separator)
	{
		List<String> processedList = new ArrayList<String>();

		for (int tokenNumber = 0; tokenNumber < tokenizedString.length; tokenNumber++)
		{
			String processedString = "";
			int offset = tokenNumber;
			boolean flag = false;
			if (tokenizedString[tokenNumber].startsWith(escapeCharacter))
			{
				tokenizedString[tokenNumber] = tokenizedString[tokenNumber].substring(1);
				offset = getLastTokenNumber(escapeCharacter, tokenNumber, tokenizedString);
				flag = true;
			}

			for (; tokenNumber <= offset && tokenNumber < tokenizedString.length; tokenNumber++)
			{
				processedString = processedString.concat(tokenizedString[tokenNumber]);
				if (flag && tokenNumber != offset)
				{
					processedString = processedString.concat(separator);
				}
			}

			if (tokenNumber < tokenizedString.length && processedString.endsWith(escapeCharacter))
			{
				processedString = processedString.substring(0, processedString.length() - 1);
			}

			tokenNumber--;
			processedList.add(processedString);

		}
		handleLastToken(originalString, separator, processedList);
		String str[] = new String[processedList.size()];
		return processedList.toArray(str);
	}

	/**
	 * handles last token which is part if the enclosed string has a separator
	 * @param originalString
	 * @param separator
	 * @param processedList
	 */
	private void handleLastToken(String originalString, String separator, List<String> processedList)
	{
		if (originalString != null && originalString.endsWith(separator)
				&& !processedList.get(processedList.size() - 1).endsWith(separator))
		{

			processedList.add(processedList.size() - 1, processedList.get(processedList.size() - 1)
					+ separator);
		}

	}

	/**
	 * Get the last token number of the enclosed string
	 * @param escapeCharacter
	 * @param tokenNumber
	 * @param strings
	 * @return
	 */
	private int getLastTokenNumber(String escapeCharacter, int tokenNumber, String[] strings)
	{
		if (!strings[tokenNumber].endsWith(escapeCharacter) && (tokenNumber + 1 < strings.length))
		{
			tokenNumber++;
			tokenNumber = getLastTokenNumber(escapeCharacter, tokenNumber, strings);

		}
		return tokenNumber;
	}

}