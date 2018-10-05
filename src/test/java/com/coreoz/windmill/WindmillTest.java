package com.coreoz.windmill;

import com.coreoz.windmill.exports.exporters.excel.ExcelCellStyler;
import com.coreoz.windmill.exports.exporters.excel.ExcelExporter;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.exports.mapping.ExportHeaderMapping;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.files.ParserGuesserTest;
import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.FileParser;
import com.coreoz.windmill.imports.Parsers;
import com.coreoz.windmill.imports.Row;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Windmill
 */
public class WindmillTest {

	@Test
	public void should_export_as_xlsx_with_header() {
		byte[] xlsxExport = Exporter.<Import>builder()
			.withExportMapping(exportHeaderMapping())
			.asExcel()
			.writeRows(data())
			.toByteArray();

		tryParseHeaderFile(FileSource.of(xlsxExport));
	}

	@Test
	public void should_export_as_csv_with_header() {
		byte[] csvExport = Exporter.<Import>builder()
				.withExportMapping(exportHeaderMapping())
				.asCsv()
				.writeRows(data())
				.toByteArray();

		tryParseHeaderFile(FileSource.of(csvExport));
	}

	@Test
	public void should_export_as_csv_with_header_by_properties() {
        List<Import> data = Arrays.asList(
                Import.builder().a("a1").b("b1").build(),
                Import.builder().a("a2").b("b2").build()
        );

        byte[] csvExport = Windmill.<Import>exporter()
				.withHeaders()
				.column("a", Import::getA)
				.column("b", Import::getB)
				.asCsv()
				.writeRows(data)
				.toByteArray();

		List<Import> result = Windmill.importer()
				.source(FileSource.of(csvExport))
				.withHeaders()
				.stream()
				.map(row -> Import.builder()
						.a(row.cell(0).asString())
						.b(row.cell(1).asString())
						.build())
				.collect(Collectors.toList());

		assertThat(result).containsExactlyElementsOf(data);
	}

	@Test
	public void should_export_as_xlsx_no_header() {
		byte[] xlsxExport = Exporter.<Import>builder()
				.withoutHeaders()
				.columns(exportNoHeaderMapping())
			 	.asExcel(ExportExcelConfig.newXlsxFile()
						.build("Sheet1"))
				.writeRows(data())
				.toByteArray();

		tryParseNoHeaderFile(FileSource.of(xlsxExport));
		tryParseNoHeaderFile(FileSource.of(xlsxExport), Parsers.xlsx());
		tryParseNoHeaderFile(FileSource.of(xlsxExport), Parsers.xlsx(0));
		tryParseNoHeaderFile(FileSource.of(xlsxExport), Parsers.xlsx("Sheet1"));
	}

	@Test
	public void should_export_as_xls_no_header() {
        ExcelExporter<Import> exporter = Exporter.<Import>builder()
                .withoutHeaders()
                .columns(exportNoHeaderMapping())
                .asExcel(ExportExcelConfig.newXlsFile()
                        .build("Sheet1")
                        .withCellStyler(ExcelCellStyler.bordersStyle()))
                .writeRows(data());

        assertThat(exporter.workbook()).isNotNull();
        byte[] xlsExport = exporter.toByteArray();

		tryParseNoHeaderFile(FileSource.of(xlsExport));
		tryParseNoHeaderFile(FileSource.of(xlsExport), Parsers.xls());
		tryParseNoHeaderFile(FileSource.of(xlsExport), Parsers.xls(0));
		tryParseNoHeaderFile(FileSource.of(xlsExport), Parsers.xls("Sheet1"));
	}

	@Test
	public void should_export_as_csv_with_no_header() {
		byte[] csvExport = Exporter.<Import>builder()
				.withoutHeaders()
				.columns(exportNoHeaderMapping())
				.asCsv()
				.writeRows(data())
				.toByteArray();

		tryParseNoHeaderFile(FileSource.of(csvExport));
		tryParseNoHeaderFile(FileSource.of(csvExport), Parsers.csv());
	}

	@Test
	public void should_export_excel_data_starting_from_a_non_origin_point() {
		byte[] xlsxExport = Exporter.<Import>builder()
			.withoutHeaders()
			.columns(exportNoHeaderMapping())
			// we are using an existing file to write the new data,
			// else POI will simply ignore the first empty rows and the test will be biased
			.asExcel(ExportExcelConfig.fromWorkbook(loadFile("/import.xlsx")).build("Feuil1").withOrigin(6, 3))
			.writeRows(data())
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
				row.cell(9).asInteger().value(),
				row.cell(10).asDouble().value()
			))
			.collect(Collectors.toList());

			assertThat(result).containsExactlyElementsOf(data());
		}
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
			Import.of("String val", null, "3 String", 54564, 0.25)
		));
	}

	// utils

	private List<Import> data() {
		return Arrays.asList(
			Import.of("String value 1", "2", "String 3", null, 5.45),
			Import.of("String val", null, "3 String ", 54564, 0.25)
		);
	}

	private ExportHeaderMapping<Import> exportHeaderMapping() {
		Map<String, Function<Import, ?>> toValues = new LinkedHashMap<>();
		toValues.put("a", Import::getA);
		toValues.put("b", Import::getB);
		toValues.put("c", Import::getC);
		toValues.put("Integer number", Import::getIntegerNumber);
		toValues.put("Double number", Import::getDoubleNumber);

		return new ExportHeaderMapping<>(toValues);
	}

	private List<Function<Import, ?>> exportNoHeaderMapping() {
		return Arrays.asList(
			Import::getA,
			Import::getB,
			Import::getC,
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

	private void tryParseNoHeaderFile(FileSource fileSource, FileParser parser) {
		try (Stream<Row> rowStream = Windmill.parse(fileSource, parser)) {
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
			row.cell(3).asInteger().value(),
			row.cell(4).asDouble().value()
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
}
