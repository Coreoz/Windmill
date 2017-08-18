package com.coreoz.windmill;

public interface Row extends Iterable<Cell> {

	int index();

	Cell cell(String columnName);

	Cell cell(Integer columnIndex);

}
