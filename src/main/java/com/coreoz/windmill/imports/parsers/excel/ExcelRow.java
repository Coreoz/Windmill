package com.coreoz.windmill.imports.parsers.excel;

import java.util.Iterator;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.FileSchema;
import com.coreoz.windmill.imports.Row;

public class ExcelRow implements Row {

	private final org.apache.poi.ss.usermodel.Row excelRow;
	private final FileSchema fileSchema;

	public ExcelRow(org.apache.poi.ss.usermodel.Row excelRow, FileSchema fileSchema) {
		this.excelRow = excelRow;
		this.fileSchema = fileSchema;
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
		return toCell(columnIndex, excelRow.getCell(columnIndex));
	}

	@Override
	public Iterator<Cell> iterator() {
		return cellIterator(excelRow);
	}

	// internal

	private static Cell toCell(Integer columnIndex, org.apache.poi.ss.usermodel.Cell excelCell) {
		return new ExcelCell(columnIndex, excelCell);
	}

	static Iterator<Cell> cellIterator(org.apache.poi.ss.usermodel.Row excelRow) {
		return new Iterator<Cell>() {
			private final Iterator<org.apache.poi.ss.usermodel.Cell> excelCellIterator = excelRow.iterator();

			@Override
			public boolean hasNext() {
				return excelCellIterator.hasNext();
			}

			@Override
			public Cell next() {
				org.apache.poi.ss.usermodel.Cell excelCell = excelCellIterator.next();
				return toCell(excelCell.getColumnIndex(), excelCell);
			}
		};
	}

}
