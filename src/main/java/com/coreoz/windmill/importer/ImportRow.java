package com.coreoz.windmill.importer;

public interface ImportRow extends Iterable<ImportCell> {

	/**
	 * Returns the current row index, starts at 0
	 */
	int rowIndex();

	ImportCell cell(String columnName);

	ImportCell cell(Integer columnIndex);

}
