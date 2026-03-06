package com.coreoz.windmill.exports.exporters.csv;

import com.coreoz.windmill.exports.config.ExportMapping;
import com.coreoz.windmill.files.BomCharset;
import com.opencsv.CSVWriter;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvExporter<T> {
    private final Iterable<T> rows;
    private final ExportMapping<T> mapping;
    private final ExportCsvConfig exportConfig;
    private CSVWriter csvWriter;

    public CsvExporter(Iterable<T> rows, ExportMapping<T> mapping, ExportCsvConfig exportConfig) {
        this.rows = rows;
        this.mapping = mapping;
        this.exportConfig = exportConfig;
    }

    /**
     * Write the export file in an existing {@link OutputStream}.
     * <p>
     * This {@link OutputStream} will not be closed automatically:
     * it should be closed manually after this method is called.
     *
     * @throws IOException if anything can't be written.
     */
    @SneakyThrows
    public OutputStream writeTo(OutputStream outputStream) {
        csvWriter = new CSVWriter(
            new OutputStreamWriter(outputStream, exportConfig.getCharset().getCharset()),
            exportConfig.getSeparator(),
            exportConfig.getQuoteChar(),
            exportConfig.getEscapeChar(),
            exportConfig.getLineEnd()
        );
        writeBom(outputStream);
        writeRows();
        return outputStream;
    }

    /**
     * @throws IOException if anything can't be written.
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writeTo(byteOutputStream);
        return byteOutputStream.toByteArray();
    }

    // internals

    @SneakyThrows
    private void writeBom(OutputStream outputStream) {
        BomCharset encodingCharset = exportConfig.getCharset();
        if (encodingCharset != null) {
            encodingCharset.writeBomBytes(outputStream);
        }
    }

    private void writeRows() {
        writeHeaderRow();

        for (T row : rows) {
            writeRow(row);
        }
    }

    private void writeHeaderRow() {
        List<String> headerColumn = mapping.headerColumns();
        if (!headerColumn.isEmpty()) {
            String[] csvRowValues = new String[headerColumn.size()];
            for (int i = 0; i < headerColumn.size(); i++) {
                csvRowValues[i] = sanitizeValue(headerColumn.get(i), stringValue(headerColumn.get(i)));
            }
            csvWriter.writeNext(csvRowValues, exportConfig.isApplyQuotesToAll());
        }
    }

    @SneakyThrows
    private void writeRow(T row) {
        String[] csvRowValues = new String[mapping.columnsCount()];
        List<String> headerColumns = mapping.headerColumns();
        for (int i = 0; i < mapping.columnsCount(); i++) {
            String columnName = i < headerColumns.size() ? headerColumns.get(i) : null;
            csvRowValues[i] = sanitizeValue(columnName, stringValue(mapping.cellValue(i, row)));
        }
        csvWriter.writeNext(csvRowValues, exportConfig.isApplyQuotesToAll());
        csvWriter.flush();
    }

    private String sanitizeValue(String columnName, String value) {
        if (
            exportConfig.isSanitizeFormulas()
                && (columnName == null || !exportConfig.getFieldNamesExcludedFromSanitization().contains(columnName))
                && isDangerousValue(value)
        ) {
            return "'" + value;
        }
        return value;
    }

    private static boolean isDangerousValue(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        char firstChar = value.charAt(0);
        return firstChar == '='
            || firstChar == '+'
            || firstChar == '-'
            || firstChar == '@'
            || firstChar == '\t'
            || firstChar == '\r';
    }

    private static String stringValue(final Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }

}
