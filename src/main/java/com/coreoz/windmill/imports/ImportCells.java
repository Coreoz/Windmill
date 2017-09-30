package com.coreoz.windmill.imports;

import java.util.function.Function;

public final class ImportCells {

	public static ImportCell cell(String columnName, FileSchema fileSchema, Function<Integer, ImportCell> cellFromIndex) {
		Integer columnIndex = fileSchema.columnIndex(columnName);
		if (columnIndex == null) {
			return null;
		}

		return cellFromIndex.apply(columnIndex);
	}

}
