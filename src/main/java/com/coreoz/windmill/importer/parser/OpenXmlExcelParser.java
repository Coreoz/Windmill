package com.coreoz.windmill.importer.parser;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.coreoz.windmill.importer.FileSource;

public final class OpenXmlExcelParser extends BaseExcelParser {

	@Override
	protected Workbook openWorkbook(FileSource source) throws IOException {
		return new XSSFWorkbook(source.toInputStream());
	}

}
