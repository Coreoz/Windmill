package com.coreoz.windmill.exports.exporters.csv;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.ByteOrderMark;

public class ExportCharset {
    private Charset charset;
    private byte[] bomBytes;

    private static final Map<Charset, byte[]> bomsMap;
    static {
        bomsMap = new HashMap<>();
        // Add here existing bom to automatically handle default bom for specific encoding
        bomsMap.put(StandardCharsets.UTF_8, new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
        bomsMap.put(StandardCharsets.UTF_16BE, new byte[] { (byte)0xFE, (byte)0xFF });
        bomsMap.put(StandardCharsets.UTF_16LE, new byte[] { (byte)0xFF, (byte)0xFE });
        bomsMap.put(StandardCharsets.ISO_8859_1, null);
    }

    public ExportCharset(Charset charset, byte[] bom) {
        this.charset = charset;
        this.bomBytes = bom;
    }

    public ExportCharset(Charset charset) {
        this.charset = charset;
        this.bomBytes = bomsMap.get(charset);
    }

    public Charset getCharset() {
        return this.charset;
    }

    public byte[] getBomBytes() {
        return this.bomBytes;
    }

    /**
     * @return the ByteOrderMarker from apache, used for parsing your csv file
     * null if there is no BOM
     */
    public ByteOrderMark getBoms() {
        if (this.charset.equals(StandardCharsets.UTF_8)) {
            return ByteOrderMark.UTF_8;
        }
        if (this.charset.equals(StandardCharsets.UTF_16LE)) {
            return ByteOrderMark.UTF_16LE;
        }
        if (this.charset.equals(StandardCharsets.UTF_16BE)) {
            return ByteOrderMark.UTF_16BE;
        }
        /**
         * Add here the mapping between your charset and ByteOrderMarker from apache
         * This is currently only used from the csv parser
         */
        return null;
    }
}
