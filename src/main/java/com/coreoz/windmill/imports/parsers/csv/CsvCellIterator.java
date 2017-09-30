package com.coreoz.windmill.imports.parsers.csv;

import java.util.Iterator;

import com.coreoz.windmill.imports.Cell;

class CsvCellIterator implements Iterator<Cell> {

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
	public Cell next() {
		Cell cell = new CsvCell(rowIndex, csvRow[rowIndex]);
		rowIndex++;
		return cell;
	}

}
