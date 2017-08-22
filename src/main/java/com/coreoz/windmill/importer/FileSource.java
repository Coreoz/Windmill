package com.coreoz.windmill.importer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import lombok.SneakyThrows;

public class FileSource {

	private final BufferedInputStream inputStream;

	private FileSource(InputStream inputStream) {
		this.inputStream = new BufferedInputStream(inputStream);
	}

	public InputStream toInputStream() {
		return inputStream;
	}

	@SneakyThrows
	public byte[] peek(int nbBytesToRead) {
		inputStream.mark(nbBytesToRead);
		byte[] peeked = new byte[nbBytesToRead];
		inputStream.read(peeked);
		inputStream.reset();
		return peeked;
	}

	public static FileSource of(InputStream inputStream) {
		return new FileSource(inputStream);
	}

	public static FileSource of(byte[] inputData) {
		return of(new ByteArrayInputStream(inputData));
	}

}
