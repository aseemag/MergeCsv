package com.readcsv.util;

public class CommonUtil {

	public static boolean isNull(Object obj) {
		return (obj == null);
	}

	public static boolean isNullOrBlank(String s) {
		boolean flag = true;
		if (!isNull(s)) {
			if (!s.trim().equals(GlobalConstants.BLANK_STRING)
					&& !s.equalsIgnoreCase("null")) {
				flag = false;
			}
		}
		return flag;
	}
}
