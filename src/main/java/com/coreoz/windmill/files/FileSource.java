package com.coreoz.windmill.files;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class FileSource {
	private final PushbackInputStream bufferedStream;

	private FileSource(InputStream inputStream) {
		this.bufferedStream = new PushbackInputStream(
			inputStream,
			Math.max(BomCharset.maxBomLength(), FileTypeGuesser.maxSignatureLength())
		);
	}

	public InputStream toInputStream() {
		return bufferedStream;
	}

	@SneakyThrows
	public byte[] peek(int bytesToRead) {
		byte[] bytesPeeked = bufferedStream.readNBytes(bytesToRead);
		bufferedStream.unread(bytesPeeked);
		return bytesPeeked;
	}

	public static FileSource of(InputStream inputStream) {
		return new FileSource(inputStream);
	}

	public static FileSource of(byte[] inputData) {
		return of(new ByteArrayInputStream(inputData));
	}
}
