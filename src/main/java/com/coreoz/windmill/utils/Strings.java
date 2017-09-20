package com.coreoz.windmill.utils;

public class Strings {

	public static String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String valueTrimed = value.trim();
		return "".equals(valueTrimed) ? null : valueTrimed;
	}

}
