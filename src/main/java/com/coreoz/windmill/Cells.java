package com.coreoz.windmill;

import java.util.function.Function;

import com.coreoz.windmill.importer.FileSchema;

public class Cells {

	public static Cell cell(String columnName, FileSchema fileSchema, Function<Integer, Cell> cellFromIndex) {
		Integer columnIndex = fileSchema.columnIndex(columnName);
		if (columnIndex == null) {
			return null;
		}

		return cellFromIndex.apply(columnIndex);
	}

}
