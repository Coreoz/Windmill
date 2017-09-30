package com.coreoz.windmill.imports.parsers.csv;

import java.util.function.Function;

import com.coreoz.windmill.imports.ImportCell;
import com.coreoz.windmill.imports.NumberValue;
import com.coreoz.windmill.utils.Strings;

public class CsvImportCell implements ImportCell {

	private final int columnIndex;
	private final String value;

	public CsvImportCell(int columnIndex, String value) {
		this.columnIndex = columnIndex;
		this.value = value;
	}

	@Override
	public int columnIndex() {
		return columnIndex;
	}

	@Override
	public String asString() {
		return Strings.emptyToNull(value);
	}

	@Override
	public NumberValue<Integer> asInteger() {
		return toNumber(Integer::parseInt);
	}

	@Override
	public NumberValue<Long> asLong() {
		return toNumber(Long::parseLong);
	}

	@Override
	public NumberValue<Float> asFloat() {
		return toNumber(Float::parseFloat);
	}

	@Override
	public NumberValue<Double> asDouble() {
		return toNumber(Double::parseDouble);
	}

	private<T> NumberValue<T> toNumber(Function<String, T> valueParser) {
		return new NumberValue<>(value, valueParser);
	}

}
