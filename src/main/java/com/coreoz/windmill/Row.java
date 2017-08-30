package com.coreoz.windmill;

public interface Row extends Iterable<Cell> {

	/**
	 * Returns the current row index, starts at 0
	 */
	int rowIndex();

	Cell cell(String columnName);

	Cell cell(Integer columnIndex);

}
