package com.coreoz.windmill.imports.parsers.csv;

import java.util.Iterator;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.FileSchema;
import com.coreoz.windmill.imports.Row;

public class CsvRow implements Row {

	private final int currentRowIndex;
	private final FileSchema fileSchema;
	private final String[] row;

	public CsvRow(int currentRowIndex, FileSchema fileSchema, String[] row) {
		this.currentRowIndex = currentRowIndex;
		this.fileSchema = fileSchema;
		this.row = row;
	}

	@Override
	public Iterator<Cell> iterator() {
		return new CsvCellIterator(row);
	}

	@Override
	public int rowIndex() {
		return currentRowIndex;
	}

	@Override
	public boolean columnExists(String columnName) {
		return fileSchema.columnExists(columnName);
	}

	@Override
	public Cell cell(String columnName) {
		return cell(fileSchema.columnIndex(columnName));
	}

	@Override
	public Cell cell(int columnIndex) {
		return new CsvCell(columnIndex, row.length > columnIndex ? row[columnIndex] : null);
	}

}
