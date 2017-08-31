package com.coreoz.windmill.exporter.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExportMapping<T> {

	private final List<ExportColumn<T>> columns;

	public ExportMapping(List<ExportColumn<T>> columns) {
		this.columns = columns;
	}

	public ExportMapping() {
		this(new ArrayList<>());
	}

	public List<ExportColumn<T>> columns() {
		return this.columns;
	}

	public ExportMapping<T> add(String name, Function<T, Object> toValue) {
		this.columns.add(ExportColumn.of(name, toValue));
		return this;
	}

}
