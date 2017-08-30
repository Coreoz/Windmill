package com.coreoz.windmill.importer.parsers;

import java.util.function.Function;

public class NumberParser {

	public static Integer parseInt(String value, boolean shouldThrowParsingError) {
		return parseNumber(value, Integer::parseInt, shouldThrowParsingError);
	}

	public static Long parseLong(String value, boolean shouldThrowParsingError) {
		return parseNumber(value, Long::parseLong, shouldThrowParsingError);
	}

	public static Float parseFloat(String value, boolean shouldThrowParsingError) {
		return parseNumber(value, Float::parseFloat, shouldThrowParsingError);
	}

	public static Double parseDouble(String value, boolean shouldThrowParsingError) {
		return parseNumber(value, Double::parseDouble, shouldThrowParsingError);
	}

	private static<T> T parseNumber(String value, Function<String, T> parser, boolean shouldThrowParsingError) {
		if (isNullOrEmpty(value)) {
			return null;
		}
		try {
			return parser.apply(value.replace(',', '.'));
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
