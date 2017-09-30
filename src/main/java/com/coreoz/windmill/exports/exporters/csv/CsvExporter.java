package com.coreoz.windmill.exports.exporters.csv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import com.coreoz.windmill.exports.config.ExportMapping;
import com.opencsv.CSVWriter;

import lombok.SneakyThrows;

public class CsvExporter<T> {

	private final Iterable<T> rows;
	private final ExportMapping<T> mapping;
	private final ExportCsvConfig exportConfig;
	private CSVWriter csvWriter;

	public CsvExporter(Iterable<T> rows, ExportMapping<T> mapping, ExportCsvConfig exportConfig) {
		this.rows = rows;
		this.mapping = mapping;
		this.exportConfig = exportConfig;
	}

	/**
	 * Write the export file in an existing {@link OutputStream}.
	 *
	 * This {@link OutputStream} will not be closed automatically:
	 * it should be closed manually after this method is called.
	 *
	 * @throws IOException if anything can't be written.
	 */
	@SneakyThrows
	public OutputStream writeTo(OutputStream outputStream) {
		csvWriter = new CSVWriter(
			new OutputStreamWriter(outputStream, exportConfig.getCharset()),
			exportConfig.getSeparator(),
			exportConfig.getQuoteChar(),
			exportConfig.getEscapeChar(),
			exportConfig.getLineEnd()
		);
		writeRows();
		return outputStream;
	}

	/**
	 * @throws IOException if anything can't be written.
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		writeTo(byteOutputStream);
		return byteOutputStream.toByteArray();
	}

	// internals

	private void writeRows() {
		writeHeaderRow();

		for(T row : rows) {
			writeRow(row);
		}
	}

	private void writeHeaderRow() {
		List<String> headerColumn = mapping.headerColumns();
		if(!headerColumn.isEmpty()) {
			String[] csvRowValues = new String[headerColumn.size()];
			for (int i = 0; i < headerColumn.size(); i++) {
				csvRowValues[i] = stringValue(headerColumn.get(i));
			}
			csvWriter.writeNext(csvRowValues);
		}
	}

	@SneakyThrows
	private void writeRow(T row) {
		String[] csvRowValues = new String[mapping.columnsCount()];
		for (int i = 0; i < mapping.columnsCount(); i++) {
			csvRowValues[i] = stringValue(mapping.cellValue(i, row));
		}
		csvWriter.writeNext(csvRowValues);
		csvWriter.flush();
	}

	private String stringValue(final Object object) {
		if (object == null) {
			return "";
		}
		return object.toString();
	}

}
