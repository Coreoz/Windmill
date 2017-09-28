package com.coreoz.windmill.importer.parsers.csv;

import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Stream;

import com.coreoz.windmill.importer.FileParser;
import com.coreoz.windmill.importer.FileSource;
import com.coreoz.windmill.importer.ImportRow;
import com.coreoz.windmill.utils.IteratorStreams;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

public class CsvParser implements FileParser {

	private final CsvParserConfiguration csvParserConfiguration;

	public CsvParser() {
		this(CsvParserConfiguration.builder().build());
	}

	public CsvParser(CsvParserConfiguration csvParserConfiguration) {
		this.csvParserConfiguration = csvParserConfiguration;
	}

	@Override
	public Stream<ImportRow> parse(FileSource source) {
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
