package edu.common.dynamicextensions.util.templategenerator;

import java.io.File;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.bulkoperator.metadata.BulkOperationClass;
import edu.wustl.bulkoperator.metadata.BulkOperationMetaData;
import edu.wustl.bulkoperator.util.BulkOperationException;

public interface TemplateGenerator
{
	BulkOperationClass getBulkOperationClass();
	BulkOperationMetaData mergeStaticTemplate(String xmlFilePath,
	String mappingXML) throws BulkOperationException;
	String createCSVTemplate(BulkOperationMetaData bulkMetaData, File file)
	throws DynamicExtensionsSystemException;
}
