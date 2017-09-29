package com.coreoz.windmill.exporter.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExportHeaderMapping<T> implements ExportMapping<T> {

	private final List<ExportColumn<T>> columns;

	public ExportHeaderMapping(List<ExportColumn<T>> columns) {
		this.columns = columns;
	}

	public ExportHeaderMapping() {
		this(new ArrayList<>());
	}

	public ExportHeaderMapping<T> add(String name, Function<T, ?> toValue) {
		this.columns.add(ExportColumn.of(name, toValue));
		return this;
	}

	@Override
	public List<String> headerColumns() {
		return columns
			.stream()
			.map(ExportColumn::getName)
			.collect(Collectors.toList());
	}

	@Override
	public int columnsCount() {
		return columns.size();
	}

	@Override
	public Object cellValue(int columnIndex, T row) {
		return columns.get(columnIndex).getToValue().apply(row);
	}

}
