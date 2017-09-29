package com.coreoz.windmill.files;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ParserGuesserTest {

	@Test
	public void guess_check_excel_ooxml() {
		Assertions
			.assertThat(FileTypeGuesser.guess(FileSource.of(ParserGuesserTest.class.getResourceAsStream("/import.xlsx"))))
			.isEqualTo(FileType.ZIP);
	}

	@Test
	public void guess_check_excel_binary() {
		Assertions
			.assertThat(FileTypeGuesser.guess(FileSource.of(ParserGuesserTest.class.getResourceAsStream("/import.xls"))))
			.isEqualTo(FileType.CFBF);
	}
}
