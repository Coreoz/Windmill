package com.coreoz.windmill.importer.parsers.csv;

import java.util.Iterator;

import com.coreoz.windmill.importer.ImportCell;

class CsvCellIterator implements Iterator<ImportCell> {

	private final String[] csvRow;
	private int rowIndex;

	public CsvCellIterator(String[] csvRow) {
		this.csvRow = csvRow;
		this.rowIndex = 0;
	}

	@Override
	public boolean hasNext() {
		return rowIndex < csvRow.length;
	}

	@Override
	public ImportCell next() {
		ImportCell cell = new CsvImportCell(rowIndex, csvRow[rowIndex]);
		rowIndex++;
		return cell;
	}

}
