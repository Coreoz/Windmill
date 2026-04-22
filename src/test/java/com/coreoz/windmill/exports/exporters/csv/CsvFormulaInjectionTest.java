package com.coreoz.windmill.exports.exporters.csv;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import lombok.Getter;
import org.junit.Test;

import com.coreoz.windmill.Windmill;
import com.coreoz.windmill.exports.config.ExportHeaderMapping;

public class CsvFormulaInjectionTest {

    @Test
    public void should_sanitize_formula_injection_by_default() {
        String formula = "=SUM(1,2)";
        byte[] csvExport = Windmill
            .export(Collections.singletonList(new RowData(formula)))
            .withHeaderMapping(new ExportHeaderMapping<RowData>().add("col", RowData::getValue))
            .asCsv()
            .toByteArray();

        String result = new String(csvExport, StandardCharsets.UTF_8);
        // On s'attend à ce que la formule soit préfixée par '
        assertThat(result).contains("'=SUM(1,2)");
    }

    @Test
    public void should_sanitize_other_dangerous_characters() {
        for (String prefix : Arrays.asList("+", "-", "@", "\t", "\r")) {
            String value = prefix + "danger";
            byte[] csvExport = Windmill
                .export(Collections.singletonList(new RowData(value)))
                .withHeaderMapping(new ExportHeaderMapping<RowData>().add("col", RowData::getValue))
                .asCsv()
                .toByteArray();

            String result = new String(csvExport, StandardCharsets.UTF_8);
            assertThat(result).as("Should sanitize value starting with " + prefix).contains("'" + prefix + "danger");
        }
    }

    @Test
    public void should_not_sanitize_formula_if_disabled_globally() {
        String formula = "=SUM(1,2)";
        byte[] csvExport = Windmill
            .export(Collections.singletonList(new RowData(formula)))
            .withHeaderMapping(new ExportHeaderMapping<RowData>().add("col", RowData::getValue))
            .asCsv(
                ExportCsvConfig.builder()
                    .disableFormulasSanitization().build()
            )
            .toByteArray();

        String result = new String(csvExport, StandardCharsets.UTF_8);
        assertThat(result)
            .contains("\"=SUM(1,2)\"")
            .doesNotContain("'=SUM(1,2)");
    }

    @Test
    public void should_not_sanitize_formula_for_specific_fields() {
        String formula = "=SUM(1,2)";
        byte[] csvExport = Windmill
            .export(Collections.singletonList(new RowData(formula)))
            .withHeaderMapping(new ExportHeaderMapping<RowData>().add("safe", RowData::getValue).add("unsafe", RowData::getValue))
            .asCsv(
                ExportCsvConfig.builder()
                    .disableFormulasSanitizationForFields("safe").build()
            )
            .toByteArray();

        String result = new String(csvExport, StandardCharsets.UTF_8);
        assertThat(result)
            // "safe" column should not be sanitized
            .contains("\"=SUM(1,2)\"")
            // "unsafe" column should be sanitized
            .contains("\"'=SUM(1,2)\"");
    }

    private static class RowData {
        @Getter
        private final String value;

        public RowData(String value) {
            this.value = value;
        }
    }
}
