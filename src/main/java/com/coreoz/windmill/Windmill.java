package com.coreoz.windmill;

import java.io.IOException;
import java.util.stream.Stream;

import com.coreoz.windmill.exporter.config.ExportConfig;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.files.FileTypeGuesser;
import com.coreoz.windmill.importer.FileParser;
import com.coreoz.windmill.importer.ImportRow;
import com.coreoz.windmill.importer.Parsers;

/**
 * Entry point to parse/export CSV and Excel files
 */
public final class Windmill {

	/**
	 * Parse a file using a {@link FileParser} corresponding
	 * to the file type determined by the {@link FileTypeGuesser}
	 * @throws IOException
	 */
	public static Stream<ImportRow> parse(FileSource fileSource) {
		return parse(fileSource, Parsers.forType(FileTypeGuesser.guess(fileSource)));
	}

	/**
	 * Parse a file with a dedicated {@link FileParser}. See {@link Parsers} for a list of all parsers
	 * @throws IOException
	 */
	public static Stream<ImportRow> parse(FileSource fileSource, FileParser fileParser) {
		return fileParser.parse(fileSource);
	}

	/**
	 * Prepare a file export
	 * @param rows The rows that will be exported to a file
	 */
	public static<T> ExportConfig<T> export(Iterable<T> rows) {
		return new ExportConfig<>(rows);
	}

}
