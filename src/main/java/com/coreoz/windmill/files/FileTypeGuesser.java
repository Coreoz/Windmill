package com.coreoz.windmill.files;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * An utility class to guess file types by reading the first bytes of the file
 */
public final class FileTypeGuesser {

	private static final byte[] ZIP_FIRST_BYTES = {0x50, 0x4B};
	private static final byte[] XLS_FIRST_BYTES = fromIntArray(new int[]{ 0xD0, 0xCF, 0x11, 0xE0, 0xA1, 0xB1, 0x1A, 0xE1 });

	private static final int MAX_SIGNATURE_LENGTH = Math.max(ZIP_FIRST_BYTES.length, XLS_FIRST_BYTES.length);

	public static int maxSignatureLength() {
		return MAX_SIGNATURE_LENGTH;
	}

	/**
	 * Guess the type of a file by reading the first bytes of the file
	 */
	public static FileType guess(FileSource fileSource) {
		if(Arrays.equals(ZIP_FIRST_BYTES, fileSource.peek(ZIP_FIRST_BYTES.length))) {
			return FileType.ZIP;
		}
		if(Arrays.equals(XLS_FIRST_BYTES, fileSource.peek(XLS_FIRST_BYTES.length))) {
			return FileType.CFBF;
		}
		return FileType.UNKNOWN;
	}

	private static byte[] fromIntArray(int[] values) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int value : values) {
			baos.write(value);
		}

		return baos.toByteArray();
	}

}

