package com.coreoz.windmill.imports;

import com.coreoz.windmill.files.FileType;
import com.coreoz.windmill.imports.parsers.csv.CsvParser;
import com.coreoz.windmill.imports.parsers.csv.CsvParserConfig;
import com.coreoz.windmill.imports.parsers.excel.BinaryExcelParser;
import com.coreoz.windmill.imports.parsers.excel.OpenXmlExcelParser;

public final class Parsers {

	private static final OpenXmlExcelParser OPEN_XML_EXCEL_PARSER = new OpenXmlExcelParser();
	private static final BinaryExcelParser BINARY_EXCEL_PARSER = new BinaryExcelParser();
	private static final CsvParser CSV_PARSER = new CsvParser();

	public static FileParser forType(FileType fileType) {
		if (fileType == FileType.ZIP) {
			return OPEN_XML_EXCEL_PARSER;
		}
		if (fileType == FileType.CFBF) {
			return BINARY_EXCEL_PARSER;
		}
		// if the file type cannot be guess, it should be a text file
		return CSV_PARSER;
	}

	public static CsvParser csv() {
		return CSV_PARSER;
	}

	public static CsvParser csv(CsvParserConfig csvParserConfiguration) {
		return new CsvParser(csvParserConfiguration);
	}

	public static OpenXmlExcelParser xlsx() {
		return OPEN_XML_EXCEL_PARSER;
	}

	public static OpenXmlExcelParser xlsx(String sheetName) {
		return new OpenXmlExcelParser(sheetName);
	}

	public static OpenXmlExcelParser xlsx(Integer sheetIndex) {
		return new OpenXmlExcelParser(sheetIndex);
	}

	public static BinaryExcelParser xls() {
		return BINARY_EXCEL_PARSER;
	}

	public static BinaryExcelParser xls(String sheetName) {
		return new BinaryExcelParser(sheetName);
	}

	public static BinaryExcelParser xls(Integer sheetIndex) {
		return new BinaryExcelParser(sheetIndex);
	}

}
