package com.coreoz.windmill.imports.parsers.excel;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.coreoz.windmill.imports.FileSchema;
import com.coreoz.windmill.imports.ImportCell;
import com.coreoz.windmill.imports.ImportCells;
import com.coreoz.windmill.imports.ImportRow;

public class ExcelImportRow implements ImportRow {

	private final Row excelRow;
	private final FileSchema fileSchema;

	public ExcelImportRow(Row excelRow, FileSchema fileSchema) {
		this.excelRow = excelRow;
		this.fileSchema = fileSchema;
	}

	@Override
	public int rowIndex() {
		return excelRow.getRowNum();
	}

	@Override
	public ImportCell cell(String columnName) {
		return ImportCells.cell(columnName, fileSchema, this::cell);
	}

	@Override
	public ImportCell cell(Integer columnIndex) {
		return toCell(columnIndex, excelRow.getCell(columnIndex));
	}

	@Override
	public Iterator<ImportCell> iterator() {
		return cellIterator(excelRow);
	}

	// internal

	private static ImportCell toCell(Integer columnIndex, Cell excelCell) {
		return new ExcelImportCell(columnIndex, excelCell);
	}

	static Iterator<ImportCell> cellIterator(Row excelRow) {
		return new Iterator<ImportCell>() {
			private final Iterator<Cell> excelCellIterator = excelRow.iterator();

			@Override
			public boolean hasNext() {
				return excelCellIterator.hasNext();
			}

			@Override
			public ImportCell next() {
				Cell cell = excelCellIterator.next();
				return toCell(cell.getColumnIndex(), cell);
			}
		};
	}

}
