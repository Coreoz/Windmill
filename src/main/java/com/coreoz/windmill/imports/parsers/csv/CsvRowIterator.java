package com.coreoz.windmill.imports.parsers.csv;

import java.util.Iterator;
import java.util.stream.Collectors;

import com.coreoz.windmill.imports.FileSchema;
import com.coreoz.windmill.imports.Row;
import com.coreoz.windmill.utils.IteratorStreams;

class CsvRowIterator implements Iterator<Row> {

	private final Iterator<String[]> csvRowIterator;
	private FileSchema fileSchema;
	private int currentRowIndex;

	public CsvRowIterator(Iterator<String[]> csvRowIterator) {
		this.csvRowIterator = csvRowIterator;
		this.fileSchema = null;
		this.currentRowIndex = 0;
	}

	@Override
	public boolean hasNext() {
		return csvRowIterator.hasNext();
	}

	@Override
	public Row next() {
		String[] nextCsvRow = csvRowIterator.next();

		if (fileSchema == null) {
			fileSchema = new FileSchema(
				IteratorStreams
					.stream(new CsvCellIterator(nextCsvRow))
					.collect(Collectors.toList())
			);
		}

		CsvRow csvRow = new CsvRow(currentRowIndex, fileSchema, nextCsvRow);
		currentRowIndex++;

		return csvRow;
	}

}
