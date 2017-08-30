package com.coreoz.windmill.importer;

import java.util.stream.Stream;

public interface FileParser {

	Stream<ImportRow> parse(FileSource source);

}
