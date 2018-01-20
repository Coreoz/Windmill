Windmill
========

[![Build Status](https://travis-ci.org/Coreoz/Windmill.svg?branch=master)](https://travis-ci.org/Coreoz/Windmill)
[![Coverage Status](https://coveralls.io/repos/github/Coreoz/Windmill/badge.svg?branch=master)](https://coveralls.io/github/Coreoz/Windmill?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coreoz/windmill/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.coreoz/windmill)

Windmill is a library to parse or write Excel and CSV files through a fluent API
that takes advantage of Java 8 Stream and Lambda features.

Windmill targets the writing/parsing of List/Collection data.
It will especially stand out for use cases like exporting/importing a result set from/to a database.

It is based on the projects [Apache POI](https://poi.apache.org/)
and [OpenCSV](http://opencsv.sourceforge.net/) to manipulate Excel and CSV files.

Getting started
---------------
Include Windmill in your project:
```xml
<dependency>
  <groupId>com.coreoz</groupId>
  <artifactId>windmill</artifactId>
  <version>1.1.0</version>
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

With CSV files, it is possible to specify multiple parameters like the charset or the escape character:
```java
Stream<Row> rowStream = Parsers
  .csv(CsvParserConfig.builder().charset(StandardCharsets.UTF_8).separator(';').build())
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
