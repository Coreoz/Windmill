package com.coreoz.windmill.exports.exporters.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Workbook;

import com.coreoz.windmill.exports.config.ExportMapping;

import lombok.SneakyThrows;

public class ExcelExporter<T> {

	private final Iterable<T> rows;
	private final ExportMapping<T> mapping;
	private final ExportExcelConfig sheetConfig;
	private Row currentExcelRow;

	public ExcelExporter(Iterable<T> rows, ExportMapping<T> mapping, ExportExcelConfig sheetConfig) {
		this.rows = rows;
		this.mapping = mapping;
		this.sheetConfig = sheetConfig;
		this.currentExcelRow = null;
	}

	/**
	 * Write the export file in the {@link Workbook}
	 *
	 * @return The {@link Workbook} in which the export has been written
	 */
	public Workbook write() {
		writeRows();
		return sheetConfig.sheet().getWorkbook();
	}

	/**
	 * Write the export file in an existing {@link OutputStream}.
	 *
	 * This {@link OutputStream} will not be closed automatically:
	 * it should be closed manually after this method is called.
	 *
	 * @throws IOException if anything can't be written.
	 */
	@SneakyThrows
	public OutputStream writeTo(OutputStream outputStream) {
		write().write(outputStream);
		return outputStream;
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

	private void writeRow(T row) {
		initializeExcelRow();
		for (int i = 0; i < mapping.columnsCount(); i++) {
			setCellValue(mapping.cellValue(i, row), i);
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
			cell.setCellValue(Double.valueOf((Integer) value));
		} else if (value instanceof Long) {
			cell.setCellValue(Double.valueOf((Long) value));
		} else if (value instanceof Float) {
			cell.setCellValue(Double.valueOf((Float) value));
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
