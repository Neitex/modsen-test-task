package com.neitex.bookstoreservice.util;

public class NullUtils {
  public static boolean anyNull(Object... objects) {
    for (Object object : objects) {
      if (object == null) {
        return true;
      }
    }
    return false;
  }
}
