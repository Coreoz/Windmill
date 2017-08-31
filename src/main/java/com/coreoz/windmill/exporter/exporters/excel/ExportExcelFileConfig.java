package com.coreoz.windmill.exporter.exporters.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportExcelFileConfig {

	private final Workbook workbook;

	private ExportExcelFileConfig(Workbook workbook) {
		this.workbook = workbook;
	}

	Workbook workbook() {
		return this.workbook;
	}

	public static ExportExcelFileConfig newFile() {
		return newXlsxFile();
	}

	public static ExportExcelFileConfig newXlsxFile() {
		return new ExportExcelFileConfig(new XSSFWorkbook());
	}

	public static ExportExcelFileConfig newXlsFile() {
		return new ExportExcelFileConfig(new HSSFWorkbook());
	}

}
