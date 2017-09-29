package com.coreoz.windmill.exporter.exporters.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class ExportExcelConfigBuilder {

	private final Workbook workbook;

	ExportExcelConfigBuilder(Workbook workbook) {
		this.workbook = workbook;
	}

	Workbook workbook() {
		return this.workbook;
	}
	
	public ExportExcelConfig build() {
		return build("Export");
	}
	
	public ExportExcelConfig build(String sheetName) {
		Sheet existingSheet = workbook.getSheet(sheetName);
		if (existingSheet != null) {
			return new ExportExcelConfig(existingSheet);
		}
		return new ExportExcelConfig(workbook.createSheet(sheetName));
	}

}

