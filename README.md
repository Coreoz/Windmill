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
  <version>1.2.0</version>
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
    .<Bean>exporter()
    .withHeaders()
    .column("Name", Bean::getName)
    .column("User login", bean -> bean.getUser().getLogin())
    .asExcel()
    .writeRows(Arrays.asList(bean1, bean2, bean3))
    .writeInto(new FileOutputStream("Export.xlsx"));
```
And an import example:
```java
Windmill
    .importer()
    .source(FileSource.of(new FileInputStream("Export.xlsx")))
    .withHeaders()
    .stream()
    .map(row -> new Bean(row.cell("Name").asString(), new User(row.cell(1).asString())))
    .collect(Collectors.toList());
```

Options can be passed to the exporter, for example with CSV files,
it is possible to specify multiple parameters like the separator character or the escape character:
```java
Windmill
    .<Bean>exporter()
    .withoutHeaders()
    .column(Bean::getName)
    .column(bean -> bean.getUser().getLogin())
    .asCsv(ExportCsvConfig.builder()
            .separator(';')
            .escapeChar('"')
            .build())
    .writeRows(Arrays.asList(bean1, bean2, bean3))
    .toByteArray();
```
```java
Windmill
    .importer()
    .source(FileSource.of(bytes))
    .parser(Parsers.csv(CsvParserConfig.builder()
            .separator(';')
            .escapeChar('"')
            .quoteChar('\'')
            .build()))
    .withoutHeaders()
    .stream()
    .map(row -> new Bean(row.cell(0).asString(), new User(row.cell(1).asString())))
    .collect(Collectors.toList());
```

It is also possible to export multiple tabs in one Excel workbook:
```java
Workbook xlsxFile = new XSSFWorkbook();

Windmill
    .<Bean>exporter()
    .withoutHeaders()
    .column(Bean::getName)
    .column(bean -> bean.getUser().getLogin())
    .asExcel(ExportExcelConfig.fromWorkbook(xlsxFile)
            .build("First tab"))
    .writeRows(Arrays.asList(bean1, bean2, bean3));

Windmill
    .<Film>exporter()
    .withoutHeaders()
    .column(Film::getTitle)
    .column(Film::getReleaseDate)
    .asExcel(ExportExcelConfig.fromWorkbook(xlsxFile)
            .build("Second tab with films"))
    .writeRow(film1)
    .writeRow(film2);

xlsxFile.write(new FileOutputStream("Export.xlsx"));
```
```java
Windmill
    .importer()
    .source(FileSource.of(new FileInputStream("Export.xlsx")))
    .parser(Parsers.xlsx("First tab"))
    .withoutHeaders()
    .stream()
    .map(row -> new Bean(row.cell(0).asString(), new User(row.cell(1).asString())))
    .collect(Collectors.toList());

Windmill
    .importer()
    .source(FileSource.of(new FileInputStream("Export.xlsx")))
    .parser(Parsers.xlsx("Second tab with films"))
    .withoutHeaders()
    .stream()
    .map(row -> {
        String title = row.cell(0).asString();
        // TODO: create convenient method for custom types
        Date releaseDate = DateUtil.getJavaDate(row.cell(1).asDouble().value());
        return new Film(title, releaseDate);
    })
    .collect(Collectors.toList());
```
