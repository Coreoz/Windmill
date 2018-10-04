package com.coreoz.windmill.exports.exporters.excel;

import com.coreoz.windmill.files.FileSource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ExportExcelConfigTest {

	@Test
	public void withOrigin_check_that_negative_coordinates_are_not_accepted() {
		ExportExcelConfig exportConfig = ExportExcelConfig.newXlsxFile().build();

		try {
			exportConfig.withOrigin(1, -1);
			fail("Should not accept negative row index");
		} catch (IllegalArgumentException e) {
			// as expected
		}

		try {
			exportConfig.withOrigin(-1, 0);
			fail("Should not accept negative column index");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

	@Test
	public void withOrigin_check_that_positive_coordinates_are_accepted() {
		ExportExcelConfig exportConfig = ExportExcelConfig.newXlsxFile().build();

		exportConfig.withOrigin(0, 0);
		assertThat(exportConfig.columnOrigin()).isZero();
		assertThat(exportConfig.rowOrigin()).isZero();

		exportConfig.withOrigin(3, 5);
		assertThat(exportConfig.columnOrigin()).isEqualTo(3);
		assertThat(exportConfig.rowOrigin()).isEqualTo(5);
	}

	@Test
	public void fromWorkbook_check_that_xlsx_files_are_accepted() {
		ExportExcelConfigBuilder configBuilder = ExportExcelConfig.fromWorkbook(
			FileSource.of(ExportExcelConfigTest.class.getResourceAsStream("/import.xlsx"))
		);

		assertThat(configBuilder).isNotNull();
	}

	@Test
	public void fromWorkbook_check_that_xls_files_are_accepted() {
		ExportExcelConfigBuilder configBuilder = ExportExcelConfig.fromWorkbook(
			FileSource.of(ExportExcelConfigTest.class.getResourceAsStream("/import.xls"))
		);

		assertThat(configBuilder).isNotNull();
	}

	@Test
	public void fromWorkbook_check_that_non_excel_files_are_not_accepted() {
		try {
			ExportExcelConfig.fromWorkbook(
				FileSource.of(ExportExcelConfigTest.class.getResourceAsStream("/import.csv"))
			);
			fail("Should not accept non excel files");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

}
