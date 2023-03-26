package com.cairns.rich.aoc;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;

/**
 * Helper class to centralize a test verifying attributes that should be unique across all objects.
 */
public final class TestUniqueness {
  private TestUniqueness() { }

  /**
   * This test ensures that the given array of objects have no collisions in their attributes acquired through
   * the given function.  Users of this test should include it in their test class and annotate it accordingly.
   */
  public static <T, U> void test(T[] objs, Function<T, U> toUniqueValue) {
    Set<U> uniques = new HashSet<>();
    for (T obj : objs) {
      Assertions.assertTrue(
          uniques.add(toUniqueValue.apply(obj)),
          "Duplicate value for " + obj.getClass().getName() + "." + obj
      );
    }
  }
}
