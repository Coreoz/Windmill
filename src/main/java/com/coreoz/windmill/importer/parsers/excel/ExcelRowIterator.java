package com.coreoz.windmill.importer.parsers.excel;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;

import com.coreoz.windmill.importer.FileSchema;
import com.coreoz.windmill.importer.ImportRow;
import com.coreoz.windmill.utils.IteratorStreams;

class ExcelRowIterator implements Iterator<ImportRow> {

	private final Iterator<Row> rowIterator;
	private FileSchema fileSchema;

	public ExcelRowIterator(Iterator<Row> rowIterator) {
		this.rowIterator = rowIterator;
		this.fileSchema = null;
	}

	@Override
	public boolean hasNext() {
		return rowIterator.hasNext();
	}

	@Override
	public ImportRow next() {
		Row nextExcelRow = rowIterator.next();

		if (fileSchema == null) {
			fileSchema = new FileSchema(
				IteratorStreams
					.stream(ExcelImportRow.cellIterator(nextExcelRow))
					.collect(Collectors.toList())
			);
		}

		return new ExcelImportRow(nextExcelRow, fileSchema);
	}

}
