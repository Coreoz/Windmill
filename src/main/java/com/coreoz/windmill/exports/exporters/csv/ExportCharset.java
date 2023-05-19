package com.coreoz.windmill.exports.exporters.csv;

import java.nio.charset.Charset;

import com.coreoz.windmill.utils.BomCharset;

public class ExportCharset {
    private Charset charset;
    private byte[] bomBytes;

    public ExportCharset(Charset charset, byte[] bom) {
        this.charset = charset;
        this.bomBytes = bom;
    }

    public ExportCharset(BomCharset charset) {
        this.charset = charset.getCharset();
        this.bomBytes = charset.getBomBytes();
    }

    public Charset getCharset() {
        return this.charset;
    }

    public byte[] getBomBytes() {
        return this.bomBytes;
    }
}
