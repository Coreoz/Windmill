package com.coreoz.windmill.importer.parsers.csv;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.stream.Stream;

import com.coreoz.windmill.importer.FileParser;
import com.coreoz.windmill.importer.FileSource;
import com.coreoz.windmill.importer.ImportRow;
import com.coreoz.windmill.utils.IteratorStreams;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

public class CsvParser implements FileParser {

	private final Charset charset;
	private final CSVParserBuilder csvParserConfiguration;

	public CsvParser() {
		this(StandardCharsets.ISO_8859_1);
	}

	public CsvParser(Charset charset) {
		this(charset, new CSVParserBuilder());
	}

	public CsvParser(Charset charset, CSVParserBuilder csvParserConfiguration) {
		this.charset = charset;
		this.csvParserConfiguration = csvParserConfiguration;
	}

	@Override
	public Stream<ImportRow> parse(FileSource source) {
		InputStreamReader csvStreamReader = new InputStreamReader(source.toInputStream(), charset);
		Iterator<String[]> csvRowIterator = new CSVReaderBuilder(csvStreamReader)
			.withCSVParser(csvParserConfiguration.build())
			.build()
			.iterator();

		return IteratorStreams
			.stream(new CsvRowIterator(csvRowIterator))
			.onClose(() -> IteratorStreams.close(csvStreamReader));
	}

}
