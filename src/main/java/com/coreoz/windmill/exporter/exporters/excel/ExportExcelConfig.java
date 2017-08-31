package com.coreoz.windmill.exporter.exporters.excel;

import org.apache.poi.ss.usermodel.Sheet;

public class ExportExcelConfig {

	private final Sheet sheet;
	private ExcelCellStyler cellStyler;

	private ExportExcelConfig(Sheet sheet) {
		this.sheet = sheet;
		this.cellStyler = ExcelCellStyler.bordersStyle();
		this.cellStyler.initialize(sheet.getWorkbook());
	}

	Sheet sheet() {
		return this.sheet;
	}

	ExcelCellStyler cellStyler() {
		return this.cellStyler;
	}

	public ExportExcelConfig withCellStyler(ExcelCellStyler cellStyler) {
		this.cellStyler = cellStyler;
		return this;
	}

	public static ExportExcelConfig of(ExportExcelSheetConfig sheetConfig) {
		return new ExportExcelConfig(sheetConfig.sheet());
	}

}
