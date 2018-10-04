package com.coreoz.windmill.exports.mapping;

import java.util.Collections;
import java.util.List;

public class NoHeaderDecorator<T> implements ExportMapping<T> {

	private final ExportMapping<T> delegate;

	public NoHeaderDecorator(ExportMapping<T> delegate) {
		this.delegate = delegate;
	}

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
