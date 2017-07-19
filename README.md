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
    <version>1.0.0-beta1</version>
</dependency>
```

```
Windmill
	.newImport()
	[.skip(nbLines)]
	.map(row -> {
		row.col(i)
		row.col("name")
		
		return result;
	})
	.parse(Source.of(), [ParserFactory])
	[.fetch()]
	[.stream()]


Import<Result> {
	linesCount()
	rows() : Row<Result> {
		errors: 
	}
	errors:
}


Tuple {
	col(i)	.asInt
			.asString
}
```
