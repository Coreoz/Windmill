package com.coreoz.windmill.importer.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Handles XLS files
 */
public final class BinaryExcelParser extends BaseExcelParser {

	public BinaryExcelParser() {
	}

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
