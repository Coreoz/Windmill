package com.coreoz.windmill.exporter.exporters.csv;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExportCsvConfig {

	/** The delimiter to use for separating entries */
	@Builder.Default private final char separator = ICSVParser.DEFAULT_SEPARATOR;
	/** The character to use for quoted elements */
	@Builder.Default private final char quoteChar = ICSVParser.DEFAULT_QUOTE_CHARACTER;
	/** The character to use for escaping quoteChar or escapeChar */
	@Builder.Default private final char escapeChar = ICSVParser.DEFAULT_ESCAPE_CHARACTER;
	/** The line feed terminator to use */
	@Builder.Default private final String lineEnd = CSVWriter.DEFAULT_LINE_END;

}
