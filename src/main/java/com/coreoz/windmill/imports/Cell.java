package com.coreoz.windmill.imports;

public interface Cell {

	/**
	 * Returns the current cell column index, starts at 0
	 */
	int columnIndex();

	/**
	 * Returns the {@link String} value of the cell.
	 * If the cell is empty, null is returned.
	 */
	String asString();

	NumberValue<Integer> asInteger();

	NumberValue<Long> asLong();

	NumberValue<Float> asFloat();

	NumberValue<Double> asDouble();

}
