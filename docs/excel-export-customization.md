# Excel customization for exports with Windmill

Windmill provides flexibility for advanced Excel customization, allowing you to go beyond the standard export capabilities. This includes applying custom styles to cells, rows, and columns, using the built-in `ExcelCellStyler` feature or by manipulating the Excel workbook directly with Apache POI after the export.

## Using `ExcelCellStyler` for cell-level styling

Windmill offers the `ExcelCellStyler` interface to define how individual cells are styled during the export process. This provides a way to apply consistent styling across your exported data.

The `ExcelCellStyler` interface (from `ExcelCellStyler.java`) defines two main methods:

* `initialize(Workbook workbook)`: Called once before styling any cells, allowing you to set up any shared styles or resources (e.g., creating `CellStyle` objects).
* `style(Cell cell)`: Called for each cell created by Windmill, allowing you to apply your desired style to that cell.

Windmill provides two built-in implementations:

* `ExcelCellStyler.bordersStyle()`: Applies thin borders to all cells. This is implemented in `ExcelCellStylerBorders.java`. This is the default applied style in Windmill.
* `ExcelCellStyler.emptyStyle()`: Applies no specific style, leaving cells as default or allowing for completely manual styling later.

**How to use `ExcelCellStyler`:**

You can provide an `ExcelCellStyler` implementation when configuring your Excel export:

```java
Windmill
    .export(rows)
    .withHeaderMapping(headerMapping)
    .asExcel(ExportExcelConfig
        .newXlsxFile()
        .build()
        // Option 1: Use a predefined styler for borders
        // .withCellStyler(ExcelCellStyler.bordersStyle())

        // Option 2: Use the empty styler if you plan to do all styling manually post-export
        // or if you only want to apply specific styles to certain elements later.
        .withCellStyler(ExcelCellStyler.emptyStyle())

        // Option 3: Provide your custom implementation
        // .withCellStyler(new MyCustomCellStyler())
    )
    .write();
```

**Creating a custom `ExcelCellStyler`:**

If you need more specific styling logic, you can implement the `ExcelCellStyler` interface.

```java
import com.coreoz.windmill.exports.exporters.excel.ExcelCellStyler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Font;

public class MyCustomCellStyler implements ExcelCellStyler {
    private CellStyle headerStyle;
    private CellStyle dataStyle;

    @Override
    public void initialize(Workbook workbook) {
        // Initialize header style
        headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Initialize data style (e.g., for non-header cells)
        dataStyle = workbook.createCellStyle();
        // Add other data cell styling if needed, e.g., borders
    }

    @Override
    public void style(Cell cell) {
        // Example: Apply headerStyle to the first row, dataStyle to others
        if (cell.getRowIndex() == 0) { // Assuming the first row is the header
            cell.setCellStyle(headerStyle);
        } else {
            // Potentially apply a default data style
            // cell.setCellStyle(dataStyle);
            // Or apply conditional styling based on cell content/column
            // if (cell.getColumnIndex() == 2 && cell.getNumericCellValue() < 0) {
            //     // apply a specific style for negative numbers in column 2
            // }
        }
    }
}
```

Using `ExcelCellStyler` is the recommended approach for applying styles that Windmill should handle as it creates cells. For more complex manipulations or styles applied *after* the initial data export, direct Apache POI manipulation can be used.

## Direct Apache POI manipulation (post-export)

For styling or structural changes that go beyond what `ExcelCellStyler` is designed for, or if you prefer to modify the workbook after Windmill has processed it, you can directly use Apache POI.

### General Workflow for Advanced Customization

1. Create or load an Excel sheet: `Workbook xlsxFile = new XSSFWorkbook()`
2. Use Windmill to perform the export using that existing: `Windmill.[...].asExcel(ExportExcelConfig.fromWorkbook(xlsxFile).build()).write()`
3. Access the sheet where the export has been performed: `Sheet currentSheet = xlsxFile.getSheet("SheetName")`
4. Apply Custom Operations using Apache POI (see the two examples bellow)
5. Write the file `xlsxFile.write(new FileOutputStream(new File("export.xlsx")))`

So the whole process will look like this:
```java
Workbook xlsxFile = new XSSFWorkbook();
Windmill
    .export(rows)
    .withHeaderMapping(
        new ExportHeaderMapping<Bean>()
            .add("Name", Bean::getName)
    )
    .asExcel(ExportExcelConfig
        .fromWorkbook(xlsxFile)
        .build("Sheet name")
        // When applying custom styling, the need is to often go from an empty style
        .withCellStyler(ExcelCellStyler.emptyStyle())
    )
    .write();

Sheet currentSheet = xlsxFile.getSheet(exportCraConfig.projectName());
// See bellow for example implementation
CustomExcelStyling.apply(currentSheet);
xlsxFile.write(new FileOutputStream(new File("export.xlsx")));
```

### Example: highlighting a row
The following example demonstrates how to highlight the first row of a sheet with a specific background color and bold font.

```java
// In the CustomExcelStyling class
private static byte[] lightBlueColor = new byte[] { (byte) 232,(byte) 240,(byte) 255 };

public static void highlightFirstRow(Sheet sheet) {
    var firstRow = sheet.getRow(0);
    applyRowStyle(firstRow, lightBlueColor);
}

private static void applyRowStyle(Row row, byte[] color) {
    XSSFWorkbook workBook = (XSSFWorkbook) row.getSheet().getWorkbook();

    XSSFFont boldFont = workBook.createFont();
    boldFont.setBold(true);

    XSSFColor xssfColor = new XSSFColor(color, workBook.getStylesSource().getIndexedColors());
    var rowStyle = workBook.createCellStyle();
    rowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    rowStyle.setFillBackgroundColor(xssfColor);
    rowStyle.setFillForegroundColor(xssfColor);
    rowStyle.setFont(boldFont);

    for (var cell : (Iterable<Cell>) row::cellIterator) {
        cell.setCellStyle(rowStyle);
    }
}
```

Usage: `CustomExcelStyling.highlightFirstRow(currentSheet)`

## Example: adding a formula
The following example demonstrates how to add a "Total" row using the SUM formula (and light green highlighting).

```java
// In the CustomExcelStyling class
private static byte[] lightGreenColor = new byte[] { (byte) 217, (byte) 234, (byte) 211 };

public static void addSumsRow(Sheet sheet, int columnsCount, int lineToCount) {
    var sumsRow = sheet.createRow(lineToCount + 3);
    sumsRow.createCell(0).setCellValue("Total");
    for (int columnIndex = 1; columnIndex < columnsCount; columnIndex++) {
        // Assuming the last 3 columns in this example are for totals, adjust as needed
        if (columnIndex < columnsCount - 3) {
            sumsRow.createCell(columnIndex); // Create empty cells if not a sum column
        } else {
            setSumFormula(sumsRow, columnIndex, lineToCount);
        }
    }
    applyRowStyle(sumsRow, lightGreenColor); // Apply styling to the new sums row
}

private static void setSumFormula(Row sumsRow, int columnIndex, int lineToCount) {
    var cellFormula = sumsRow.createCell(columnIndex);
    char columnLetter = (char) ('A' + columnIndex);
    // Example: SUM(B2:B101) if lineToCount is 100 and columnIndex is 1 (B)
    cellFormula.setCellFormula("SUM(" + columnLetter + "2:" + columnLetter + (lineToCount + 1) + ")");
}
```

Usage: `CustomExcelStyling.addSumsRow(currentSheet, headerMapping.columnsCount(), rows.size())`
