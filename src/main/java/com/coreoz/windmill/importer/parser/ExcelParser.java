package com.coreoz.windmill.importer.parser;

import java.util.function.Function;

import com.coreoz.windmill.Row;
import com.coreoz.windmill.importer.FileParser;
import com.coreoz.windmill.importer.FileParsing;
import com.coreoz.windmill.importer.FileSource;

public class ExcelParser implements FileParser {

	@Override
	public <T> FileParsing<T> parse(FileSource source, Function<Row, T> rowMapper) {
		// TODO Aller récupérer le parser de la Cardibox
		return null;
	}

}
