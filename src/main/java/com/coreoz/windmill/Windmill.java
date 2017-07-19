package com.coreoz.windmill;

import java.util.function.Function;

import com.coreoz.windmill.importer.Importer;

public class Windmill {

	public static<T> Importer<T> newImport(Function<Row, T> rowMapper) {
		return new Importer<>(rowMapper);
	}

}
