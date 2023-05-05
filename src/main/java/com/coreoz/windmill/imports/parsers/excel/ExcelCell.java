package com.coreoz.windmill.imports.parsers.excel;

import java.util.function.Function;

import org.apache.poi.ss.usermodel.CellType;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.NumberValue;
import com.coreoz.windmill.imports.NumberValueWithDefault;

class ExcelCell implements Cell {

	private final org.apache.poi.ss.usermodel.Cell excelCell;
	private final int columnIndex;
	private final boolean trimValue;

	public ExcelCell(int columnIndex, org.apache.poi.ss.usermodel.Cell excelCell, boolean trimValue) {
		this.excelCell = excelCell;
		this.columnIndex = columnIndex;
		this.trimValue = trimValue;
	}

	@Override
	public int columnIndex() {
		return columnIndex;
	}

	@Override
	public String asString() {
		if (excelCell == null) {
			// POI sometimes returns null when a cell is empty...
			return null;
		}

		// maybe in the future this needs to be replaced by:
		// DataFormatter formatter = new DataFormatter(); // to cache in thread locale?
		// FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		// String value = formatter.formatCellValue(cell, evaluator);
		if (excelCell.getCellType() == CellType.NUMERIC
			|| excelCell.getCellType() == CellType.FORMULA
			|| excelCell.getCellType() == CellType.BOOLEAN) {
			excelCell.setCellType(CellType.STRING);
		}
		return emptyToNullTrimmed(excelCell.getRichStringCellValue().getString(), trimValue);
	}

	@Override
	public String toString() {
		return asString();
	}

	@Override
	public NumberValue<Integer> asInteger() {
		return toNumber(Double::intValue, Integer::parseInt);
	}

	@Override
	public NumberValue<Long> asLong() {
		return toNumber(Double::longValue, Long::parseLong);
	}

	@Override
	public NumberValue<Float> asFloat() {
		return toNumber(Double::floatValue, Float::parseFloat);
	}

	@Override
	public NumberValue<Double> asDouble() {
		return toNumber(Function.identity(), Double::parseDouble);
	}

	// internal

	private<T> NumberValue<T> toNumber(Function<Double, T> cast, Function<String, T> valueParser) {
		return new NumberValueWithDefault<>(tryGetValue(cast), asString(), valueParser);
	}

	private<T> T tryGetValue(Function<Double, T> cast) {
		if (excelCell != null) {
			if(excelCell.getCellType() == CellType.NUMERIC
				|| excelCell.getCellType() == CellType.FORMULA) {
				return cast.apply(excelCell.getNumericCellValue());
			}
		}
		return null;
	}

	private static String emptyToNullTrimmed(String value, boolean shouldTrim) {
		return "".equals(value) ?
			null
			: (shouldTrim ? value.trim() : value);
	}

}
