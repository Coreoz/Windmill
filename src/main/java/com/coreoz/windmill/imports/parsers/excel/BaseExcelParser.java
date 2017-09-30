package com.coreoz.windmill.imports.parsers.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.ImportRow;
import com.coreoz.windmill.utils.IteratorStreams;

import lombok.SneakyThrows;

public abstract class BaseExcelParser implements FileParser {

	private final String sheetName;
	private final Integer sheetIndex;

	public BaseExcelParser() {
		// by default, use the first sheet
		this(null, 0);
	}

	public BaseExcelParser(String sheetName) {
		this(sheetName, null);
	}

	/**
	 * @param sheetIndex the sheet index, starts at 0
	 */
	public BaseExcelParser(Integer sheetIndex) {
		this(null, sheetIndex);
	}

	private BaseExcelParser(String sheetName, Integer sheetIndex) {
		if(sheetName == null && sheetIndex == null) {
			throw new IllegalArgumentException("Either the sheetName or the sheetIndex must be specified");
		}

		this.sheetName = sheetName;
		this.sheetIndex = sheetIndex;
	}

	protected abstract Workbook openWorkbook(InputStream sourceInputStream) throws IOException;

	/**
	 * @throws IOException
	 */
	@SneakyThrows
	@Override
	public Stream<ImportRow> parse(FileSource source) {
		Workbook workbook = openWorkbook(source.toInputStream());
		Sheet sheet = selectSheet(workbook);

		return IteratorStreams
			.stream(new ExcelRowIterator(sheet.rowIterator()))
			.onClose(() -> IteratorStreams.close(workbook));
	}

	private Sheet selectSheet(Workbook workbook) {
		if(sheetIndex != null) {
			return workbook.getSheetAt(sheetIndex);
		}
		return workbook.getSheet(sheetName);
	}

}
