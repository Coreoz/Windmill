package com.coreoz.windmill.importer;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import com.coreoz.windmill.importer.parsers.excel.BinaryExcelParser;
import com.coreoz.windmill.importer.parsers.excel.OpenXmlExcelParser;

public class ParserGuesser {

	private static byte[] ZIP_FIRST_BYTES = {0x50, 0x4B};
	private static byte[] XLS_FIRST_BYTES = fromIntArray(new int[]{ 0xD0, 0xCF, 0x11, 0xE0, 0xA1, 0xB1, 0x1A, 0xE1 });

	public static FileParser guess(FileSource fileSource) {
		if(Arrays.equals(ZIP_FIRST_BYTES, fileSource.peek(ZIP_FIRST_BYTES.length))) {
			return new OpenXmlExcelParser();
		}
		if(Arrays.equals(XLS_FIRST_BYTES, fileSource.peek(XLS_FIRST_BYTES.length))) {
			return new BinaryExcelParser();
		}
		throw new IllegalArgumentException("Parser could be detected from " + fileSource);
	}

	private static byte[] fromIntArray(int[] values) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < values.length; ++i) {
			baos.write(values[i]);
		}

		return baos.toByteArray();
	}

}
