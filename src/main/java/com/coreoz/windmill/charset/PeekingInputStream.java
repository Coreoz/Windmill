package com.coreoz.windmill.charset;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * An InputStream wrapper that provides a peeking feature.
 * See {@link BomCharset} for usage
 */
public class PeekingInputStream {
    private final PushbackInputStream fileStream;

    public PeekingInputStream(InputStream fileStream) {
        this.fileStream = new PushbackInputStream(fileStream, BomCharset.maxBomLength());
    }

    public byte[] peekMaxBomLength() throws IOException {
        // This number comes from the max possible length for available boms
        return peek(BomCharset.maxBomLength());
    }

    private byte[] peek(int bytesToRead) throws IOException {
        byte[] bytesPeeked = fileStream.readNBytes(bytesToRead);
        this.fileStream.unread(bytesPeeked);
        return bytesPeeked;
    }

    public PushbackInputStream peekedStream() {
        return this.fileStream;
    }
}