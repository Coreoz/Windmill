Windmill
========

[![Build Status](https://github.com/Coreoz/Windmill/actions/workflows/maven.yml/badge.svg)](./actions)
[![Coverage Status](https://coveralls.io/repos/github/Coreoz/Windmill/badge.svg?branch=master)](https://coveralls.io/github/Coreoz/Windmill?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coreoz/windmill/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.coreoz/windmill)

Windmill is a library to parse or write Excel and CSV files through a fluent API.

Windmill targets the writing/parsing of List/Collection data.
It will especially stand out for use cases like exporting/importing a result set from/to a database.

It is based on the projects [Apache POI](https://poi.apache.org/)
and [OpenCSV](http://opencsv.sourceforge.net/) to manipulate Excel and CSV files.

At least Java 11 is required from version 2.0.0, whereas in versions 1.x.x, at least Java 8 is required.

Upgrade from 1.x.x to 2.x.x
---------------------------
See upgrade instructions in the [release details](https://github.com/Coreoz/Windmill/releases/tag/2.0.0).

Getting started
---------------
Include Windmill in your project:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>windmill</artifactId>
  <version>2.0.0</version>
</dependency>
```

Import/Parsing
--------------
Here is an import example:
```java
try (Stream<Row> rowStream = Windmill.parse(FileSource.of(new FileInputStream("myFile.xlsx")))) {
  rowStream
    // skip the header row that contains the column names
    .skip(1)
    .forEach(row -> {
      System.out.println(
        "row n°" + row.rowIndex()
        + " column 'User login' value : " + row.cell("User login").asString()
        + " column n°3 number value : " + row.cell(2).asDouble().value() // index is zero-based
      );
    });
}
```
Note that the `try` statement is required to close the `Stream` if the `InputStream` used should be closed.

Options can be passed to the parser.
For example with Excel workbooks, it is possible to select the spreadsheet to use,
or to specify that cell values should be trimmed:
```java
Stream<Row> rowStream = Parsers
  .xlsx("User sheet")
  .trimValues()
  .parse(FileSource.of(new FileInputStream("myFile.xlsx")));
```

With CSV files, it is possible to specify multiple parameters like the escape character:
```java
Stream<Row> rowStream = Parsers
  .csv(CsvParserConfig.builder().separator(';').build())
  .parse(FileSource.of(new FileInputStream("myFile.csv")));
```

The CSV parser will try to detect the encoding charset, but you can specify the fallback charset if none has been found.
The fallback charset should always be a charset without BOM, for instance BomCharset.UTF_8_NO_BOM,
otherwise the content of the CSV file will be stripped of the length of the BOM.
```java
Stream<Row> rowStream = Parsers
  .csv(CsvParserConfig.builder().fallbackCharset(BomCharset.ISO_8859_1).build())
  .parse(FileSource.of(new FileInputStream("myFile.csv")));
```

Export/Writing
--------------
Here is an export example:
```java
Windmill
  .export(Arrays.asList(bean1, bean2, bean3))
  .withHeaderMapping(
    new ExportHeaderMapping<Bean>()
      .add("Name", Bean::getName)
      .add("User login", bean -> bean.getUser().getLogin())
  )
  .asExcel()
  .writeTo(new FileOutputStream("Export.xlsx"));
```

Options can be passed to the exporter, for example with CSV files,
it is possible to specify multiple parameters like the separator character or the escape character:
```java
Windmill
  .export(Arrays.asList(bean1, bean2, bean3))
  .withNoHeaderMapping(Bean::getName, bean -> bean.getUser().getLogin())
  .asCsv(ExportCsvConfig.builder().separator(';').escapeChar('"').build());
  .toByteArray();
```

CSV Exporter will add by default an UTF-8 BOM in the file to allow reader such as Excel to detect the correct
encoding.
If you need to export the file without BOM, just specify an encoding charset without BOM :
```java
Windmill
  .export(Arrays.asList(bean1, bean2, bean3))
  .withNoHeaderMapping(Bean::getName, bean -> bean.getUser().getLogin())
  .asCsv(ExportCsvConfig.builder()
    .separator(';')
    .escapeChar('"')
    .charset(BomCharset.ISO_8859_1) // or BomCharset.UTF_8_NO_BOM for UTF-8
    .build());
  .toByteArray();
```

It is also possible to export multiple tabs in one Excel workbook:
```java
Workbook xlsxFile = new XSSFWorkbook();

Windmill
  .export(Arrays.asList(bean1, bean2, bean3))
  .withNoHeaderMapping(Bean::getName, bean -> bean.getUser().getLogin())
  .asExcel(ExportExcelConfig.fromWorkbook(xlsxFile).build("First tab"))
  .write();

Windmill
  .export(Arrays.asList(film1, film2))
  .withNoHeaderMapping(Film::getTitle, Film::getReleaseDate)
  .asExcel(ExportExcelConfig.fromWorkbook(xlsxFile).build("Second tab with films"))
  .write();

xlsxFile.write(new FileOutputStream("Export.xlsx"));
```

CSV export with memory optimization
-----------------------------------
Big file exports can sometimes fill up all the JVM memory available resulting of OutOfMemory exceptions.

To avoid that, it is best to use the CSV format which is a lot less memory consuming than Excel (~ 10 times more efficient).
Then, it is also possible to avoid loading the full CSV file in memory before returning it.
Here is a sample WS-RS API making sure all data are never fully loaded into memory:
```java
@GET
public Response generateExport() {
    // Using the StreamingOutput object to get a reference toward the output stream
    // ready to write the HTTP response body
    StreamingOutput fileStream = output -> {
        try {
            // If the data is fetched using a stream,
            // it means the whole data set will never be loaded in memory:
            // Windmill will stream the data along to the HTTP response output stream
            Stream<Bean> data = fetchData();
            Windmill
                .export(data)
                .withHeaderMapping(
                    new ExportHeaderMapping<Bean>()
                        .add("Name", Bean::getName)
                        .add("User login", bean -> bean.getUser().getLogin())
                )
                .asCsv()
                // Here the HTTP response output stream is referenced to write all the
                // CSV lines directly in it whenever they are generated
                .writeTo(output);
        } catch (Exception e) {
            logger.error("Could not generate export", e);
            return Response.serverError().entity("Export failed").build();
        }
    };
    return Response
        .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
        .header("content-disposition","attachment; filename = export.csv")
        .build();
}
```

This behavior will be available for Excel once https://github.com/Coreoz/Windmill/issues/3 is resolved.

Excel customization for exports
-------------------------------
Windmill enables full control over Excel sheets using the included `ExcelCellStyler` feature or using Apache POI.

See the [complete Excel customization for exports documentation](docs/excel-export-customization.md)
