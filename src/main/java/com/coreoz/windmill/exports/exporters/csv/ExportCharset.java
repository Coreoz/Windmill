package com.coreoz.windmill.exports.exporters.csv;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ExportCharset {
    private Charset charset;
    private String bom;

    private static final Map<Charset, String> bomsMap;
    static {
        bomsMap = new HashMap<>();
        // Add here existing bom to automatically handle default bom for specific encoding
        bomsMap.put(StandardCharsets.UTF_8, Character.toString((char) 0xEFBBBF));
        bomsMap.put(StandardCharsets.UTF_16BE, Character.toString((char) 0xFEFF));
        bomsMap.put(StandardCharsets.UTF_16LE, Character.toString((char) 0xFFFE));
        bomsMap.put(StandardCharsets.ISO_8859_1, null);
    }

    public ExportCharset(Charset charset, String bom) {
        this.charset = charset;
        this.bom = bom;
    }

    public ExportCharset(Charset charset) {
        this.charset = charset;
        this.bom = bomsMap.get(charset);
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getBom() {
        return this.bom;
    }
}
