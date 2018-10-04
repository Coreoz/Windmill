package com.coreoz.windmill;

import com.coreoz.windmill.exports.exporters.csv.CsvExporter;
import com.coreoz.windmill.exports.exporters.csv.ExportCsvConfig;
import com.coreoz.windmill.exports.exporters.excel.ExcelExporter;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.exports.mapping.ExportHeaderMapping;
import com.coreoz.windmill.exports.mapping.ExportMapping;
import com.coreoz.windmill.exports.mapping.NoHeaderDecorator;
import com.coreoz.windmill.utils.BeanPropertyUtils;
import org.apache.commons.collections4.map.LinkedMap;

import java.io.ByteArrayOutputStream;
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
    void writeInto(OutputStream outputStream);

    /**
     * @throws IOException if anything can't be written.
     */
    default byte[] toByteArray() {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writeInto(byteOutputStream);
        return byteOutputStream.toByteArray();
    }

    interface InitialState<T> {
        NamedValueMapperStage<T> withHeaders();
        ValueMapperStage<T> withoutHeaders();
        PresentationState<T> withExportMapping(ExportMapping<T> mapping);
    }

    interface ValueMapperStage<T> extends BaseValueMapperStage<T> {
        ValueMapperStage<T> column(Function<T, ?> applier);
        PresentationState<T> columns(Collection<Function<T, ?>> appliers);
    }

    interface NamedValueMapperStage<T> extends BaseValueMapperStage<T> {
        NamedValueMapperStage<T> column(String name, Function<T, ?> applier);
        PresentationState<T> columns(Map<String, Function<T, ?>> appliers);
    }

    interface BaseValueMapperStage<T> extends PresentationState<T> {
        PresentationState<T> withType(Class<T> beanClass);
    }

    interface PresentationState<T> {
        Exporter<T> asCsv();
        Exporter<T> asCsv(ExportCsvConfig config);
        Exporter<T> asExcel();
        Exporter<T> asExcel(ExportExcelConfig config);
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
        public PresentationState<T> withType(Class<T> beanClass) {
            LinkedMap<String, Function<T, ?>> accessor = BeanPropertyUtils.beanPropertiesAccessor(beanClass);
            this.headerMapping = new ExportHeaderMapping<>(accessor);
            return this;
        }

        @Override
        public PresentationState<T> withExportMapping(ExportMapping<T> mapping) {
            this.headerMapping = mapping;
            return this;
        }

        @Override
        public Exporter<T> asCsv() {
            return asCsv(ExportCsvConfig.builder().build());
        }

        @Override
        public Exporter<T> asCsv(ExportCsvConfig config) {
            return new CsvExporter<>(headerMapping, config);
        }

        @Override
        public Exporter<T> asExcel() {
            return asExcel(ExportExcelConfig.newXlsxFile().build());
        }

        @Override
        public Exporter<T> asExcel(ExportExcelConfig config) {
            return new ExcelExporter<>(headerMapping, config);
        }
    }
    
    static <T> InitialState<T> builder() {
        return new Builder<>(new LinkedMap<>());
    }
}
