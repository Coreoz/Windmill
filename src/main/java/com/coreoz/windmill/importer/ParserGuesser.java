package com.coreoz.windmill.importer;

import org.assertj.core.util.Objects;

import com.coreoz.windmill.importer.parser.ExcelParser;

class ParserGuesser {

	private static byte[] ZIP_FIRST_BYTES = {0x50, 0x4B};

	static FileParser guess(FileSource fileSource) {
		if(Objects.areEqualArrays(ZIP_FIRST_BYTES, fileSource.peek(2))) {
			return new ExcelParser();
		}
		throw new IllegalArgumentException("Parser could be detected from " + fileSource);
	}

}
