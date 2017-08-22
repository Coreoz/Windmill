package com.coreoz.windmill.importer.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Handles XLS files
 */
public final class BinaryExcelParser extends BaseExcelParser {

	@Override
	protected Workbook openWorkbook(InputStream sourceInputStream) throws IOException {
		return new HSSFWorkbook(sourceInputStream);
	}

}
