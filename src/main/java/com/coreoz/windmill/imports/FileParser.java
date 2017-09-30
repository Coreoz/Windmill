package com.coreoz.windmill.imports;

import java.util.stream.Stream;

import com.coreoz.windmill.files.FileSource;

/**
 * Represents a file parser.
 * Implementations should be thread-safe.
 */
public interface FileParser {

	/**
	 * Starts to parse a file.
	 * @param source The file input source
	 * @return A stream containing the file rows <strong>that should be closed</strong> when it is done reading
	 */
	Stream<ImportRow> parse(FileSource source);

}
