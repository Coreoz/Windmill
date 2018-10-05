package com.coreoz.windmill.imports.parsers.excel;

import com.coreoz.windmill.imports.Cell;
import com.coreoz.windmill.imports.FileSchema;
import com.coreoz.windmill.utils.IteratorStreams;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ExcelRowCellTest {

    private static ExcelRow ROW;

    @BeforeClass
    public static void initializeRow() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();

        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0, CellType.STRING).setCellValue("name");
        headerRow.createCell(1, CellType.STRING).setCellValue("integer");
        headerRow.createCell(2, CellType.STRING).setCellValue("formula");

        HSSFRow dataRow = sheet.createRow(1);
        dataRow.createCell(0, CellType.STRING).setCellValue("foobar");
        dataRow.createCell(1, CellType.FORMULA).setCellFormula("SQRT(3 * 3)");
        dataRow.createCell(2, CellType.FORMULA).setCellFormula("SQRT(0.001 * 0.001)");

        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

        List<Cell> headerCells = IteratorStreams.stream(headerRow.cellIterator())
                .map(c -> new ExcelCell(c.getColumnIndex(), c, false))
                .collect(Collectors.toList());

        ROW = new ExcelRow(sheet.getRow(1), new FileSchema(headerCells), false);
    }

    @Test
    public void check_for_correct_row_index() {
        assertThat(ROW.rowIndex()).isEqualTo(1);
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

        assertThat(cell.asString()).isEqualTo("3");

        assertThat(cell.asInteger().isNull()).isFalse();
        assertThat(cell.asInteger().value()).isEqualTo(3);
        assertThat(cell.asInteger().safeValue()).isEqualTo(3);

        assertThat(cell.asLong().isNull()).isFalse();
        assertThat(cell.asLong().value()).isEqualTo(3);
        assertThat(cell.asLong().safeValue()).isEqualTo(3);
    }

    @Test
    public void should_discover_fractional_number() {
        Cell cell = ROW.cell("formula");

        assertThat(cell.asString()).isEqualTo("0.001");
        assertThat(cell.toString()).isEqualTo("0.001");

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

    @Test
    public void weird_behaviour_compared_with_csv_cell() {
        Cell cell = ROW.cell(2);
        assertThat(cell.columnIndex()).isEqualTo(2);

        Integer value = cell.asInteger().value();
        assertThat(value).isEqualTo(0);
    }
}