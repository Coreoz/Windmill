package com.coreoz.windmill;

public interface Cell {

	/**
	 * Returns the current cell column index, starts at 0
	 */
	int columnIndex();

	String asString();

}
