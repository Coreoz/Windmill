package com.coreoz.windmill.exports.exporters.csv;

import com.coreoz.windmill.Exporter;
import com.coreoz.windmill.exports.mapping.ExportMapping;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvExporter<T> implements Exporter<T> {

	private final ExportMapping<T> mapping;
	private final ExportCsvConfig exportConfig;
	private CSVWriter csvWriter;
	private ByteArrayOutputStream intermediate;

	public CsvExporter(ExportMapping<T> mapping, ExportCsvConfig exportConfig) {
		this.mapping = mapping;
		this.exportConfig = exportConfig;
	}

	@SneakyThrows
	public CsvExporter<T> writeRow(T row) {
		String[] csvRowValues = new String[mapping.columnsCount()];
		for (int i = 0; i < mapping.columnsCount(); i++) {
			Object value = ObjectUtils.defaultIfNull(mapping.cellValue(i, row), "");
			csvRowValues[i] = String.valueOf(value);
		}

		getWriter().writeNext(csvRowValues, exportConfig.isApplyQuotesToAll());
		getWriter().flush();

		return this;
	}

	@Override
	public CsvExporter<T> writeRows(Iterable<T> rows) {
		writeHeaderRow();

		for(T row : rows) {
			writeRow(row);
		}

		return this;
	}

	@Override
	@SneakyThrows
	public void writeInto(OutputStream outputStream) {
		if (csvWriter == null) {
			this.csvWriter = initializeWriter(outputStream);
		} else {
			outputStream.write(intermediate.toByteArray());
		}
	}

	private CSVWriter initializeWriter(OutputStream outputStream) {
		return new CSVWriter(
				new OutputStreamWriter(outputStream, exportConfig.getCharset()),
				exportConfig.getSeparator(),
				exportConfig.getQuoteChar(),
				exportConfig.getEscapeChar(),
				exportConfig.getLineEnd()
		);
	}

	private void writeHeaderRow() {
		List<String> headerColumn = mapping.headerColumns();
		if (!headerColumn.isEmpty()) {
			String[] csvRowValues = new String[headerColumn.size()];
			for (int i = 0; i < headerColumn.size(); i++) {
				String value = ObjectUtils.defaultIfNull(headerColumn.get(i), "");
				csvRowValues[i] = value;
			}

			getWriter().writeNext(csvRowValues,exportConfig.isApplyQuotesToAll());
		}
	}

	private CSVWriter getWriter() {
		if (csvWriter == null) {
			// initialize intermediate buffer for written rows
			intermediate = new ByteArrayOutputStream();
			csvWriter = initializeWriter(intermediate);
		}

		return csvWriter;
	}
}
