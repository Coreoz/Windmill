package com.coreoz.windmill;

import java.io.IOException;
import java.util.stream.Stream;

import com.coreoz.windmill.importer.FileParser;
import com.coreoz.windmill.importer.FileSource;
import com.coreoz.windmill.importer.ParserGuesser;

public class Windmill {

	/**
	 * @throws IOException
	 */
	public static Stream<Row> parse(FileSource fileSource) {
		return parse(fileSource, ParserGuesser.guess(fileSource));
	}

	/**
	 * @throws IOException
	 */
	public static Stream<Row> parse(FileSource fileSource, FileParser fileParser) {
		return fileParser.parse(fileSource);
	}

}
