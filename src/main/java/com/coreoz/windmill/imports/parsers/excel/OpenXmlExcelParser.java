package com.coreoz.windmill.imports.parsers.excel;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class OpenXmlExcelParser extends BaseExcelParser {

	/**
	 * Alias to <code>new OpenXmlExcelParser(0)</code>.
	 * The first sheet will be parsed.
	 */
	public OpenXmlExcelParser() {
	}

	/**
	 * @param sheetIndex the sheet index, starts at 0
	 */
	public OpenXmlExcelParser(Integer sheetIndex) {
		super(sheetIndex);
	}

	public OpenXmlExcelParser(String sheetName) {
		super(sheetName);
	}

	@Override
	protected Workbook openWorkbook(InputStream sourceInputStream) throws IOException {
		return new XSSFWorkbook(sourceInputStream);
	}

}
