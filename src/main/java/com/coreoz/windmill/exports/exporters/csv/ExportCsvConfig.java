package com.coreoz.windmill.exports.exporters.csv;

import java.util.Collections;
import java.util.Set;

import com.coreoz.windmill.files.BomCharset;
import com.opencsv.ICSVParser;

import com.opencsv.ICSVWriter;
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
	@Builder.Default private final String lineEnd = ICSVWriter.DEFAULT_LINE_END;
	/** The boolean to use for applying or not optional wrapping quotes  */
	@Builder.Default private final boolean applyQuotesToAll = true;

	/** If true, values starting with =, +, -, @, \t, \r will be prefixed with ' to prevent CSV formula injection */
	@Builder.Default private final boolean sanitizeFormulas = true;
	/** The field names for which formula sanitization should be disabled */
	@Builder.Default private final Set<String> fieldNamesExcludedFromSanitization = Collections.emptySet();

	public static class ExportCsvConfigBuilder {
		public ExportCsvConfigBuilder disableFormulasSanitization() {
			this.sanitizeFormulas$value = false;
			this.sanitizeFormulas$set = true;
			return this;
		}

		public ExportCsvConfigBuilder disableFormulasSanitizationForFields(String... fieldNames) {
			this.fieldNamesExcludedFromSanitization$value = Set.of(fieldNames);
			this.fieldNamesExcludedFromSanitization$set = true;
			return this;
		}
	}
}
