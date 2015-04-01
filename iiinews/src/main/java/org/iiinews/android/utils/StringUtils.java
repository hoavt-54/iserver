package org.iiinews.android.utils;

public class StringUtils {
	public static boolean isEmpty (String s){
		return s == null || s.length() == 0;
	}
	public static String unescapeHtml(String url){
		return url.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
	}
}
