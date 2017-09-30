package com.coreoz.windmill.imports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileSchema {

	private final List<Cell> firstRowCells;
	private final Map<String, Integer> columnNameToColumnIndex;

	public FileSchema(List<Cell> firstRowCells) {
		this.firstRowCells = firstRowCells;
		this.columnNameToColumnIndex = new HashMap<>();
	}

	public boolean columnExists(String columnName) {
		return columnIndexNullable(columnName) != null;
	}

	/**
	 * Returns the column index if it exists, else returns {@link Integer#MAX_VALUE}
	 */
	public int columnIndex(String columnName) {
		Integer columnIndex = columnIndexNullable(columnName);
		return columnIndex == null ? Integer.MAX_VALUE : columnIndex.intValue();
	}

	private Integer columnIndexNullable(String columnName) {
		if (columnNameToColumnIndex.containsKey(columnName)) {
			return columnNameToColumnIndex.get(columnName);
		}

		for (int columnIndex = 0; columnIndex < firstRowCells.size(); columnIndex++) {
			Cell cell = firstRowCells.get(columnIndex);
			if (Objects.equals(columnName, cell.asString())) {
				columnNameToColumnIndex.put(columnName, columnIndex);
				return columnIndex;
			}
		}

		// column does not exist
		columnNameToColumnIndex.put(columnName, null);
		return null;
	}

}
