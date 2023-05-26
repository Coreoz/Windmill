package com.coreoz.windmill.imports.parsers.csv;

import com.coreoz.windmill.files.BomCharset;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Row;
import com.coreoz.windmill.streams.IteratorStreams;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import lombok.SneakyThrows;

import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Stream;

public class CsvParser implements FileParser {

	private final CsvParserConfig csvParserConfiguration;

	public CsvParser() {
		this(CsvParserConfig.builder().build());
	}

	public CsvParser(CsvParserConfig csvParserConfiguration) {
		this.csvParserConfiguration = csvParserConfiguration;
	}

    @SneakyThrows
	@Override
	public Stream<Row> parse(FileSource source) {
        BomCharset charset = handleFileStreamBom(source, csvParserConfiguration.getFallbackCharset());
		InputStreamReader csvStreamReader = new InputStreamReader(source.toInputStream(), charset.getCharset());
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

	/**
	 * Detect and remove the BOM from the file stream if it exists
	 * @return The charset found
	 */
	@SneakyThrows
	private BomCharset handleFileStreamBom(FileSource source, BomCharset fallbackCharset) {
		byte[] firstFileBytes = source.peek(BomCharset.maxBomLength());
		BomCharset bomCharset = BomCharset.detectCharset(firstFileBytes, fallbackCharset);
		// Try to remove the bom
		source.toInputStream().skip(bomCharset.bomLength());

		return bomCharset;
	}
}
