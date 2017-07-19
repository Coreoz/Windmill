package com.coreoz.windmill.importer;

import java.util.function.Function;

import com.coreoz.windmill.Row;

public interface FileParser {

	<T> FileParsing<T> parse(FileSource source, Function<Row, T> rowMapper);

}
