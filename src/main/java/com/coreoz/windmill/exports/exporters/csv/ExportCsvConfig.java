package com.coreoz.windmill.exports.exporters.csv;

import com.coreoz.windmill.files.BomCharset;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExportCsvConfig {

	/** the file charset
	 * Will use bom if possible
	 * or use a BomCharset version without a BOM, for instance BomCharset.UTF_8_NO_BOM
	*/
	@Builder.Default private final BomCharset charset = BomCharset.UTF_8;
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
}
