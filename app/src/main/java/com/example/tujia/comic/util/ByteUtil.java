package com.example.tujia.comic.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.google.common.base.Strings;

/**
 * Utility of byte.
 *
 * @author wlei
 */
public class ByteUtil {

  public static final String STRING_UTF8 = "UTF-8";
  public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

  public static final byte[] fromString(String value) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }
    try {
      return value.getBytes(STRING_UTF8);
    } catch (final UnsupportedEncodingException e) {
      return value.getBytes();
    }
  }

  public static boolean isEmpty(byte[] bytes) {
    return (bytes == null) || (bytes.length == 0);
  }

  public static final ByteBuffer toByteBuffer(byte[] bytes) {
    return (bytes == null) ? null : ByteBuffer.wrap(bytes);
  }

  public static final ByteBuffer toByteBuffer(String value) {
    final byte[] bytes = fromString(value);
    return (bytes == null) ? null : ByteBuffer.wrap(bytes);
  }

  public static byte[] toBytes(ByteBuffer byteBuffer) {
    if (byteBuffer == null) {
      return null;
    }
    final int length = byteBuffer.limit() - byteBuffer.position();
    final byte[] bytes = new byte[length];
    try {
      byteBuffer.get(bytes);
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return bytes;
  }

  public static final String toString(byte[] bytes) {
    if ((bytes == null) || (bytes.length == 0)) {
      return null;
    }
    try {
      return new String(bytes, STRING_UTF8);
    } catch (final UnsupportedEncodingException e) {
      return new String(bytes);
    }
  }

  public static String toString(ByteBuffer name) {
    return toString(toBytes(name));
  }
}
