package com.coreoz.windmill.exporter.exporters.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.coreoz.windmill.exporter.config.ExportMapping;

import lombok.SneakyThrows;

public class ExcelExporter<T> {

	private final Iterable<T> rows;
	private final ExportMapping<T> mapping;
	private final ExportExcelConfig sheetConfig;
	private Row currentRow;

	public ExcelExporter(Iterable<T> rows, ExportMapping<T> mapping, ExportExcelConfig sheetConfig) {
		this.rows = rows;
		this.mapping = mapping;
		this.sheetConfig = sheetConfig;
		this.currentRow = sheetConfig.sheet().createRow(0);
	}

	/**
	 * @throws IOException if anything can't be written.
	 */
	@SneakyThrows
	public void writeTo(OutputStream outputStream) {
		writeRows();
		sheetConfig.sheet().getWorkbook().write(outputStream);
	}

	/**
	 * @throws IOException if anything can't be written.
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		writeTo(byteOutputStream);
		return byteOutputStream.toByteArray();
	}

	// internals

	private void writeRows() {
		writeHeaderRow();

		for(T row : rows) {
			writeRow(row);
		}

		setAutoSizeColumns();
	}

	private void writeHeaderRow() {
		for (int i = 0; i < mapping.columns().size(); i++) {
			setCellValue(mapping.columns().get(i).getName(), i);
		}
	}

	private void setAutoSizeColumns() {
		for (int i = 0; i < mapping.columns().size(); i++) {
			sheetConfig.sheet().autoSizeColumn(i);
		}
	}

	private void writeRow(T row) {
		currentRow = sheetConfig.sheet().createRow(currentRow.getRowNum() + 1);
		for (int i = 0; i < mapping.columns().size(); i++) {
			setCellValue(mapping.columns().get(i).getToValue().apply(row), i);
		}
	}

	private void setCellValue(final Object value, final int columnIndex) {
		Cell cell = currentRow.createCell(columnIndex);
		sheetConfig.cellStyler().style(cell);

		if(value == null) {
			return;
		}

		// numbers
		if (value instanceof Integer) {
			cell.setCellValue(Double.valueOf(((Integer) value).intValue()));
		} else if (value instanceof Long) {
			cell.setCellValue(Double.valueOf(((Long) value).longValue()));
		} else if (value instanceof Float) {
			cell.setCellValue(Double.valueOf(((Float) value).floatValue()));
		} else if (value instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal) value).doubleValue());
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		}
		// other types
		else if (value instanceof Boolean) {
			cell.setCellValue(((Boolean) value).booleanValue());
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
