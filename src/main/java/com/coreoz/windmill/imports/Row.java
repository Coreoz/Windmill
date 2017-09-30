package com.coreoz.windmill.imports;

public interface Row extends Iterable<Cell> {

	/**
	 * Returns the current row index, starts at 0
	 */
	int rowIndex();

	/**
	 * Check if the column has been defined in the first row.
	 */
	boolean columnExists(String columnName);

	/**
	 * Returns a cell by its name.
	 * The name of the column is the value of the column in the first row of the spreadsheet.
	 * Never returns null.
	 */
	Cell cell(String columnName);

	/**
	 * Returns a cell by its index. Never returns null.
	 *
	 * @param columnIndex The column index, starts at 0
	 */
	Cell cell(int columnIndex);

}
