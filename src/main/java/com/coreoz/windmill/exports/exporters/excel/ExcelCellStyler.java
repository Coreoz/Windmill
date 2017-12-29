package com.coreoz.windmill.exports.exporters.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Define how to style each cell in the export
 */
public interface ExcelCellStyler {

	void initialize(Workbook workbook);

	void style(Cell cell);

	static ExcelCellStyler bordersStyle() {
		return new ExcelCellBordersStyler();
	}

	public static class ExcelCellBordersStyler implements ExcelCellStyler {

		private CellStyle borderStyle;

		@Override
		public void initialize(Workbook workbook) {
			this.borderStyle = workbook.createCellStyle();
			this.borderStyle.setBorderBottom(BorderStyle.THIN);
			this.borderStyle.setBorderTop(BorderStyle.THIN);
			this.borderStyle.setBorderRight(BorderStyle.THIN);
			this.borderStyle.setBorderLeft(BorderStyle.THIN);
		}

		@Override
		public void style(Cell cell) {
			cell.setCellStyle(borderStyle);
		}

	}

}
