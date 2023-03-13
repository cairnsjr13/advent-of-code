package com.cairns.rich.aoc;

import com.cairns.rich.aoc.Base.HasId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for Enums.
 */
public class EnumUtils {
  private static final Map<Class<?>, Object[]> enumValuesCache = new HashMap<>();

  /**
   * Acts as a replacement for Enum.values().  The issue with this method is that it returns a new
   * array every time, which can be a memory concern.  Using this method will cache the values array
   * and have the same behavior, as long as the array is not modified.  The caching is never cleared.
   */
  public static <T> T[] enumValues(Class<T> type) {
    @SuppressWarnings("unchecked")
    T[] values = (T[]) enumValuesCache.computeIfAbsent(type, (t) -> t.getEnumConstants());
    return values;
  }

  /**
   * Generates a lookup table for the given class based on its Id.
   */
  public static <T, E extends HasId<T>> Map<T, E> getLookup(Class<E> type) {
    return Base.getLookup(Arrays.asList(enumValues(type)));
  }
}
