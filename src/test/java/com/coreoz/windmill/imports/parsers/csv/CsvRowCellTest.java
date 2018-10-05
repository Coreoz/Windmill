package com.coreoz.windmill.imports.parsers.csv;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.FileSchema;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvRowCellTest {

    private static final CsvRow ROW = new CsvRow(
            3,
            new FileSchema(Arrays.asList(
                new CsvCell(0, "name"),
                new CsvCell(1, "integer"),
                new CsvCell(2, "fractional"))),
            new String[]{"foobar", "42", "0.001"});

    @Test
    public void check_for_correct_row_index() {
        assertThat(ROW.rowIndex()).isEqualTo(3);
    }

    @Test
    public void iterator_should_return_correct_amount_of_entries() {
        assertThat(ROW.iterator()).hasSize(3);
    }

    @Test
    public void should_discover_columns() {
        assertThat(ROW.columnExists("name")).isTrue();
        assertThat(ROW.columnExists("integer")).isTrue();
        assertThat(ROW.columnExists("foobar")).isFalse();
    }

    @Test
    public void should_discover_integer_number() {
        Cell cell = ROW.cell(1);

        assertThat(cell.asInteger().isNull()).isFalse();
        assertThat(cell.asInteger().value()).isEqualTo(42);
        assertThat(cell.asInteger().safeValue()).isEqualTo(42);

        assertThat(cell.asLong().isNull()).isFalse();
        assertThat(cell.asLong().value()).isEqualTo(42);
        assertThat(cell.asLong().safeValue()).isEqualTo(42);
    }

    @Test
    public void should_discover_fractional_number() {
        Cell cell = ROW.cell("fractional");

        assertThat(cell.asString()).isEqualTo("0.001");

        assertThat(cell.asDouble().isNull()).isFalse();
        assertThat(cell.asDouble().value()).isEqualTo(0.001d);
        assertThat(cell.asDouble().safeValue()).isEqualTo(0.001d);

        assertThat(cell.asFloat().isNull()).isFalse();
        assertThat(cell.asFloat().value()).isEqualTo(0.001f);
        assertThat(cell.asFloat().safeValue()).isEqualTo(0.001f);

        assertThat(cell.asInteger().isNull()).isFalse();
        assertThat(cell.asInteger().safeValue()).isNull();

        assertThat(cell.asLong().isNull()).isFalse();
        assertThat(cell.asLong().safeValue()).isNull();
    }

    @Test(expected = NumberFormatException.class)
    public void should_throw_exception_on_unsafe_operation() {
        Cell cell = ROW.cell(2);
        assertThat(cell.columnIndex()).isEqualTo(2);

        cell.asInteger().value();
    }
}