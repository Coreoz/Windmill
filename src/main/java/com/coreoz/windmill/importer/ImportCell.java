package com.coreoz.windmill.importer;

public interface ImportCell {

	/**
	 * Returns the current cell column index, starts at 0
	 */
	int columnIndex();

	String asString();

	NumberValue<Integer> asInteger();

	NumberValue<Long> asLong();

	NumberValue<Float> asFloat();

	NumberValue<Double> asDouble();

}
