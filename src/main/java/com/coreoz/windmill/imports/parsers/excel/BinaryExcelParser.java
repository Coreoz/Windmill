package com.coreoz.windmill.imports.parsers.excel;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Handles XLS files
 */
public final class BinaryExcelParser extends BaseExcelParser {

	/**
	 * Alias to <code>new BinaryExcelParser(0)</code>.
	 * The first sheet will be parsed.
	 */
	public BinaryExcelParser() {
	}

	/**
	 * @param sheetIndex the sheet index, starts at 0
	 */
	public BinaryExcelParser(Integer sheetIndex) {
		super(sheetIndex);
	}

	public BinaryExcelParser(String sheetName) {
		super(sheetName);
	}

	@Override
	protected Workbook openWorkbook(InputStream sourceInputStream) throws IOException {
		return new HSSFWorkbook(sourceInputStream);
	}

}
