package com.coreoz.windmill.importer;

import java.util.stream.Stream;

/**
 * A Parser should be thread-safe
 */
public interface FileParser {

	Stream<ImportRow> parse(FileSource source);

}
