package com.coreoz.windmill;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import com.coreoz.windmill.exports.config.ExportConfig;
import com.coreoz.windmill.exports.config.ExportHeaderMapping;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.files.ParserGuesserTest;
import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.Row;

import lombok.Value;

public class WindmillTest {

	@Test
	public void should_export_as_xlsx_with_header() {
		byte[] xlsExport = exportBase()
			.withHeaderMapping(exportHeaderMapping())
			.asExcel()
			.toByteArray();

		tryParseHeaderFile(FileSource.of(xlsExport));
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
		byte[] xlsExport = exportBase()
			.withNoHeaderMapping(exportNoHeaderMapping())
			.asExcel()
			.toByteArray();

		tryParseNoHeaderFile(FileSource.of(xlsExport));
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

	// utils

	private List<Import> data() {
		return Arrays.asList(
			Import.of("String value 1", "2", "String 3", null, 5.45),
			Import.of("String val", null, "3 String", 54564, 0.25)
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
			.add("Integer number", Import::getIntegerNumber)
			.add("Double number", Import::getDoubleNumber);
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
			.map(row -> Import.of(
				row.cell("a").asString(),
				row.cell("b").asString(),
				row.cell("c").asString(),
				row.cell("Integer number").asInteger().value(),
				row.cell("Double number").asDouble().value()
			))
			.collect(Collectors.toList());

		assertThat(result).containsExactlyElementsOf(data());
	}

	private void tryParseNoHeaderFile(FileSource fileSource) {
		List<Import> result = Windmill
			.parse(fileSource)
			.map(row -> Import.of(
				row.cell(0).asString(),
				row.cell(1).asString(),
				row.cell(2).asString(),
				row.cell(3).asInteger().value(),
				row.cell(4).asDouble().value()
			))
			.collect(Collectors.toList());

		assertThat(result).containsExactlyElementsOf(data());
	}

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
		private Integer integerNumber;
		private Double doubleNumber;
	}

}
