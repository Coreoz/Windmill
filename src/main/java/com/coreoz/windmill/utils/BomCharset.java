package com.coreoz.windmill.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class BomCharset {
    private Charset charset;
    private byte[] bomBytes;

    public static final BomCharset UTF_8 = new BomCharset(
        StandardCharsets.UTF_8,
        new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF }
    );

    public static final BomCharset UTF_8_NO_BOM = new BomCharset(
        StandardCharsets.UTF_8,
        null
    );

    public static final BomCharset UTF_16BE = new BomCharset(
        StandardCharsets.UTF_16BE,
        new byte[] { (byte)0xFE, (byte)0xFF }
    );

    public static final BomCharset UTF_16BE_NO_BOM = new BomCharset(
        StandardCharsets.UTF_16BE,
        null
    );

    public static final BomCharset UTF_16LE = new BomCharset(
        StandardCharsets.UTF_16LE,
        new byte[] { (byte)0xFF, (byte)0xFE }
    );

    public static final BomCharset UTF_16LE_NO_BOM = new BomCharset(
        StandardCharsets.UTF_16LE,
        null
    );

    public static final BomCharset ISO_8859_1_NO_BOM = new BomCharset(
        StandardCharsets.ISO_8859_1,
        null
    );

    public static final BomCharset[] availableBoms = {
        BomCharset.UTF_8,
        BomCharset.UTF_16BE,
        BomCharset.UTF_16LE,
    };

    /**
     * @return the maximal possible length for a Bom
     */
    public static int maxBomLength() {
        int maxLength = 0;
        for (BomCharset charset : availableBoms) {
            int bomLength = charset.bomLength();
            if (maxLength < bomLength) {
                maxLength = bomLength;
            }
        }
        return maxLength;
    }

    public int bomLength() {
        return bomBytes == null ? 0 : bomBytes.length;
    }

    public static BomCharset detectCharset(byte[] documentFirstBytes, BomCharset fallbackCharset) {
        for (BomCharset charset : availableBoms) {
            if (documentFirstBytes.length < charset.bomLength()) {
                // Should not happen if maxBomLength() is used to get the document first bytes
                continue;
            }
            byte[] bomBytes = charset.getBomBytes();
            int i = 0;
            while (i < charset.bomLength()) {
                if (bomBytes[i] != documentFirstBytes[i]) {
                    break;
                }
                ++i;
            }
            // All bytes from the bom were matching the document first bytes
            if (i == charset.bomLength()) {
                return charset;
            }
        }

        return fallbackCharset;
    }

    public BomCharset(Charset charset, byte[] bomBytes) {
        this.charset = charset;
        this.bomBytes = bomBytes;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public byte[] getBomBytes() {
        return this.bomBytes;
    }

    public void setBomBytes(byte[] bomBytes) {
        this.bomBytes = bomBytes;
    }
}
