package com.coreoz.windmill.exporter.config;

import java.util.List;

public interface ExportMapping<T> {

	/**
	 * Returns the header columns, if empty, there will be no header
	 */
	List<String> headerColumns();
	int columnsCount();
	Object cellValue(int columnIndex, T row);

}
