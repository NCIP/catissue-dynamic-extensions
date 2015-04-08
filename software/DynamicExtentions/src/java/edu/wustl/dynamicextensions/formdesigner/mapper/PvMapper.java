
package edu.wustl.dynamicextensions.formdesigner.mapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.common.dynamicextensions.domain.nui.DataType;
import edu.common.dynamicextensions.domain.nui.PermissibleValue;
import edu.common.dynamicextensions.domain.nui.PvDataSource;
import edu.common.dynamicextensions.domain.nui.PvDataSource.Ordering;
import edu.common.dynamicextensions.domain.nui.PvVersion;
import edu.wustl.dynamicextensions.formdesigner.utility.CSDConstants;
import edu.wustl.dynamicextensions.formdesigner.utility.PermissibleValueComparator;

public class PvMapper {

	@SuppressWarnings("unchecked")
	public static PvDataSource propertiesToPvDataSource(Properties controlProperties) throws IOException {
		PvDataSource pvDataSource = new PvDataSource();

		DataType dataType = controlProperties.getDataType("dataType");
		if (dataType != null) {
			pvDataSource.setDataType(dataType);
		} else {
			pvDataSource.setDataType(DataType.STRING);
		}

		String pvOrder = controlProperties.getString("pvOrder");
		if ("ASC".equalsIgnoreCase(pvOrder)) {
			pvDataSource.setOrdering(Ordering.ASC);
		} else if ("DESC".equalsIgnoreCase(pvOrder)) {
			pvDataSource.setOrdering(Ordering.DESC);
		} else if ("NONE".equalsIgnoreCase(pvOrder)) {
			pvDataSource.setOrdering(Ordering.NONE);
		}

		PvVersion pvVersion = new PvVersion();
		List<PermissibleValue> pvList = new ArrayList<PermissibleValue>();

		// process permissible values from grid.
		Map<String, Object> defaultPvMap = controlProperties.getMap("defaultPv");
		if (defaultPvMap != null) {
			pvVersion.setDefaultValue(propertiesToPv(new Properties(defaultPvMap)));
		}

		Map<String, Object> pvs = controlProperties.getMap("pvs");
		if (pvs != null) {

			for (String key : pvs.keySet()) {
				Map<String, Object> pvProperties = (Map<String, Object>) pvs.get(key);
				Properties properties = new Properties(pvProperties);
				pvList.add(propertiesToPv(properties));
			}
		}

		pvList.addAll(getPvsFromFile(controlProperties.getString("pvFile")));
		pvVersion.setActivationDate(new Date());
		pvVersion.setPermissibleValues(pvList);
		pvDataSource.setPvVersions(Arrays.asList(pvVersion));
		return pvDataSource;
	}

	/**
	 * @param controlProperties
	 * @param pvList
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static List<PermissibleValue> getPvsFromFile(String pvFile) throws FileNotFoundException, IOException,
			NumberFormatException {
		// process permissible values from uploaded file.
		//String pvFile = controlProperties.getString("pvFile");
		List<PermissibleValue> pvList = new ArrayList<PermissibleValue>();
		if (pvFile != null) {
			CSVReader csvReader = new CSVReader(new FileReader(new File(pvFile)));
			String[] csvData;
			while ((csvData = csvReader.readNext()) != null) {
				pvList.add(getPvFromFileData(csvData));
			}
		}
		return pvList;
	}

	public static File getPvFile(String fileName, List<PermissibleValue> pvs) throws IOException {
		File file = new File(fileName);

		CSVWriter csvWriter = new CSVWriter(new FileWriter(file));

		csvWriter.writeNext(CSDConstants.PV_HEADERS);
		for (PermissibleValue pv : pvs) {
			csvWriter.writeNext(getPvTuple(pv));
		}
		csvWriter.flush();
		csvWriter.close();

		return file;
	}

	/**
	 * @param csvData
	 * @return
	 * @throws NumberFormatException
	 */
	private static PermissibleValue getPvFromFileData(String[] csvData) throws NumberFormatException {
		PermissibleValue pv = new PermissibleValue();
		if (!csvData[0].isEmpty()) {
			pv.setValue(csvData[0]);
		}
		// add validation check for is a number
		if (!csvData[1].isEmpty()) {
			pv.setNumericCode(Long.parseLong(csvData[1]));
		}
		if (!csvData[2].isEmpty()) {
			pv.setOptionName(csvData[2]);
		}
		if (!csvData[3].isEmpty()) {
			pv.setDefinitionSource(csvData[3]);
		}
		if (!csvData[4].isEmpty()) {
			pv.setConceptCode(csvData[4]);
		}
		if ((pv.getOptionName() == null) || (pv.getOptionName().trim().isEmpty())) {
			pv.setOptionName(csvData[0]);
		}
		return pv;
	}

	private static String[] getPvTuple(PermissibleValue pv) throws NumberFormatException {

		String[] csvData = new String[5];

		if (pv.getValue() == null) {
			csvData[0] = "";
		} else {
			csvData[0] = pv.getValue();

		}
		// add validation check for is a number
		if (pv.getNumericCode() == null) {
			csvData[1] = "";
		} else {
			csvData[1] = pv.getNumericCode().toString();
		}

		if (pv.getOptionName() == null) {
			csvData[2] = "";
		} else {
			csvData[2] = pv.getOptionName();
		}

		if (pv.getDefinitionSource() == null) {
			csvData[3] = "";
		} else {
			csvData[3] = pv.getDefinitionSource();
		}

		if (pv.getConceptCode() == null) {
			csvData[4] = "";
		} else {
			csvData[4] = pv.getConceptCode();
		}
		return csvData;
	}

	/**
	 * @param properties
	 * @return
	 */
	private static PermissibleValue propertiesToPv(Properties properties) {
		PermissibleValue pv = new PermissibleValue();
		String conceptCode = properties.getString("conceptCode");
		if (conceptCode != null) {
			pv.setConceptCode(conceptCode);
		}
		String definitionSource = properties.getString("definitionSource");
		if (definitionSource != null) {
			pv.setDefinitionSource(definitionSource);
		}
		Long numericCode = properties.getLong("numericCode");
		if (numericCode != null) {
			pv.setNumericCode(numericCode);
		}
		String definition = properties.getString("definition");
		if (definition != null) {
			pv.setOptionName(definition);
		}
		String value = properties.getString("value");
		if (value != null) {
			pv.setValue(value.trim());
		}
		return pv;
	}

	/**
	 * @param pvDataSource
	 * @return
	 */
	public static void pVDataSourcetoProperties(PvDataSource pvDataSource, Properties controlProps) {
		Map<String, Object> pvMap = new TreeMap<String, Object>();
		String pvKey = "pv_";
		Integer pvKeyNum = 0;
		List<PermissibleValue> pvs = pvDataSource.getPermissibleValues(new Date());
		if (pvDataSource.getOrdering() == Ordering.ASC) {
			controlProps.setProperty("pvOrder", "ASC");
			Collections.sort(pvs, new PermissibleValueComparator());
		} else if (pvDataSource.getOrdering() == Ordering.DESC) {
			controlProps.setProperty("pvOrder", "DESC");
			Collections.sort(pvs, new PermissibleValueComparator());
			Collections.reverse(pvs);
		}
		for (PermissibleValue pv : pvs) {

			pvMap.put(pvKey + pvKeyNum, pvToProperties(pv));
			pvKeyNum++;
		}
		PermissibleValue defaultPv = pvDataSource.getDefaultValue(new Date());
		if (defaultPv != null) {
			controlProps.setProperty("defaultPv", pvToProperties(defaultPv));
		}
		controlProps.setProperty("pvs", pvMap);
		controlProps.setProperty("dataType", pvDataSource.getDataType().toString());
	}

	/**
	 * @param pv
	 * @return
	 */
	private static Map<String, Object> pvToProperties(PermissibleValue pv) {
		Map<String, Object> pvProperties = new HashMap<String, Object>();
		pvProperties.put("value", pv.getValue() == null ? "" : pv.getValue());
		pvProperties.put("numericCode", pv.getNumericCode() == null ? "" : pv.getNumericCode());
		pvProperties.put("conceptCode", pv.getConceptCode() == null ? "" : pv.getConceptCode());
		pvProperties.put("definitionSource", pv.getDefinitionSource() == null ? "" : pv.getDefinitionSource());
		pvProperties.put("definition", pv.getOptionName() == null ? "" : pv.getOptionName());
		return pvProperties;
	}

}
