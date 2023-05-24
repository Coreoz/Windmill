package com.coreoz.windmill.exports.exporters.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Define how to style each cell in the export
 */
public interface ExcelCellStyler {
	void initialize(Workbook workbook);

	void style(Cell cell);

	static ExcelCellStyler bordersStyle() {
		return new ExcelCellStylerBorders();
	}

	static ExcelCellStyler emptyStyle() {
		return ExcelCellStylerEmpty.noStyleCellStyler;
	}
}
