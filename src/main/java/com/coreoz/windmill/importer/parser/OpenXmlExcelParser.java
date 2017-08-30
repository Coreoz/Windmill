package com.coreoz.windmill.importer.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class OpenXmlExcelParser extends BaseExcelParser {

	public OpenXmlExcelParser() {
	}

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
