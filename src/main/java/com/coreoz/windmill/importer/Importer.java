package com.coreoz.windmill.importer;

import java.util.function.Function;

import com.coreoz.windmill.Row;

public class Importer<T> {

	private final Function<Row, T> rowMapper;

	public Importer(Function<Row, T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public FileParsing<T> parse(FileSource source) {
		return parse(source, ParserGuesser.guess(source));
	}

	public FileParsing<T> parse(FileSource source, FileParser fileParser) {
		return fileParser.parse(source, rowMapper);
	}

}
