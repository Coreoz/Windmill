Windmill
========

A Java 8 friendly library to import/export Excel and CSV files.

Getting started
---------------
Include Windmill in your project:
```xml
<dependency>
    <groupId>com.coreoz</groupId>
    <artifactId>windmill</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Import
------
Here is an import example:
```java
try (Stream<ImportRow> rowStream = Windmill.parse(FileSource.of(new FileInputStream("myfile.xlsx")))) {
	rowStream.skip(1).forEach(row -> {
		System.out.println("row n°" + row.rowIndex() + " col 'REF' value : " + row.cell("REF").asString());
	});
}
```

Export
------
TODO example
