package com.coreoz.windmill.importer.parser;

import java.util.Iterator;

import com.coreoz.windmill.Cell;
import com.coreoz.windmill.Cells;
import com.coreoz.windmill.Row;
import com.coreoz.windmill.importer.FileSchema;

public class ExcelRow implements Row {

	private final org.apache.poi.ss.usermodel.Row excelRow;
	private final FileSchema fileSchema;

	public ExcelRow(org.apache.poi.ss.usermodel.Row excelRow, FileSchema fileSchema) {
		this.excelRow = excelRow;
		this.fileSchema = fileSchema;
	}

	@Override
	public int index() {
		return excelRow.getRowNum();
	}

	@Override
	public Cell cell(String columnName) {
		return Cells.cell(columnName, fileSchema, this::cell);
	}

	@Override
	public Cell cell(Integer columnIndex) {
		return toCell(excelRow.getCell(columnIndex));
	}

	@Override
	public Iterator<Cell> iterator() {
		return cellIterator(excelRow);
	}

	// internal

	static Cell toCell(org.apache.poi.ss.usermodel.Cell excelCell) {
		if (excelCell == null) {
			return null;
		}

		return new ExcelCell(excelCell);
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
				return toCell(excelCellIterator.next());
			}
		};
	}

}
