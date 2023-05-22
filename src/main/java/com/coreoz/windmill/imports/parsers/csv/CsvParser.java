package com.coreoz.windmill.imports.parsers.csv;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Stream;

import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Row;
import com.coreoz.windmill.utils.BomCharset;
import com.coreoz.windmill.utils.IteratorStreams;
import com.coreoz.windmill.utils.PeekingInputStream;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

import lombok.SneakyThrows;

public class CsvParser implements FileParser {

	private final CsvParserConfig csvParserConfiguration;

	public CsvParser() {
		this(CsvParserConfig.builder().build());
	}

	public CsvParser(CsvParserConfig csvParserConfiguration) {
		this.csvParserConfiguration = csvParserConfiguration;
	}

    /**
     * Remove the bom from the file if it exists
     * @param peekingStream
     * @param fallbackCharset
     * @return The charset found
     */
    @SneakyThrows
    private BomCharset cleanFilestreamBom(PeekingInputStream peekingStream, BomCharset fallbackCharset) {
        byte[] firstFileBytes = peekingStream.peekMaxBomLength();
        BomCharset bomCharset = BomCharset.detectCharset(firstFileBytes, fallbackCharset);
        // Try to remove the bom
        peekingStream.peekedStream().skip(bomCharset.bomLength());

        return bomCharset;
    }

    @SneakyThrows
	@Override
	public Stream<Row> parse(FileSource source) {
        PeekingInputStream peekingStream = new PeekingInputStream(source.toInputStream());

        BomCharset charset = cleanFilestreamBom(peekingStream, csvParserConfiguration.getFallbackCharset());

		InputStreamReader csvStreamReader = new InputStreamReader(peekingStream.peekedStream(), charset.getCharset());
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
