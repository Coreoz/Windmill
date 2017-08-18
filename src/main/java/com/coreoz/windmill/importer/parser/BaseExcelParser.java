package com.coreoz.windmill.importer.parser;

import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.coreoz.windmill.Row;
import com.coreoz.windmill.importer.FileParser;
import com.coreoz.windmill.importer.FileSchema;
import com.coreoz.windmill.importer.FileSource;

import lombok.SneakyThrows;

// TODO add options to select the tab to import
public abstract class BaseExcelParser implements FileParser {

	protected abstract Workbook openWorkbook(FileSource source) throws IOException;

	/**
	 * @throws IOException
	 */
	@SneakyThrows
	@Override
	public Stream<Row> parse(FileSource source) {
		Workbook workbook = openWorkbook(source);
		Sheet sheet = workbook.getSheetAt(0);

		return stream(new ExcelRowIterator(sheet.rowIterator()))
			.onClose(() -> close(workbook));
	}

	@SneakyThrows
	public static void close(AutoCloseable closeable) {
		closeable.close();
	}

	  /**
	   * Returns a sequential {@link Stream} of the remaining contents of {@code iterator}. Do not use
	   * {@code iterator} directly after passing it to this method.
	   */
	  public static <T> Stream<T> stream(Iterator<T> iterator) {
	    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
	  }

	private static class ExcelRowIterator implements Iterator<Row> {

		private final Iterator<org.apache.poi.ss.usermodel.Row> rowIterator;
		private FileSchema fileSchema;

		public ExcelRowIterator(Iterator<org.apache.poi.ss.usermodel.Row> rowIterator) {
			this.rowIterator = rowIterator;
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
					StreamSupport
						.stream(Spliterators.spliteratorUnknownSize(ExcelRow.cellIterator(nextExcelRow), 0), false)
						.collect(Collectors.toList())
				);
			}

			return new ExcelRow(nextExcelRow, fileSchema);
		}

	}

}
