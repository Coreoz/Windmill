package com.coreoz.windmill.importer.parsers.csv;

import java.util.Iterator;
import java.util.stream.Collectors;

import com.coreoz.windmill.importer.FileSchema;
import com.coreoz.windmill.importer.ImportRow;
import com.coreoz.windmill.utils.IteratorStreams;

public class CsvRowIterator implements Iterator<ImportRow> {

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
	public ImportRow next() {
		String[] nextCsvRow = csvRowIterator.next();

		if (fileSchema == null) {
			fileSchema = new FileSchema(
				IteratorStreams
					.stream(new CsvCellIterator(nextCsvRow))
					.collect(Collectors.toList())
			);
		}

		CsvImportRow importRow = new CsvImportRow(currentRowIndex, fileSchema, nextCsvRow);
		currentRowIndex++;

		return importRow;
	}

}
