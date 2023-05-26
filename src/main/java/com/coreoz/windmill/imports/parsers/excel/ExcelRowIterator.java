package com.coreoz.windmill.imports.parsers.excel;

import java.util.Iterator;
import java.util.stream.Collectors;

import com.coreoz.windmill.imports.FileSchema;
import com.coreoz.windmill.imports.Row;
import com.coreoz.windmill.streams.IteratorStreams;

class ExcelRowIterator implements Iterator<Row> {

	private final Iterator<org.apache.poi.ss.usermodel.Row> rowIterator;
	private final boolean trimValues;
	private FileSchema fileSchema;

	public ExcelRowIterator(Iterator<org.apache.poi.ss.usermodel.Row> rowIterator, boolean trimValues) {
		this.rowIterator = rowIterator;
		this.trimValues = trimValues;
		this.fileSchema = null;
	}

	@Override
	public boolean hasNext() {
		return rowIterator.hasNext();
	}

	@Override
	public Row next() {
		org.apache.poi.ss.usermodel.Row nextExcelRow = rowIterator.next();

		if (fileSchema == null) {
			fileSchema = new FileSchema(
				IteratorStreams
					.stream(ExcelRow.cellIterator(nextExcelRow, trimValues))
					.collect(Collectors.toList())
			);
		}

		return new ExcelRow(nextExcelRow, fileSchema, trimValues);
	}

}
