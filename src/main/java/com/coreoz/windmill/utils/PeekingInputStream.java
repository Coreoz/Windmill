package com.coreoz.windmill.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * An InputStream wrapper that provides a peeking feature.
 * See {@link BomCharset} for usage
 */
public class PeekingInputStream {
    // This number comes from the max possible length for available boms
    private static final int MAX_BOM_LENGTH = BomCharset.maxBomLength();

    private final PushbackInputStream fileStream;
    private final InputStream originalStream;

    public PeekingInputStream(InputStream fileStream) {
        this.fileStream = new PushbackInputStream(fileStream, MAX_BOM_LENGTH);
        this.originalStream = fileStream;
    }

    public byte[] peekMaxBomLength() throws IOException {
        return peek(MAX_BOM_LENGTH);
    }

    private byte[] peek(int bytesToRead) throws IOException {
        byte[] bytesPeeked = new byte[bytesToRead];
        fileStream.read(bytesPeeked, 0, bytesToRead);
        this.fileStream.unread(bytesPeeked);
        return bytesPeeked;
    }

    public PushbackInputStream peekedStream() {
        return this.fileStream;
    }
}