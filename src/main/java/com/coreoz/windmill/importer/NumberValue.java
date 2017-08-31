package com.coreoz.windmill.importer;

import java.util.function.Function;

public class NumberValue<T> {

	private final T value;
	private final String stringValue;
	private final Function<String, T> valueParser;

	public NumberValue(T value, String stringValue, Function<String, T> valueParser) {
		this.value = value;
		this.stringValue = stringValue;
		this.valueParser = valueParser;
	}

	public boolean isNull() {
		return value == null || stringValue == null;
	}

	/**
	 * Returns the value, may be null if the cell is empty.
	 * See {@link #safeValue()} to avoid any exception during the parsing step.
	 * @throws NumberFormatException if the cell value is not a correct number
	 */
	public T value() {
		return value == null ? null : parseNumber(stringValue, valueParser, true);
	}

	/**
	 * Returns the value, may be null if the cell is empty
	 * or if the cell value could not be parsed into a number
	 * See {@link #value()} to raise an exception if the value parsing 
	 */
	public T safeValue() {
		return value == null ? null : parseNumber(stringValue, valueParser, false);
	}

	private static<T> T parseNumber(String value, Function<String, T> valueParser, boolean shouldThrowParsingError) {
		if (isNullOrEmpty(value)) {
			return null;
		}
		try {
			return valueParser.apply(value.replace(',', '.'));
		} catch (NumberFormatException e) {
			if(shouldThrowParsingError) {
				throw e;
			}
			return null;
		}
	}

	private static boolean isNullOrEmpty(String value) {
		return value == null || "".equals(value);
	}

}
