package com.coreoz.windmill.streams;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.SneakyThrows;

public class IteratorStreams {
	/**
	 * Returns a sequential {@link Stream} of the remaining contents of
	 * {@code iterator}. Do not use {@code iterator} directly after passing it to
	 * this method.
	 */
	public static <T> Stream<T> stream(Iterator<T> iterator) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
	}

	@SneakyThrows
	public static void close(AutoCloseable closeable) {
		closeable.close();
	}
}
