package com.coreoz.windmill;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.coreoz.windmill.exporter.config.ExportMapping;
import com.coreoz.windmill.importer.FileSource;
import com.coreoz.windmill.importer.ParserGuesserTest;

import lombok.Value;

public class WindmillTest {

	public static void main(String[] args) throws FileNotFoundException {
		Windmill
			.export(Arrays.asList(
				Import.of("1", "2", "3"),
				Import.of("4", "5", "6")
			))
			.withMapping(new ExportMapping<Import>()
				.add("sdf sdf sdf sqdf sqd fsqd fsq df", Import::getA)
				.add("b", Import::getB)
				.add("c", Import::getC)
			)
			.asExcel()
			.writeTo(new FileOutputStream(new File("test.xlsx")));
	}

	@Test
	public void parse_basic_usage() {
		List<Import> result = Windmill
			.parse(FileSource.of(ParserGuesserTest.class.getResourceAsStream("/import.xlsx")))
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
