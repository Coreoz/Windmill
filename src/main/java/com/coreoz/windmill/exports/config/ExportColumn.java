package com.coreoz.windmill.exports.config;

import java.util.function.Function;

import lombok.Value;

@Value(staticConstructor = "of")
public class ExportColumn<T> {

	private final String name;
	private final Function<T, ?> toValue;

}
