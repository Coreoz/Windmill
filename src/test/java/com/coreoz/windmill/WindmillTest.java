package com.coreoz.windmill;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.coreoz.windmill.exports.config.ExportConfig;
import com.coreoz.windmill.exports.config.ExportHeaderMapping;
import com.coreoz.windmill.exports.config.ExportMapping;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.files.ParserGuesserTest;
import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.Parsers;
import com.coreoz.windmill.imports.Row;

import lombok.Value;

/**
 * Integration tests for Windmill
 */
public class WindmillTest {

	@Test
	public void should_export_as_xlsx_with_header() {
		byte[] xlsxExport = exportBase()
			.withHeaderMapping(exportHeaderMapping())
			.asExcel()
			.toByteArray();

		tryParseHeaderFile(FileSource.of(xlsxExport));
	}

	@Test
	public void should_export_as_csv_with_header() {
		byte[] csvExport = exportBase()
			.withHeaderMapping(exportHeaderMapping())
			.asCsv()
			.toByteArray();

		tryParseHeaderFile(FileSource.of(csvExport));
	}

	@Test
	public void should_export_as_xlsx_no_header() {
		byte[] xlsxExport = exportBase()
			.withNoHeaderMapping(exportNoHeaderMapping())
			.asExcel()
			.toByteArray();

		tryParseNoHeaderFile(FileSource.of(xlsxExport));
	}

	@Test
	public void should_export_as_csv_with_no_header() {
		byte[] csvExport = exportBase()
			.withNoHeaderMapping(exportNoHeaderMapping())
			.asCsv()
			.toByteArray();

		tryParseNoHeaderFile(FileSource.of(csvExport));
	}

	@Test
	public void should_export_excel_data_starting_from_a_non_origin_point() {
		byte[] xlsxExport = exportBase()
			.withNoHeaderMapping(exportNoHeaderMapping())
			// we are using an existing file to write the new data,
			// else POI will simply ignore the first empty rows and the test will be biased
			.asExcel(ExportExcelConfig.fromWorkbook(loadFile("/import.xlsx")).build("Feuil1").withOrigin(6, 3))
			.toByteArray();

		// check that the first rows are not modified
		try (Stream<Row> rowStream = Windmill.parse(FileSource.of(xlsxExport))) {
			List<Import> result = rowStream
				.skip(1)
				.limit(2)
				.map(this::parsingFunction)
				.collect(Collectors.toList());

			assertThat(result).containsExactlyElementsOf(data());
		}

		try (Stream<Row> rowStream = Windmill.parse(FileSource.of(xlsxExport))) {
			List<Import> result = rowStream.skip(3).map(row -> Import.of(
				row.cell(6).asString(),
				row.cell(7).asString(),
				row.cell(8).asString(),
				row.cell(9).asString(),
				row.cell(10).asInteger().value(),
				row.cell(11).asDouble().value()
			))
			.collect(Collectors.toList());

			assertThat(result).containsExactlyElementsOf(data());
		}
	}

	@Test
	public void should_export_as_csv_with_custom() {
		byte[] csvExport = exportBase()
			.withMapping(new ExportMapping<WindmillTest.Import>() {
				@Override
				public List<String> headerColumns() {
                    return Arrays.asList("a", "b", "c", "d", "Integer number", "Double number");
				}

				@Override
				public int columnsCount() {
                    return 6;
				}

				@Override
				public Object cellValue(int columnIndex, Import row) {
                    switch(columnIndex) {
                        case 0:
                            return row.getA();
                        case 1:
                            return row.getB();
                        case 2:
                            return row.getC();
                        case 3:
                            return row.getD();
                        case 4:
                            return row.getIntegerNumber();
                        case 5:
                            return row.getDoubleNumber();
                        default:
                            return "";
                    }
				}
            })
			.asCsv()
			.toByteArray();

        tryParseHeaderFile(FileSource.of(csvExport));
	}

	@Test
	public void should_parse_xls() {
		tryParseFile("/import.xls");
	}

	@Test
	public void should_parse_xlsx() {
		tryParseFile("/import.xlsx");
	}

	@Test
	public void should_parse_csv() {
		tryParseFile("/import.csv");
	}

	@Test
	public void import_should_not_return_null_cell_xlsx() {
		checkInexistantCell("/import.xlsx");
	}

	@Test
	public void import_should_not_return_null_cell_csv() {
		checkInexistantCell("/import.csv");
	}

	@Test
	public void should_parse_xlsx_with_formula_as_string() throws Exception {
		assertThat(parseFormulaFile().cell(2).asString()).isEqualTo("3");
	}

	@Test
	public void should_parse_xlsx_with_formula_as_integer() throws Exception {
		assertThat(parseFormulaFile().cell(2).asInteger().value()).isEqualTo(3);
	}

	@Test
	public void should_parse_xlsx_with_trimmed_values() {
		List<Import> result = Parsers
			.xlsx()
			.trimValues()
			.parse(loadFile("/import.xlsx"))
			.skip(2)
			.map(parseNamedRow())
			.collect(Collectors.toList());

		assertThat(result).containsExactlyElementsOf(Arrays.asList(
			Import.of("String val", null, "3 String", "FALSE", 54564, 0.25)
		));
	}

	// utils

	private List<Import> data() {
		return Arrays.asList(
			Import.of("String value 1", "2", "String 3", "TRUE", null, 5.45),
			Import.of("String val", null, "3 String ", "FALSE",54564, 0.25)
		);
	}

	private ExportConfig<Import> exportBase() {
		return Windmill.export(data());
	}

	private ExportHeaderMapping<Import> exportHeaderMapping() {
		return new ExportHeaderMapping<Import>()
			.add("a", Import::getA)
			.add("b", Import::getB)
			.add("c", Import::getC)
			.add("d", Import::getD)
			.add("Integer number", Import::getIntegerNumber)
			.add("Double number", Import::getDoubleNumber);
	}

	private List<Function<Import, ?>> exportNoHeaderMapping() {
		return Arrays.asList(
			Import::getA,
			Import::getB,
			Import::getC,
			Import::getD,
			Import::getIntegerNumber,
			Import::getDoubleNumber
		);
	}

	private FileSource loadFile(String fileName) {
		return FileSource.of(ParserGuesserTest.class.getResourceAsStream(fileName));
	}

	private void tryParseFile(String fileName) {
		tryParseHeaderFile(loadFile(fileName));
	}

	private void tryParseHeaderFile(FileSource fileSource) {
		List<Import> result = Windmill
			.parse(fileSource)
			// do not parse the header line
			.skip(1)
			.map(parseNamedRow())
			.collect(Collectors.toList());

		assertThat(result).containsExactlyElementsOf(data());
	}

	private Function<? super Row, ? extends Import> parseNamedRow() {
		return row -> Import.of(
			row.cell("a").asString(),
			row.cell("b").asString(),
			row.cell("c").asString(),
			row.cell("d").asString(),
			row.cell("Integer number").asInteger().value(),
			row.cell("Double number").asDouble().value()
		);
	}

	private void tryParseNoHeaderFile(FileSource fileSource) {
		try (Stream<Row> rowStream = Windmill.parse(fileSource)) {
			List<Import> result = rowStream.map(this::parsingFunction)
			.collect(Collectors.toList());

			assertThat(result).containsExactlyElementsOf(data());
		}
	}

	private Import parsingFunction(Row row) {
		return Import.of(
			row.cell(0).asString(),
			row.cell(1).asString(),
			row.cell(2).asString(),
			row.cell(3).asString(),
			row.cell(4).asInteger().value(),
			row.cell(5).asDouble().value()
		);
	}

	private Row parseFormulaFile() {
		return Parsers
			.xlsx()
			.trimValues()
			.parse(loadFile("/formula.xlsx"))
			.findAny()
			.get();
	};

	private void checkInexistantCell(String fileName) {
		Row firstRow = Windmill
			.parse(loadFile(fileName))
			.skip(1)
			.findFirst()
			.get();

		Cell inexistantCellByName = firstRow.cell("INEXISTANT COLUMN");
		Cell inexistantCellByIndex = firstRow.cell(97844);

		assertThat(firstRow.columnExists("INEXISTANT COLUMN")).isFalse();

		assertThat(inexistantCellByName).isNotNull();
		assertThat(inexistantCellByName.asString()).isNull();
		assertThat(inexistantCellByName.asLong().isNull()).isTrue();

		assertThat(inexistantCellByIndex).isNotNull();
		assertThat(inexistantCellByIndex.asString()).isNull();
		assertThat(inexistantCellByIndex.asLong().isNull()).isTrue();
	}

	@Value(staticConstructor = "of")
	private static class Import {
		private String a;
		private String b;
		private String c;
		private String d;
		private Integer integerNumber;
		private Double doubleNumber;
	}

}
