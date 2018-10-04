package com.coreoz.windmill.exports.mapping;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ExportHeaderMapping<T> implements ExportMapping<T> {

	private final LinkedMap<String, Function<T, ?>> toValues;

	public ExportHeaderMapping(Map<String, Function<T, ?>> toValues) {
		this(new LinkedMap<>(toValues));
	}

	public ExportHeaderMapping(LinkedMap<String, Function<T, ?>> toValues) {
		this.toValues = toValues;
	}

	@Override
	public List<String> headerColumns() {
		return new ArrayList<>(toValues.keySet());
	}

	@Override
	public int columnsCount() {
		return toValues.size();
	}

	@Override
	public Object cellValue(int columnIndex, T row) {
		return toValues.getValue(columnIndex).apply(row);
	}

}
