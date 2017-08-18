package com.coreoz.windmill.importer.parser;

import org.apache.poi.ss.usermodel.CellType;

import com.coreoz.windmill.Cell;

public class ExcelCell implements Cell {

	private final org.apache.poi.ss.usermodel.Cell excelCell;

	public ExcelCell(org.apache.poi.ss.usermodel.Cell excelCell) {
		this.excelCell = excelCell;
	}

	@Override
	public int columnIndex() {
		return excelCell.getColumnIndex();
	}

	@Override
	public String asString() {
		if (excelCell.getCellTypeEnum() == CellType.NUMERIC) {
			excelCell.setCellType(CellType.STRING);
			return trim(excelCell.getStringCellValue());
		}
		return trim(excelCell.getRichStringCellValue().getString());
	}

	private String trim(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	@Override
	public String toString() {
		return asString();
	}

}
