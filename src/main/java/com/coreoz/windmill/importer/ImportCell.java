package com.coreoz.windmill.importer;

public interface ImportCell {

	/**
	 * Returns the current cell column index, starts at 0
	 */
	int columnIndex();

	String asString();

	Integer asInteger();

	Long asLong();

	Float asFloat();

	Double asDouble();

}
