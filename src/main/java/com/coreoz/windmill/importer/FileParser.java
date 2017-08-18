package com.coreoz.windmill.importer;

import java.util.stream.Stream;

import com.coreoz.windmill.Row;

public interface FileParser {

	Stream<Row> parse(FileSource source);

}
