package com.coreoz.windmill.importer;

import java.util.function.Function;

public class NumberValueWithDefault<T> extends NumberValue<T> {

	private final T value;

	public NumberValueWithDefault(T defaultValue, String stringValue, Function<String, T> valueParser) {
		super(stringValue, valueParser);

		this.value = defaultValue;
	}

	@Override
	public boolean isNull() {
		return value == null && super.isNull();
	}

	@Override
	public T value() {
		return value != null ? value : super.value();
	}

	@Override
	public T safeValue() {
		return value != null ? value : super.safeValue();
	}

}
