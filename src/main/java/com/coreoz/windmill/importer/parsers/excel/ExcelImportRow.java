package com.coreoz.windmill.importer.parsers.excel;

import java.util.Iterator;

import com.coreoz.windmill.importer.ImportCell;
import com.coreoz.windmill.importer.ImportCells;
import com.coreoz.windmill.importer.FileSchema;
import com.coreoz.windmill.importer.ImportRow;

public class ExcelImportRow implements ImportRow {

	private final org.apache.poi.ss.usermodel.Row excelRow;
	private final FileSchema fileSchema;

	public ExcelImportRow(org.apache.poi.ss.usermodel.Row excelRow, FileSchema fileSchema) {
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
		return toCell(excelRow.getCell(columnIndex));
	}

	@Override
	public Iterator<ImportCell> iterator() {
		return cellIterator(excelRow);
	}

	// internal

	static ImportCell toCell(org.apache.poi.ss.usermodel.Cell excelCell) {
		if (excelCell == null) {
			return null;
		}

		return new ExcelImportCell(excelCell);
	}

	static Iterator<ImportCell> cellIterator(org.apache.poi.ss.usermodel.Row excelRow) {
		return new Iterator<ImportCell>() {
			private final Iterator<org.apache.poi.ss.usermodel.Cell> excelCellIterator = excelRow.iterator();

			@Override
			public boolean hasNext() {
				return excelCellIterator.hasNext();
			}

			@Override
			public ImportCell next() {
				return toCell(excelCellIterator.next());
			}
		};
	}

}
