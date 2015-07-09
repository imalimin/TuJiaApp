package com.example.tujia.comic.util;



/**
 * Utility for String.
 *
 * @author Wlei 2012-5-13
 */
public class StringUtil {

  public static boolean isNullOrEmpty(String value) {
    return (value == null) || (value.length() == 0);
  }
  
  public static String[] concat(String[] a, String[] b) {
    String[] c= new String[a.length+b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
 }
  
}
