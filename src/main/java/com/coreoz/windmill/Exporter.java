package com.coreoz.windmill;

import com.coreoz.windmill.exports.exporters.csv.CsvExporter;
import com.coreoz.windmill.exports.exporters.csv.ExportCsvConfig;
import com.coreoz.windmill.exports.exporters.excel.ExcelExporter;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.exports.mapping.ExportHeaderMapping;
import com.coreoz.windmill.exports.mapping.ExportMapping;
import com.coreoz.windmill.exports.mapping.NoHeaderDecorator;
import org.apache.commons.collections4.map.LinkedMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Exporter<T> extends Consumer<T> {

    Exporter<T> writeRow(T row);
    Exporter<T> writeRows(Iterable<T> rows);

    default void accept(T row) {
        writeRow(row);
    }

    /**
     * Write the exported state into an existing {@link OutputStream}.
     *
     * This {@link OutputStream} will not be closed automatically:
     * it should be closed manually after this method is called.
     *
     * @throws IOException if anything can't be written.
     */
    Exporter<T> writeInto(OutputStream outputStream);

    /**
     * @throws IOException if anything can't be written.
     */
    byte[] toByteArray();

    interface InitialState<T> {
        NamedValueMapperStage<T> withHeaders();
        ValueMapperStage<T> withoutHeaders();
        PresentationState<T> withExportMapping(ExportMapping<T> mapping);
    }

    interface ValueMapperStage<T> extends PresentationState<T> {
        ValueMapperStage<T> column(Function<T, ?> applier);
        PresentationState<T> columns(Collection<Function<T, ?>> appliers);
    }

    interface NamedValueMapperStage<T> extends PresentationState<T> {
        NamedValueMapperStage<T> column(String name, Function<T, ?> applier);
        PresentationState<T> columns(Map<String, Function<T, ?>> appliers);
    }

    interface PresentationState<T> {
        CsvExporter<T> asCsv();
        CsvExporter<T> asCsv(ExportCsvConfig config);
        ExcelExporter<T> asExcel();
        ExcelExporter<T> asExcel(ExportExcelConfig config);
    }
    
    class Builder<T> implements InitialState<T>, ValueMapperStage<T>, NamedValueMapperStage<T>, PresentationState<T> {

        private final LinkedMap<String, Function<T, ?>> toValues;

        private ExportMapping<T> headerMapping;

        public Builder(LinkedMap<String, Function<T, ?>> toValues) {
            this.toValues = toValues;
        }

        @Override
        public NamedValueMapperStage<T> withHeaders() {
            this.headerMapping = new ExportHeaderMapping<>(toValues);
            return this;
        }

        @Override
        public ValueMapperStage<T> withoutHeaders() {
            this.headerMapping = new NoHeaderDecorator<>(new ExportHeaderMapping<>(toValues));
            return this;
        }

        @Override
        public ValueMapperStage<T> column(Function<T, ?> applier) {
            toValues.put(applier.toString(), applier);
            return this;
        }

        @Override
        public PresentationState<T> columns(Collection<Function<T, ?>> appliers) {
            for (Function<T, ?> applier : appliers) {
                column(applier);
            }

            return this;
        }

        @Override
        public NamedValueMapperStage<T> column(String name, Function<T, ?> applier) {
            toValues.put(name, applier);
            return this;
        }

        @Override
        public PresentationState<T> columns(Map<String, Function<T, ?>> appliers) {
            toValues.putAll(appliers);
            return this;
        }

        @Override
        public PresentationState<T> withExportMapping(ExportMapping<T> mapping) {
            this.headerMapping = mapping;
            return this;
        }

        @Override
        public CsvExporter<T> asCsv() {
            return asCsv(ExportCsvConfig.builder().build());
        }

        @Override
        public CsvExporter<T> asCsv(ExportCsvConfig config) {
            return new CsvExporter<>(headerMapping, config);
        }

        @Override
        public ExcelExporter<T> asExcel() {
            return asExcel(ExportExcelConfig.newXlsxFile().build());
        }

        @Override
        public ExcelExporter<T> asExcel(ExportExcelConfig config) {
            return new ExcelExporter<>(headerMapping, config);
        }
    }
    
    static <T> InitialState<T> builder() {
        return new Builder<>(new LinkedMap<>());
    }
}
