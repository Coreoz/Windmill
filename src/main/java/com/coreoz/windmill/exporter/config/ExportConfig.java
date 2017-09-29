package com.coreoz.windmill.exporter.config;

import java.util.List;
import java.util.function.Function;

public class ExportConfig<T> {

	private final Iterable<T> rows;

	public ExportConfig(Iterable<T> rows) {
		this.rows = rows;
	}

	public ExportRowsConfig<T> withNoHeaderMapping(List<Function<T, ?>> toValues) {
		return new ExportRowsConfig<>(rows, new ExportNoHeaderMapping<>(toValues));
	}

	@SafeVarargs
	public final ExportRowsConfig<T> withNoHeaderMapping(Function<T, ?> ...toValue) {
		return new ExportRowsConfig<>(rows, new ExportNoHeaderMapping<>(toValue));
	}

	public ExportRowsConfig<T> withHeaderMapping(ExportHeaderMapping<T> mapping) {
		return new ExportRowsConfig<>(rows, mapping);
	}

}
