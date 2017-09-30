package com.coreoz.windmill.imports.parsers.csv;

import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Stream;

import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Row;
import com.coreoz.windmill.utils.IteratorStreams;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

public class CsvParser implements FileParser {

	private final CsvParserConfig csvParserConfiguration;

	public CsvParser() {
		this(CsvParserConfig.builder().build());
	}

	public CsvParser(CsvParserConfig csvParserConfiguration) {
		this.csvParserConfiguration = csvParserConfiguration;
	}

	@Override
	public Stream<Row> parse(FileSource source) {
		InputStreamReader csvStreamReader = new InputStreamReader(source.toInputStream(), csvParserConfiguration.getCharset());
		Iterator<String[]> csvRowIterator = new CSVReaderBuilder(csvStreamReader)
			.withCSVParser(
				new CSVParserBuilder()
					.withEscapeChar(csvParserConfiguration.getEscapeChar())
					.withFieldAsNull(csvParserConfiguration.getNullFieldIndicator())
					.withIgnoreLeadingWhiteSpace(csvParserConfiguration.isIgnoreLeadingWhiteSpace())
					.withIgnoreQuotations(csvParserConfiguration.isIgnoreQuotations())
					.withQuoteChar(csvParserConfiguration.getQuoteChar())
					.withSeparator(csvParserConfiguration.getSeparator())
					.withStrictQuotes(csvParserConfiguration.isStrictQuotes())
					.build()
			)
			.withKeepCarriageReturn(csvParserConfiguration.isKeepCr())
			.build()
			.iterator();

		return IteratorStreams
			.stream(new CsvRowIterator(csvRowIterator))
			.onClose(() -> IteratorStreams.close(csvStreamReader));
	}

}
