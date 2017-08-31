package com.coreoz.windmill.exporter.config;

public class ExportConfig<T> {

	private final Iterable<T> rows;

	public ExportConfig(Iterable<T> rows) {
		this.rows = rows;
	}

	public ExportRowsConfig<T> withMapping(ExportMapping<T> mapping) {
		return new ExportRowsConfig<>(rows, mapping);
	}

}
