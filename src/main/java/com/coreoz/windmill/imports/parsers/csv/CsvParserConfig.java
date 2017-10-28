package com.coreoz.windmill.imports.parsers.csv;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.opencsv.ICSVParser;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CsvParserConfig {

	/** the file charset */
	@Builder.Default private final Charset charset = StandardCharsets.ISO_8859_1;
	/** if the reader built will keep or discard carriage returns */
	@Builder.Default private final boolean keepCr = false;
	/** the delimiter to use for separating entries */
	@Builder.Default private final char separator = ICSVParser.DEFAULT_SEPARATOR;
	/** the character to use for quoted elements */
	@Builder.Default private final char quoteChar = ICSVParser.DEFAULT_QUOTE_CHARACTER;
	/** the character to use for escaping a separator or quote */
	@Builder.Default private final char escapeChar = ICSVParser.DEFAULT_ESCAPE_CHARACTER;
	/** if true, characters outside the quotes are ignored. */
	@Builder.Default private final boolean strictQuotes = ICSVParser.DEFAULT_STRICT_QUOTES;
	/** if true, white space in front of a quote in a field is ignored */
	@Builder.Default private final boolean ignoreLeadingWhiteSpace = ICSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE;
	/** if true, quotations are ignored */
	@Builder.Default private final boolean ignoreQuotations = ICSVParser.DEFAULT_IGNORE_QUOTATIONS;
	/** set to what should be considered a null field */
	@Builder.Default private final CSVReaderNullFieldIndicator nullFieldIndicator = CSVReaderNullFieldIndicator.BOTH;

}
