package com.coreoz.windmill;

import com.coreoz.windmill.exports.exporters.csv.CsvExporter;
import com.coreoz.windmill.exports.exporters.excel.ExcelExporter;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StreamingExamples {

    @Test
    public void should_stream_csv_without_intermediate_state_buffer() {
        CountingOutputStream outputStream = new CountingOutputStream(new NullOutputStream());
        CsvExporter<Import> exporter = initial().asCsv()
                .writeInto(outputStream);

        generate(100_000).forEach(exporter);
        assertThat(outputStream.getByteCount()).isGreaterThan(4_000_000);

        try {
            exporter.toByteArray();
            fail("unreachable");
        } catch (Exception e) {
            assertThat(e).hasNoCause()
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("The state has already been flushed to another output stream");
        }
    }

    @Test
    public void should_stream_csv_with_intermediate_state_buffer() {
        CsvExporter<Import> exporter = initial().asCsv();

        generate(10_000).forEach(exporter);
        assertThat(exporter.toByteArray().length).isGreaterThan(300_000);
    }

    @Test
    public void should_stream_xls_with_intermediate_state_buffer() {
        CountingOutputStream outputStream = new CountingOutputStream(new NullOutputStream());
        ExcelExporter<Import> exporter = initial().asExcel()
                .writeInto(outputStream);

        generate(1_000).forEach(exporter);

        // by default workbook doesn't support async flushing of internal state
        assertThat(outputStream.getByteCount()).isLessThan(10_000);
        exporter.writeInto(outputStream);
        assertThat(outputStream.getByteCount()).isGreaterThan(30_000);
    }

    @Test
    public void should_stream_xls_without_inmemory_intermediate_state_buffer() {
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(), 100);
        ExcelExporter<Import> exporter = initial()
                .asExcel(ExportExcelConfig.fromWorkbook(workbook).build());

        generate(100_000).forEach(exporter);

        CountingOutputStream outputStream = new CountingOutputStream(new NullOutputStream());
        exporter.writeInto(outputStream);
        assertThat(outputStream.getByteCount()).isGreaterThan(2_500_000);
        workbook.dispose();
        closeQuietly(workbook);
    }

    @Test
    public void should_fail_on_processing_after_first_flushing() {
        CountingOutputStream outputStream = new CountingOutputStream(new NullOutputStream());
        SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(), 100);
        ExcelExporter<Import> exporter = initial()
                .asExcel(ExportExcelConfig.fromWorkbook(workbook).build())
                .writeInto(outputStream);

        try {
            generate(1_000).forEach(exporter);
        } catch (Exception e) {
            assertThat(e).hasCauseInstanceOf(IOException.class)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Stream closed");
        } finally {
            workbook.dispose();
            closeQuietly(workbook);
        }
    }

    private Exporter.PresentationState<Import> initial() {
        return Windmill.<Import>exporter()
                    .withHeaders()
                    .columns(getAppliers());
    }

    private LinkedHashMap<String, Function<Import, ?>> getAppliers() {
        return new LinkedHashMap<String, Function<Import, ?>>() {{
            put("a", Import::getA);
            put("b", Import::getB);
            put("c", Import::getC);
            put("d", Import::getDoubleNumber);
            put("e", Import::getIntegerNumber);
        }};
    }

    private Stream<Import> generate(int limit) {
        return IntStream.range(0, limit)
                .mapToObj(i -> Import.builder()
                        .a("a" + i)
                        .b("b" + i)
                        .c("c" + i)
                        .integerNumber(i)
                        .doubleNumber(((double) i * 3) / 2)
                        .build());
    }
}
