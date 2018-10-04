package com.coreoz.windmill;

import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.files.FileTypeGuesser;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Parsers;
import com.coreoz.windmill.imports.Row;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Entry point to parse/export CSV and Excel files
 */
public final class Windmill {

	/**
	 * Parse a file using a {@link FileParser} corresponding
	 * to the file type determined by the {@link FileTypeGuesser}
	 * @throws IOException
	 */
	@Deprecated
	public static Stream<Row> parse(FileSource fileSource) {
		return Importer.builder()
				.source(fileSource)
				.withoutHeaders()
				.stream();
	}

	/**
	 * Parse a file with a dedicated {@link FileParser}. See {@link Parsers} for a list of all parsers
	 * @throws IOException
	 */
	@Deprecated
	public static Stream<Row> parse(FileSource fileSource, FileParser fileParser) {
		return Importer.builder()
				.source(fileSource)
				.parser(fileParser)
				.withoutHeaders()
				.stream();
	}

	public static Importer.InitialState importer() {
		return Importer.builder();
	}

	/**
	 * Prepare a file export
	 * @param rows The rows that will be exported to a file
	 */
	@Deprecated
	public static<T> void export(Iterable<T> rows) {
		throw new UnsupportedOperationException("Use exporter() method instead!");
	}

	public static <T> Exporter.InitialState<T> exporter() {
		return Exporter.builder();
	}

}
