package com.coreoz.windmill;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.coreoz.windmill.exporter.config.ExportMapping;
import com.coreoz.windmill.importer.FileSource;
import com.coreoz.windmill.importer.ParserGuesserTest;

import lombok.Value;

public class WindmillTest {

	@Test
	public void should_export_as_xlsx() {
		byte[] xlsExport = Windmill
			.export(Arrays.asList(
				Import.of("1", "2", "3"),
				Import.of("4", "5", "6")
			))
			.withMapping(new ExportMapping<Import>()
				.add("a", Import::getA)
				.add("b", Import::getB)
				.add("c", Import::getC)
			)
			.asExcel()
			.toByteArray();

		tryParseFile(FileSource.of(xlsExport));
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

	private void tryParseFile(String name) {
		tryParseFile(FileSource.of(ParserGuesserTest.class.getResourceAsStream(name)));
	}

	private void tryParseFile(FileSource fileSource) {
		List<Import> result = Windmill
			.parse(fileSource)
			// do not parse the header line
			.skip(1)
			.map(row -> Import.of(
					row.cell("a").asString(),
					row.cell("b").asString(),
					row.cell("c").asString()
					))
			.collect(Collectors.toList());

		assertThat(result).containsExactly(
			Import.of("1", "2", "3"),
			Import.of("4", "5", "6")
		);
	}

	@Value(staticConstructor = "of")
	private static class Import {
		private String a;
		private String b;
		private String c;
	}

}
