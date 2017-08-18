package com.coreoz.windmill.importer.parser;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.coreoz.windmill.importer.FileSource;

/**
 * Handles XLS files
 */
public final class BinaryExcelParser extends BaseExcelParser {

	@Override
	protected Workbook openWorkbook(FileSource source) throws IOException {
		return new HSSFWorkbook(source.toInputStream());
	}

}
