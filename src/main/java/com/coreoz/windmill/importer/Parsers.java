package com.coreoz.windmill.importer;

import com.coreoz.windmill.files.FileType;
import com.coreoz.windmill.importer.parsers.csv.CsvParser;
import com.coreoz.windmill.importer.parsers.csv.CsvParserConfig;
import com.coreoz.windmill.importer.parsers.excel.BinaryExcelParser;
import com.coreoz.windmill.importer.parsers.excel.OpenXmlExcelParser;

public final class Parsers {

	private static final FileParser OPEN_XML_EXCEL_PARSER = new OpenXmlExcelParser();
	private static final FileParser BINARY_EXCEL_PARSER = new BinaryExcelParser();
	private static final FileParser CSV_PARSER = new CsvParser();
	
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
	
	public static FileParser csv() {
		return CSV_PARSER;
	}
	
	public static FileParser csv(CsvParserConfig csvParserConfiguration) {
		return new CsvParser(csvParserConfiguration);
	}
	
	public static FileParser xlsx() {
		return OPEN_XML_EXCEL_PARSER;
	}
	
	public static FileParser xlsx(String sheetName) {
		return new OpenXmlExcelParser(sheetName);
	}
	
	public static FileParser xlsx(Integer sheetIndex) {
		return new OpenXmlExcelParser(sheetIndex);
	}
	
	public static FileParser xls() {
		return BINARY_EXCEL_PARSER;
	}
	
	public static FileParser xls(String sheetName) {
		return new BinaryExcelParser(sheetName);
	}
	
	public static FileParser xls(Integer sheetIndex) {
		return new BinaryExcelParser(sheetIndex);
	}
	
}
