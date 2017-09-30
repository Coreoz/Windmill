package com.coreoz.windmill.imports;

public interface ImportRow extends Iterable<ImportCell> {

	/**
	 * Returns the current row index, starts at 0
	 */
	int rowIndex();

	/**
	 * Returns a cell by its name.
	 * The name of the column is the value of the column in the first row of the spreadsheet.
	 */
	ImportCell cell(String columnName);

	/**
	 * Returns a cell by its index
	 * 
	 * @param columnIndex The column index, starts at 0
	 */
	ImportCell cell(Integer columnIndex);

}
