package com.coreoz.windmill.exports.exporters.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

class ExcelCellStylerBorders implements ExcelCellStyler {
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
