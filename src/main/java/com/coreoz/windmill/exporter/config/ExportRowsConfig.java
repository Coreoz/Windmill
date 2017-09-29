package com.coreoz.windmill.exporter.config;

import com.coreoz.windmill.exporter.exporters.csv.CsvExporter;
import com.coreoz.windmill.exporter.exporters.csv.ExportCsvConfig;
import com.coreoz.windmill.exporter.exporters.excel.ExcelExporter;
import com.coreoz.windmill.exporter.exporters.excel.ExportExcelConfig;

public class ExportRowsConfig<T> {

	private final Iterable<T> rows;
	private final ExportMapping<T> mapping;

	public ExportRowsConfig(Iterable<T> rows, ExportMapping<T> mapping) {
		this.mapping = mapping;
		this.rows = rows;
	}

	public CsvExporter<T> asCsv() {
		return asCsv(ExportCsvConfig.builder().build());
	}

	public CsvExporter<T> asCsv(ExportCsvConfig config) {
		return new CsvExporter<>(rows, mapping, config);
	}

	public ExcelExporter<T> asExcel() {
		return asExcel(ExportExcelConfig.newXlsxFile().build());
	}

	public ExcelExporter<T> asExcel(ExportExcelConfig config) {
		return new ExcelExporter<>(rows, mapping, config);
	}

}
