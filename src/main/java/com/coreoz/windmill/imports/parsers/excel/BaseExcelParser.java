package com.coreoz.windmill.imports.parsers.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Row;
import com.coreoz.windmill.streams.IteratorStreams;

import lombok.SneakyThrows;

public abstract class BaseExcelParser implements FileParser {

	private final String sheetName;
	private final Integer sheetIndex;
	private final boolean trimValues;

	public BaseExcelParser() {
		// by default, use the first sheet
		this(0);
	}

	public BaseExcelParser(String sheetName) {
		this(sheetName, null, false);
	}

	/**
	 * @param sheetIndex the sheet index, starts at 0
	 */
	public BaseExcelParser(Integer sheetIndex) {
		this(null, sheetIndex, false);
	}

	private BaseExcelParser(String sheetName, Integer sheetIndex, boolean trimValues) {
		if(sheetName == null && sheetIndex == null) {
			throw new IllegalArgumentException("Either the sheetName or the sheetIndex must be specified");
		}

		this.sheetName = sheetName;
		this.sheetIndex = sheetIndex;
		this.trimValues = trimValues;
	}

	protected abstract Workbook openWorkbook(InputStream sourceInputStream) throws IOException;

	/**
	 * Specify that cell values should be trimmed.
	 * If this option is enabled:
	 * <ul>
	 *   <li>
	 *     if a cell in the column "Name" contains "value ":
	 *     <code>row.cell("Name").asString()</code> will returns "value"
	 *   </li>
	 *   <li>
	 *     if a column name (so a cell in the first row) contains " Name ":
	 *     <code>row.cell("Name")</code> will enable to get values from this column
	 *   </li>
	 * </ul>
	 */
	public FileParser trimValues() {
		return new BaseExcelParser(sheetName, sheetIndex, true) {
			@Override
			protected Workbook openWorkbook(InputStream sourceInputStream) throws IOException {
				return BaseExcelParser.this.openWorkbook(sourceInputStream);
			}
		};
	}

	/**
	 * @throws IOException
	 */
	@SneakyThrows
	@Override
	public Stream<Row> parse(FileSource source) {
		Workbook workbook = openWorkbook(source.toInputStream());
		Sheet sheet = selectSheet(workbook);

		return IteratorStreams
			.stream(new ExcelRowIterator(sheet.rowIterator(), trimValues))
			.onClose(() -> IteratorStreams.close(workbook));
	}

	private Sheet selectSheet(Workbook workbook) {
		if(sheetIndex != null) {
			return workbook.getSheetAt(sheetIndex);
		}
		return workbook.getSheet(sheetName);
	}

}
