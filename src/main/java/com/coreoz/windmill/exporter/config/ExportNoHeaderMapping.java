package com.coreoz.windmill.exporter.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ExportNoHeaderMapping<T> implements ExportMapping<T> {

	private final List<Function<T, Object>> toValues;

	public ExportNoHeaderMapping(List<Function<T, Object>> toValues) {
		this.toValues = toValues;
	}

	@SafeVarargs
	public ExportNoHeaderMapping(Function<T, Object> ...toValue) {
		this.toValues = Arrays.asList(toValue);
	}

	@Override
	public List<String> headerColumns() {
		return Collections.emptyList();
	}

	@Override
	public int columnsCount() {
		return toValues.size();
	}

	@Override
	public Object cellValue(int columnIndex, T row) {
		return toValues.get(columnIndex).apply(row);
	}

}
