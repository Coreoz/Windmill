package com.coreoz.windmill.exports.exporters.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

class ExcelCellStylerEmpty implements ExcelCellStyler {
    static final ExcelCellStylerEmpty noStyleCellStyler = new ExcelCellStylerEmpty();

    @Override
    public void initialize(Workbook workbook) {
    }

    @Override
    public void style(Cell cell) {
    }
}
