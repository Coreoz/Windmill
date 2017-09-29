package com.coreoz.windmill.exporter.exporters.csv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import com.coreoz.windmill.exporter.config.ExportMapping;
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
	 * @throws IOException if anything can't be written.
	 */
	@SneakyThrows
	public void writeTo(OutputStream outputStream) {
		csvWriter = new CSVWriter(
			new OutputStreamWriter(outputStream),
			exportConfig.getSeparator(),
			exportConfig.getQuoteChar(),
			exportConfig.getEscapeChar(),
			exportConfig.getLineEnd()
		);
		writeRows();
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
