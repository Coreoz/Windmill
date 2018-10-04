package com.coreoz.windmill.exports.mapping;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class NoHeaderDecorator<T> implements ExportMapping<T> {

	private final ExportMapping<T> delegate;

	@Override
	public List<String> headerColumns() {
		return Collections.emptyList();
	}

	@Override
	public int columnsCount() {
		return delegate.columnsCount();
	}

	@Override
	public Object cellValue(int columnIndex, T row) {
		return delegate.cellValue(columnIndex, row);
	}
}
