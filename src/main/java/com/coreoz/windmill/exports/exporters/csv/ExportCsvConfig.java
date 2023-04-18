package com.coreoz.windmill.exports.exporters.csv;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExportCsvConfig {

	/** the file charset */
	@Builder.Default private final Charset charset = StandardCharsets.UTF_8;
	/** The delimiter to use for separating entries */
	@Builder.Default private final char separator = ICSVParser.DEFAULT_SEPARATOR;
	/** The character to use for quoted elements */
	@Builder.Default private final char quoteChar = ICSVParser.DEFAULT_QUOTE_CHARACTER;
	/** The character to use for escaping quoteChar or escapeChar */
	@Builder.Default private final char escapeChar = ICSVParser.DEFAULT_ESCAPE_CHARACTER;
	/** The line feed terminator to use */
	@Builder.Default private final String lineEnd = CSVWriter.DEFAULT_LINE_END;
	/** The boolean to use for applying or not optional wrapping quotes  */
	@Builder.Default private final boolean applyQuotesToAll = true;
	/** The UTF8 bom used to enable auto encoding detection for excel */
	@Builder.Default private final String bom = Character.toString((char) 0xFEFF);
	/** The boom should be used only for excel usage */
	@Builder.Default private final boolean isBomEnabled = false;
}
