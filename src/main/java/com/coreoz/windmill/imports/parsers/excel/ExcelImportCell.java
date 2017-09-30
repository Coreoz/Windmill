package com.coreoz.windmill.imports.parsers.excel;

import java.util.function.Function;

import org.apache.poi.ss.usermodel.CellType;

import com.coreoz.windmill.imports.ImportCell;
import com.coreoz.windmill.imports.NumberValue;
import com.coreoz.windmill.imports.NumberValueWithDefault;
import com.coreoz.windmill.utils.Strings;

public class ExcelImportCell implements ImportCell {

	private final org.apache.poi.ss.usermodel.Cell excelCell;
	private final int columnIndex;

	public ExcelImportCell(int columnIndex, org.apache.poi.ss.usermodel.Cell excelCell) {
		this.excelCell = excelCell;
		this.columnIndex = columnIndex;
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
		if (excelCell.getCellTypeEnum() == CellType.NUMERIC) {
			excelCell.setCellType(CellType.STRING);
			return Strings.emptyToNull(excelCell.getStringCellValue());
		}
		return richTextStringTrimmedValue();
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

	private<T> NumberValue<T> toNumber(Function<Double, T> cast, Function<String, T> valueParser) {
		return new NumberValueWithDefault<>(tryGetValue(cast), asString(), valueParser);
	}

	private<T> T tryGetValue(Function<Double, T> cast) {
		if (excelCell != null && excelCell.getCellTypeEnum() == CellType.NUMERIC) {
			return cast.apply(excelCell.getNumericCellValue());
		}
		return null;
	}

	private String richTextStringTrimmedValue() {
		return Strings.emptyToNull(excelCell.getRichStringCellValue().getString());
	}

}
