package com.coreoz.windmill.importer.parsers.excel;

import org.apache.poi.ss.usermodel.CellType;

import com.coreoz.windmill.importer.ImportCell;
import com.coreoz.windmill.importer.parsers.NumberParser;

public class ExcelImportCell implements ImportCell {

	private final org.apache.poi.ss.usermodel.Cell excelCell;

	public ExcelImportCell(org.apache.poi.ss.usermodel.Cell excelCell) {
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
		return richTextStringTrimmedValue();
	}

	private String trim(String value) {
		if (value == null) {
			return null;
		}
		String valueTrimed = value.trim();
		return "".equals(valueTrimed) ? null : valueTrimed;
	}

	@Override
	public String toString() {
		return asString();
	}

	@Override
	public Integer asInteger() {
		if (excelCell.getCellTypeEnum() == CellType.NUMERIC) {
			return (int) excelCell.getNumericCellValue();
		}
		return NumberParser.parseInt(richTextStringTrimmedValue(), false);
	}

	@Override
	public Long asLong() {
		if (excelCell.getCellTypeEnum() == CellType.NUMERIC) {
			return (long) excelCell.getNumericCellValue();
		}
		return NumberParser.parseLong(richTextStringTrimmedValue(), false);
	}

	@Override
	public Float asFloat() {
		if (excelCell.getCellTypeEnum() == CellType.NUMERIC) {
			return (float) excelCell.getNumericCellValue();
		}
		return NumberParser.parseFloat(richTextStringTrimmedValue(), false);
	}

	@Override
	public Double asDouble() {
		if (excelCell.getCellTypeEnum() == CellType.NUMERIC) {
			return (double) excelCell.getNumericCellValue();
		}
		return NumberParser.parseDouble(richTextStringTrimmedValue(), false);
	}

	private String richTextStringTrimmedValue() {
		return trim(excelCell.getRichStringCellValue().getString());
	}

}
