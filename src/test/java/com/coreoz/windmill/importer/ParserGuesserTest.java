package com.coreoz.windmill.importer;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.coreoz.windmill.importer.parsers.excel.BinaryExcelParser;
import com.coreoz.windmill.importer.parsers.excel.OpenXmlExcelParser;

public class ParserGuesserTest {

	@Test
	public void guess_check_excel_ooxml() {
		Assertions
			.assertThat(ParserGuesser.guess(FileSource.of(ParserGuesserTest.class.getResourceAsStream("/import.xlsx"))))
			.isInstanceOf(OpenXmlExcelParser.class);
	}

	@Test
	public void guess_check_excel_binary() {
		Assertions
		.assertThat(ParserGuesser.guess(FileSource.of(ParserGuesserTest.class.getResourceAsStream("/import.xls"))))
		.isInstanceOf(BinaryExcelParser.class);
	}

}
