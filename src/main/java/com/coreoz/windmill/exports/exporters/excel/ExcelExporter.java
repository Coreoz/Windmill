package com.coreoz.windmill.exports.exporters.excel;

import com.coreoz.windmill.Exporter;
import com.coreoz.windmill.exports.mapping.ExportMapping;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class ExcelExporter<T> implements Exporter<T> {

	private final ExportMapping<T> mapping;
	private final ExportExcelConfig sheetConfig;

	private Row currentExcelRow;

	public Workbook workbook() {
		return sheetConfig.sheet().getWorkbook();
	}

	public ExcelExporter<T> writeRow(T row) {
		initializeExcelRow();
		for (int i = 0; i < mapping.columnsCount(); i++) {
			setCellValue(mapping.cellValue(i, row), i);
		}

		return this;
	}

	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		writeInto(byteOutputStream);
		return byteOutputStream.toByteArray();
	}

	@Override
	public ExcelExporter<T> writeRows(Iterable<T> rows) {
		writeHeaderRow();

		for(T row : rows) {
			writeRow(row);
		}

		setAutoSizeColumns();
		return this;
	}

	@Override
	@SneakyThrows
	public ExcelExporter<T> writeInto(OutputStream outputStream) {
		Workbook workbook = sheetConfig.sheet().getWorkbook();
		workbook.write(outputStream);
		return this;
	}

	private void writeHeaderRow() {
		List<String> headerColumn = mapping.headerColumns();
		if(!headerColumn.isEmpty()) {
			initializeExcelRow();
			for (int i = 0; i < headerColumn.size(); i++) {
				setCellValue(headerColumn.get(i), i);
			}
		}
	}

	private void setAutoSizeColumns() {
		for (int i = 0; i < mapping.columnsCount(); i++) {
			sheetConfig.sheet().autoSizeColumn(i);
		}
	}

	private void initializeExcelRow() {
		int rowIndex = currentExcelRow == null ? sheetConfig.rowOrigin() : currentExcelRow.getRowNum() + 1;
		currentExcelRow = sheetConfig.sheet().getRow(rowIndex);
		if(currentExcelRow == null) {
			currentExcelRow =  sheetConfig.sheet().createRow(rowIndex);
		}
	}

	private void setCellValue(final Object value, final int columnIndex) {
		Cell cell = currentExcelRow.getCell(
			sheetConfig.columnOrigin() + columnIndex,
			MissingCellPolicy.CREATE_NULL_AS_BLANK
		);
		sheetConfig.cellStyler().style(cell);

		if(value == null) {
			return;
		}

		// numbers
		if (value instanceof Integer) {
			cell.setCellValue((double) (Integer) value);
		} else if (value instanceof Long) {
			cell.setCellValue((double) (Long) value);
		} else if (value instanceof Float) {
			cell.setCellValue((double) (Float) value);
		} else if (value instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal) value).doubleValue());
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		}
		// other types
		else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Calendar) {
			cell.setCellValue((Calendar) value);
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		}

		else {
			cell.setCellValue(value.toString());
		}
	}
}
