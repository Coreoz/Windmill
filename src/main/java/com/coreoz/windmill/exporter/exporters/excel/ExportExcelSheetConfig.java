package com.coreoz.windmill.exporter.exporters.excel;

import org.apache.poi.ss.usermodel.Sheet;

public class ExportExcelSheetConfig {

	private final Sheet sheet;

	private ExportExcelSheetConfig(Sheet sheet) {
		this.sheet = sheet;
	}

	Sheet sheet() {
		return this.sheet;
	}

	public static ExportExcelSheetConfig of(ExportExcelFileConfig exportExcelFileConfig) {
		return of(exportExcelFileConfig, "Export");
	}

	public static ExportExcelSheetConfig of(ExportExcelFileConfig exportExcelFileConfig, String sheetname) {
		Sheet existingSheet = exportExcelFileConfig.workbook().getSheet(sheetname);
		if (existingSheet != null) {
			return new ExportExcelSheetConfig(existingSheet);
		}
		return new ExportExcelSheetConfig(exportExcelFileConfig.workbook().createSheet(sheetname));
	}

}
