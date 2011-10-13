
package edu.common.dynamicextensions.util.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.owasp.stinger.Stinger;

import au.com.bytecode.opencsv.CSVReader;
import edu.common.dynamicextensions.domain.FormControlNotes;
import edu.common.dynamicextensions.domaininterface.FormControlNotesInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
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

	private static final String PERMISSIBLE_VALUES_FILE_ERROR = "Error while reading permissible values file ";
	private static final String DEFAULT_SEPERATOR = ",";
	public static final String DEFAULT_ESCAPE_CHARACTER = "\"";
	private static final String EQUAL_SIGN = "=";
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
	@Override
	public long getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * This method reads the next line
	 * @param reader
	 * @return true/false
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException if line contains the \n
	 */
	@Override
	public boolean readNext() throws IOException, DynamicExtensionsSystemException
	{
		//To skip the blank lines
		boolean flag = true;
		line = reader.readNext();
		while (line != null)
		{
			lineNumber++;
			if (line[0].length() != 0 && !line[0].startsWith("##"))
			{
				if (line[0].contains("\n"))
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.MISSING_QUOTES)
							+ Character.SPACE_SEPARATOR + lineNumber);
				}
				break;
			}
			line = reader.readNext();

		}

		if (line == null)
		{
			flag = false;
		}
		else
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
	@Override
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
	@Override
	public Map<String, List<String>> getPaths() throws DynamicExtensionsSystemException
	{
		Map<String, List<String>> entityNamePath = new LinkedHashMap<String, List<String>>();

		for (String entityNameAndPath : readLine())
		{
			List<String> path = new ArrayList<String>();
			StringBuffer entityPath = new StringBuffer();

			StringTokenizer stringTokenizer = new StringTokenizer(entityNameAndPath.split("~")[1],
					Constants.COLON);
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
	@Override
	public String getDisplyLable()
	{
		return processEscapeCharacter(readLine()[0].split(Constants.COLON), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, Constants.COLON)[1].trim();
	}

	/**
	 * @return showCaption
	 */
	@Override
	public boolean isShowCaption()
	{
		boolean showCaption = true;
		if (readLine().length > 1)
		{
			showCaption = Boolean.valueOf(readLine()[1].split("=")[1].trim());
		}
		return showCaption;
	}

	@Override
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
	@Override
	public String getEntityName() throws DynamicExtensionsSystemException
	{
		categoryValidator.validateEntityName(readLine()[0].split(Constants.COLON)[0].trim());
		return readLine()[0].split(Constants.COLON)[0].trim();
	}

	/**
	 * @return attribute name
	 */
	@Override
	public String getAttributeName()
	{
		return readLine()[0].split(Constants.COLON)[1].trim();
	}

	/**
	 * @return control type
	 */
	@Override
	public String getControlType()
	{
		return readLine()[1].trim();
	}

	/**
	 * @return control label
	 */
	@Override
	public String getControlCaption()
	{
		return readLine()[2].trim();
	}

	/**
	 * Check permissible value present.
	 * @return true, if check permissible value present
	 */
	@Override
	public boolean checkPermissibleValuePresent()
	{
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
		return permissibleValuesPresent;
	}

	/**
	 * Gets the permissible values of dependent attribute.
	 * @return the permissible values of dependent attribute
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	@Override
	public Set<String> getPermissibleValuesOfDependentAttribute()
			throws DynamicExtensionsSystemException
	{
		Set<String> listOfPermissibleValues = new HashSet<String>();
		String[] tokenOfCurrentline = readLine();
		for (int i = 0; i < tokenOfCurrentline.length; i++)
		{
			if (tokenOfCurrentline[i].startsWith(CategoryCSVConstants.PERMISSIBLE_VALUES_FILE))
			{
				String filePath = getSystemIndependantFilePath(tokenOfCurrentline[i]
						.substring(tokenOfCurrentline[i].indexOf('~') + 1));
				listOfPermissibleValues.addAll(getPermissibleValuesFromFile(filePath));
			}
			else if (tokenOfCurrentline[i].startsWith(CategoryCSVConstants.PERMISSIBLE_VALUES))
			{
				String originalPVString = tokenOfCurrentline[i].substring(tokenOfCurrentline[i]
						.indexOf('~') + 1);
				String[] pvListString = processEscapeCharacter(originalPVString.split(Constants.COLON),
						originalPVString, DEFAULT_ESCAPE_CHARACTER, Constants.COLON);
				listOfPermissibleValues.addAll(Arrays.asList(pvListString));
			}
		}
		return listOfPermissibleValues;
	}

	/**
	 * Gets the permissible values from file.
	 * @param filePath the file path
	 * @return the permissible values from file
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private Set<String> getPermissibleValuesFromFile(String filePath)
			throws DynamicExtensionsSystemException
	{
		Set<String> permissibleValues = new HashSet<String>();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			String line = reader.readLine();
			while (line != null)
			{
				if (!"".equals(line.trim()))
				{
					permissibleValues.add(line.trim());
				}
				line = reader.readLine();
			}
		}
		catch (FileNotFoundException e)
		{
			throw new DynamicExtensionsSystemException(PERMISSIBLE_VALUES_FILE_ERROR + filePath, e);
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(PERMISSIBLE_VALUES_FILE_ERROR + filePath, e);
		}
		finally
		{
			closeFileResource(reader, filePath);
		}
		return permissibleValues;
	}

	/**
	 * @return permissible values collection
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException 
	 */
	@Override
	public Set<String> getPermissibleValues() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
	
		//counter for to locate the start of the permissible values
		String[] nextLine = readLine();
		int counter;
		//line no entity name attribute name 
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

		Set<String> pvSet = new LinkedHashSet<String>();

		int indexOfTilda = nextLine[counter].indexOf('~');
		String permissibleValueKey = nextLine[counter].substring(0, indexOfTilda);

		if (CategoryCSVConstants.PERMISSIBLE_VALUES.equalsIgnoreCase(permissibleValueKey))
		{
			String originalPVString = nextLine[counter].substring(indexOfTilda + 1);
			String[] pvListString = processEscapeCharacter(originalPVString.split(Constants.COLON),
					originalPVString, DEFAULT_ESCAPE_CHARACTER, Constants.COLON);
			for (String pv : pvListString)
			{
				addPv(pvSet, pv);
			}
		}

		else if (CategoryCSVConstants.PERMISSIBLE_VALUES_FILE.equalsIgnoreCase(permissibleValueKey))
		{//PV from File
			String filePath = getSystemIndependantFilePath(nextLine[counter]
					.substring(indexOfTilda + 1));
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
				String line = reader.readLine();
				while (line != null)
				{
					if (!"".equals(line.trim()))//skip the line if it is blank
					{
						addPv(pvSet,line.trim());
					}
					line = reader.readLine();
				}
			}
			catch (FileNotFoundException e)
			{
				throw new DynamicExtensionsSystemException(
						PERMISSIBLE_VALUES_FILE_ERROR + filePath, e);
			}
			catch (IOException e)
			{
				throw new DynamicExtensionsSystemException(
						PERMISSIBLE_VALUES_FILE_ERROR + filePath, e);
			}
			finally
			{
				closeFileResource(reader, filePath);
			}
		}
		return pvSet;
	}

	/**
	 * @param pvSet
	 * @param pv
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	//Checks for double quotes in a pv
	private void addPv(Set<String> pvSet, String pv) throws DynamicExtensionsApplicationException, DynamicExtensionsSystemException
	{
		if(pv.contains(Constants.DOUBLE_QUOTES))
		{
			List<String> placeHoldersList=new ArrayList<String>();
			placeHoldersList.add(pv);
			placeHoldersList.add(getEntityName()+Character.SPACE_SEPARATOR+getAttributeName());
			throw  new DynamicExtensionsApplicationException(ApplicationProperties.getValue("pv.validation.doubleQuotes",placeHoldersList));
		}		
		
		pvSet.add(pv);
	}

	private void closeFileResource(BufferedReader reader, String filePath)
			throws DynamicExtensionsSystemException
	{
		try
		{
			if (reader != null) {
				reader.close();
			}
		}
		catch (IOException e)
		{
			throw new DynamicExtensionsSystemException(PERMISSIBLE_VALUES_FILE_ERROR + filePath, e);
		}
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
					+ Character.SPACE_SEPARATOR
					+ getLineNumber()
					+ Character.SPACE_SEPARATOR
					+ ApplicationProperties.getValue("readingFile")
					+ getRelativeFilePath()
					+ ". "
					+ ApplicationProperties.getValue("dynExtn.validation.unsafe.character", value));
		}
	}

	/**
	 * @return getPermissibleValueOptions
	 * @throws DynamicExtensionsSystemException
	 */
	@Override
	public Map<String, String> getPermissibleValueOptions()
	{
		Map<String, String> permissibleValueOptions = new HashMap<String, String>();
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		for (String string : readLine())
		{
			if (string.toLowerCase(locale).startsWith(
					CategoryCSVConstants.PERMISSIBLE_VALUE_OPTIONS.toLowerCase(locale) + "~"))
			{
				String[] controlOptionsValue = string.split("~")[1].split(Constants.COLON);

				for (String optionValue : controlOptionsValue)
				{
					permissibleValueOptions.put(optionValue.split("=")[0],
							optionValue.split("=")[1]);
				}

			}
		}
		return permissibleValueOptions;
	}

	//	/**
	//	 * @param semanticProperty semantic Property
	//	 * @param key key
	//	 * @param value value
	//	 */
	//	private void populateSemanticProperty(SemanticPropertyInterface semanticProperty, String key, String value)
	//	{
	//		if (key.equalsIgnoreCase(Constants.CONCEPT_CODE))
	//		{
	//			semanticProperty.setConceptCode(value);
	//		}
	//		else if (key.equalsIgnoreCase(Constants.CONCEPT_DEFINITION))
	//		{
	//			semanticProperty.setConceptDefinition(value);
	//		}
	//		else if (key.equalsIgnoreCase(Constants.PREFERRED_NAME))
	//		{
	//			semanticProperty.setConceptDefinitionSource(value);
	//		}
	//		else if (key.equalsIgnoreCase(Constants.DEFINITION_SOURCE))
	//		{
	//			semanticProperty.setConceptPreferredName(value);
	//		}
	//	}

	/**
	 * @return true/false if has display label
	 */
	@Override
	public boolean hasDisplayLable()
	{
		boolean iaLablePresent = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (readLine()[0].trim().toLowerCase().startsWith(
				CategoryCSVConstants.DISPLAY_LABLE.toLowerCase(locale)))
		{
			iaLablePresent = true;
		}
		return iaLablePresent;
	}

	/**
	 * @return true/false if has form definition
	 */
	@Override
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
	 * @return category name
	 */
	@Override
	public String getCategoryName()
	{
		return readLine()[0].trim();
	}

	/**
	 * @return entity group name
	 */
	@Override
	public String getEntityGroupName()
	{
		return readLine()[0].trim();
	}

	/**
	 * @return true/false if sub category
	 */
	@Override
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
	 * @return target container caption
	 */
	@Override
	public String getTargetContainerCaption()
	{
		return processEscapeCharacter(readLine()[0].split(Constants.COLON), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, Constants.COLON)[1].trim();
	}

	/**
	 * @return multiplicity
	 * @throws DynamicExtensionsSystemException
	 */
	@Override
	public String getMultiplicity() throws DynamicExtensionsSystemException
	{
		categoryValidator.validateMultiplicity();
		return processEscapeCharacter(readLine()[0].split(Constants.COLON), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, Constants.COLON)[2].trim();
	}

	/**
	 * Checks if is paste button configuration is defined or not and sets its
	 * value correspondingly.
	 *
	 * @return true, if checks if is paste button enabled
	 * @throws DynamicExtensionsSystemException
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	@Override
	public boolean isPasteButtonEnabled(int multiplicityValue)
			throws DynamicExtensionsSystemException
	{
		boolean pasteButtonEnabled = true;
		if (readLine() != null)
		{
			String[] tokens = processEscapeCharacter(readLine()[0].split(Constants.COLON), readLine()[0],
					DEFAULT_ESCAPE_CHARACTER, Constants.COLON);

			if (tokens.length >= 4)
			{
				String pasteOption = tokens[3].trim();
				if (pasteOption.contains("showPaste"))
				{
					validateMultiplicity(multiplicityValue);
					int startIndex = pasteOption.indexOf('=');
					if (startIndex == -1)
					{
						throw new DynamicExtensionsSystemException(
								"Inappropriate syntax for Paste option on line number:"
										+ getLineNumber());
					}
					else
					{
						String pasteValue = pasteOption.substring(startIndex + 1, pasteOption
								.length());
						pasteButtonEnabled = DynamicExtensionsUtility
								.validateAndReturnBooleanValue(pasteValue);
					}
				}
			}
		}
		return pasteButtonEnabled;
	}

	/**
	 * Validate whether showPaste option is defined for multi line sub category only.
	 *
	 * @param multiplicityValue the multiplicity value
	 *
	 * @throws DynamicExtensionsSystemException the dynamic extensions system exception
	 */
	private void validateMultiplicity(int multiplicityValue)
			throws DynamicExtensionsSystemException
	{
		if (multiplicityValue != -1)
		{
			throw new DynamicExtensionsSystemException(
					"Paste button configuration defined for single line "
							+ "category on line number:" + getLineNumber());
		}
	}

	/**
	 * @return control options
	 */
	@Override
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
	@Override
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
				String[] rulesValues = string.trim().split("~")[1].split(Constants.COLON);

				for (String ruleValue : rulesValues)
				{
					if (ruleValue.trim().toLowerCase(locale).startsWith(
							CategoryCSVConstants.RANGE.toLowerCase(locale)))
					{
						ruleValue=ruleValue.trim().replaceFirst("-","#");
						String[] rangeValues = ruleValue.trim().split("#");
						for (String rangeValue : rangeValues)
						{
							if (rangeValue.trim().toLowerCase(locale).startsWith(
									CategoryCSVConstants.RANGE.toLowerCase(locale)))
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
							else
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
	 * @throws DynamicExtensionsSystemException problem reading the line
	 */
	public boolean isOverridePermissibleValues() throws IOException,
			DynamicExtensionsSystemException
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public String getDefaultValueForRelatedAttribute()
	{
		return processEscapeCharacter(readLine()[0].split("="), readLine()[0],
				DEFAULT_ESCAPE_CHARACTER, "=")[1].trim();
	}

	@Override
	public String getRelatedAttributeName()
	{
		return readLine()[0].split("=")[0].split(Constants.COLON)[1].trim();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#getDefaultValue()
	 */
	@Override
	public String getDefaultValue()
	{
		String defaultValue = null;
		for (String string : readLine())
		{
			if (string.startsWith(CategoryCSVConstants.DEFAULT_VALUE))
			{
				defaultValue = processEscapeCharacter(string.split("="), string,
						DEFAULT_ESCAPE_CHARACTER, "=")[1];
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
				String[] notes = processEscapeCharacter(stringNotes.split(Constants.COLON), stringNotes,
						DEFAULT_ESCAPE_CHARACTER, Constants.COLON);
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
	public boolean isSingleLineDisplayStarted() throws IOException,
			DynamicExtensionsSystemException
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
		if (CategoryCSVConstants.SEPARATOR.equalsIgnoreCase(readLine()[0].split(Constants.COLON)[0].trim()))
		{
			flag = true;
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.util.parser.CategoryFileParser#getSeparator()
	 * Separator:'<separator_string>'
	 */
	@Override
	public String getSeparator()
	{
		return readLine()[0].substring(readLine()[0].indexOf(Constants.COLON) + 2, readLine()[0].length() - 1);
	}

	/**
	 * @return true/false
	 */
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

	/**
	 * @return map of common options
	 */
	@Override
	public Map<String, String> getCommonControlOptions()
	{
		Map<String, String> controlOptions = new HashMap<String, String>();
		populateOptionsMap(controlOptions, CategoryCSVConstants.COMMON_OPTIONS);
		return controlOptions;
	}

	/**
	 * @param controlOptions control options
	 * @param optionConstant option Constants
	 */
	private void populateOptionsMap(Map<String, String> controlOptions, String optionConstant)
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		for (String string : readLine())
		{
			if (string.toLowerCase(locale).startsWith(optionConstant.toLowerCase(locale) + "~"))
			{
				String[] controlOptionsValue = string.split("~")[1].split(Constants.COLON);

				for (String optionValue : controlOptionsValue)
				{
					controlOptions.put(optionValue.split("=")[0], optionValue.split("=")[1]);
				}
			}
		}

	}

	/**
	 * @return Skip Logic Source Attribute ClassName
	 */
	@Override
	public String getSkipLogicSourceAttributeClassName()
	{
		return readLine()[0].split(Constants.COLON)[0].trim();
	}

	/**
	 * @return Skip Logic Source Attribute Name
	 */
	@Override
	public String getSkipLogicSourceAttributeName()
	{
		return readLine()[0].split(Constants.COLON)[1].trim();
	}

	/**
	 * @return Skip Logic Target Attribute ClassName
	 */
	@Override
	public String getSkipLogicTargetAttributeClassName()
	{
		return readLine()[1].split("~")[1].split(Constants.COLON)[0].trim();
	}

	/**
	 * @return Skip Logic Target Attribute Name
	 */
	@Override
	public String getSkipLogicTargetAttributeName()
	{
		return readLine()[1].split("~")[1].split(Constants.COLON)[1].trim();
	}

	/**
	 * @return Skip Logic Permissible Value Name
	 */
	@Override
	public String getConditionValue()
	{
		return readLine()[0].split(Constants.COLON)[2].trim();
	}

	/**
	 * This method will verify weather the file to which this parser
	 * object pointing is actually a category file or not.
	 * @return true if the file is category file.
	 * @throws DynamicExtensionsSystemException problem reading the line
	 */
	@Override
	public boolean isCategoryFile() throws IOException, DynamicExtensionsSystemException
	{
		boolean isCategory = false;
		if (readNext() && hasFormDefination())
		{
			isCategory = true;
		}
		return isCategory;
	}

	@Override
	public boolean isPVFile() throws IOException, DynamicExtensionsSystemException
	{
		boolean isPVFile = false;
		if (readNext() && hasEntityGroup())
		{
			isPVFile = true;
		}
		return isPVFile;
	}

	/**
	 * @return true/false
	 * @throws IOException
	 */
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

	/**
	 * This method will validate weather the populateFromXML tag is present on
	 * the current line on which the category CSV file parser is & if it is
	 * specified then according to its value will be return.
	 * @return value specified for populateFromXML tag if tag present ,
	 * 		else False.
	 * @throws DynamicExtensionsSystemException if the value specified is
	 * 		other than true or false.
	 *
	 */
	@Override
	public boolean isPopulateFromXMLAttribute() throws DynamicExtensionsSystemException
	{
		boolean isPopulateFromXml = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		for (String token : readLine())
		{
			if (token.toLowerCase(locale).startsWith(
					CategoryCSVConstants.POPULATE_FROM_XML.toLowerCase(locale) + '='))
			{
				try
				{
					isPopulateFromXml = DynamicExtensionsUtility
							.validateAndReturnBooleanValue(token.split("=")[1]);
				}
				catch (DynamicExtensionsSystemException e)
				{
					throw new DynamicExtensionsSystemException(ApplicationProperties
							.getValue(CategoryConstants.CREATE_CAT_FAILS)
							+ ApplicationProperties.getValue(CategoryConstants.LINE_NUMBER)
							+ Character.SPACE_SEPARATOR
							+ getLineNumber()
							+ Character.SPACE_SEPARATOR
							+ ApplicationProperties.getValue("readingFile")
							+ getRelativeFilePath()
							+ ". "
							+ ApplicationProperties
									.getValue("dyExtn.category.invalidPopulateFromXML"), e);
				}
				break;
			}
		}
		return isPopulateFromXml;
	}

	/**
	 * @return true/false
	 */
	@Override
	public boolean hasTagValues()
	{
		boolean flag = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (readLine() != null
				&& readLine()[0].trim().toLowerCase(locale).startsWith(
						CategoryCSVConstants.FORM_TAG_VALUES.toLowerCase(locale)))
		{
			flag = true;
		}
		return flag;
	}

	/**
	 * @return Skip Logic Permissible Value Name
	 */
	public String getTaggedValuesString()
	{
		return readLine()[0].split(Constants.COLON)[1].trim();
	}

	/**
	 * @return taggedValueMap
	 */
	@Override
	public Map<String, String> getTagValueMap()
	{
		Map<String, String> tagValueMap = new HashMap<String, String>();
		for (String token : readLine())
		{
			if (token.contains(CategoryCSVConstants.FORM_TAG_VALUES)
					&& token.split(EQUAL_SIGN).length > 1)
			{
				tagValueMap.put(token.split(EQUAL_SIGN)[0].split(Constants.COLON)[1],
						token.split(EQUAL_SIGN)[1]);
			}
			else if (token.split(EQUAL_SIGN).length > 1)
			{
				tagValueMap.put(token.split(EQUAL_SIGN)[0], token.split(EQUAL_SIGN)[1]);
			}
		}
		return tagValueMap;
	}

	@Override
	public boolean isProcessorClassPresent()
	{
		boolean flag = false;
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		if (readLine() != null
				&& readLine()[0].trim().toLowerCase(locale).startsWith(
						CategoryCSVConstants.POST_PROCESSOR_CLASS.toLowerCase(locale)))
		{
			flag = true;
		}
		return flag;
	}

	public String getProcessorClass()
	{
		return readLine()[0].replace(CategoryCSVConstants.POST_PROCESSOR_CLASS, "").trim();

	}
	public boolean isLazylyLoadPvs()
	{

		boolean isLazy = false;
		for (String string : readLine())
		{
			if (string.toLowerCase().startsWith(CategoryCSVConstants.LAZY))
			{
				if("true".equalsIgnoreCase(string.split("=")[1]))
				{
					isLazy = true;
				}
			}
		}
		return isLazy;

	}


}