package com.coreoz.windmill.imports.parsers.csv;

import java.util.function.Function;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.NumberValue;

class CsvCell implements Cell {

	private final int columnIndex;
	private final String value;

	public CsvCell(int columnIndex, String value) {
		this.columnIndex = columnIndex;
		this.value = value;
	}

	@Override
	public int columnIndex() {
		return columnIndex;
	}

	@Override
	public String asString() {
		return value;
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
		return new NumberValue<>(asString(), valueParser);
	}

}
