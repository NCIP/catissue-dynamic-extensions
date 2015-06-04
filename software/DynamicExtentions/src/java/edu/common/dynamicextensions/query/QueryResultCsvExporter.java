package edu.common.dynamicextensions.query;	

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import au.com.bytecode.opencsv.CSVWriter;
import edu.common.dynamicextensions.nutility.IoUtil;

public class QueryResultCsvExporter implements QueryResultExporter {
	@Override
	public void export(String exportPath, Query query) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(exportPath);
			export(out, query);
		} catch (Exception e) {
			throw new RuntimeException("Error exporting CSV file", e);
		} finally {
			IoUtil.close(out);
		}		
	}

	@Override
	public void export(OutputStream out, Query query) {
		QueryResultData data = query.getData();
		data.setColumnLabelFormatter(new DefaultResultColLabelFormatter("_"));
		export(out, data);
	}

	@Override
	public void export(OutputStream out, QueryResultData result) {
		CSVWriter csvWriter = null;

		try {
			csvWriter = getCsvWriter(out);			
			csvWriter.writeNext(result.getColumnLabels());			
			for (int i = 0; i < result.rowCount(); ++i) {
				csvWriter.writeNext(result.stringifiedRow(i));
			}						
		} catch (Exception e) {
			throw new RuntimeException("Error writing query result data to CSV file", e);
		} finally {
			IoUtil.close(csvWriter);
		}		
	}
	
	private CSVWriter getCsvWriter(OutputStream out) {
		return new CSVWriter(new OutputStreamWriter(out));
	}
}