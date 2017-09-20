package com.coreoz.windmill.importer;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import com.coreoz.windmill.importer.parsers.csv.CsvParser;
import com.coreoz.windmill.importer.parsers.excel.BinaryExcelParser;
import com.coreoz.windmill.importer.parsers.excel.OpenXmlExcelParser;

public class ParserGuesser {

	private static final FileParser OPEN_XML_EXCEL_PARSER = new OpenXmlExcelParser();
	private static final FileParser BINARY_EXCEL_PARSER = new BinaryExcelParser();
	private static final FileParser CSV_PARSER = new CsvParser();

	private static final byte[] ZIP_FIRST_BYTES = {0x50, 0x4B};
	private static final byte[] XLS_FIRST_BYTES = fromIntArray(new int[]{ 0xD0, 0xCF, 0x11, 0xE0, 0xA1, 0xB1, 0x1A, 0xE1 });

	public static FileParser guess(FileSource fileSource) {
		if(Arrays.equals(ZIP_FIRST_BYTES, fileSource.peek(ZIP_FIRST_BYTES.length))) {
			return OPEN_XML_EXCEL_PARSER;
		}
		if(Arrays.equals(XLS_FIRST_BYTES, fileSource.peek(XLS_FIRST_BYTES.length))) {
			return BINARY_EXCEL_PARSER;
		}
		// if the file type cannot be guess, it should be a text file (it does not have magic bytes)
		return CSV_PARSER;
	}

	private static byte[] fromIntArray(int[] values) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < values.length; ++i) {
			baos.write(values[i]);
		}

		return baos.toByteArray();
	}

}
