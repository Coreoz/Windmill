package com.coreoz.windmill.imports.parsers.excel;

import java.util.Iterator;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.FileSchema;
import com.coreoz.windmill.imports.Row;

class ExcelRow implements Row {

	private final org.apache.poi.ss.usermodel.Row excelRow;
	private final FileSchema fileSchema;
	private final boolean trimValues;

	public ExcelRow(org.apache.poi.ss.usermodel.Row excelRow, FileSchema fileSchema, boolean trimValues) {
		this.excelRow = excelRow;
		this.fileSchema = fileSchema;
		this.trimValues = trimValues;
	}

	@Override
	public int rowIndex() {
		return excelRow.getRowNum();
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
		return toCell(columnIndex, excelRow.getCell(columnIndex), trimValues);
	}

	@Override
	public Iterator<Cell> iterator() {
		return cellIterator(excelRow, trimValues);
	}

	// internal

	private static Cell toCell(Integer columnIndex, org.apache.poi.ss.usermodel.Cell excelCell, boolean trimValues) {
		return new ExcelCell(columnIndex, excelCell, trimValues);
	}

	static Iterator<Cell> cellIterator(org.apache.poi.ss.usermodel.Row excelRow, boolean trimValues) {
		return new Iterator<Cell>() {
			private final Iterator<org.apache.poi.ss.usermodel.Cell> excelCellIterator = excelRow.iterator();

			@Override
			public boolean hasNext() {
				return excelCellIterator.hasNext();
			}

			@Override
			public Cell next() {
				org.apache.poi.ss.usermodel.Cell excelCell = excelCellIterator.next();
				return toCell(excelCell.getColumnIndex(), excelCell, trimValues);
			}
		};
	}

}
