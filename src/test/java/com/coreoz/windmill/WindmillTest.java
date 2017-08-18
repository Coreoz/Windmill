package com.coreoz.windmill;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.coreoz.windmill.importer.FileSource;
import com.coreoz.windmill.importer.ParserGuesserTest;

import lombok.Value;

public class WindmillTest {

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
