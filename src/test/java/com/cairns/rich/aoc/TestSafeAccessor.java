package com.cairns.rich.aoc;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SafeAccessor}.
 */
public class TestSafeAccessor {
  private final String strVal = "This is a String";
  private final CharSequence charSeqVal = new StringBuilder("This is NOT a string");
  private final int[] intVals = { 1, 2, 3, 4, 5, 6, 7 };
  private final long[] longVals = { 1, 2, 3, 4, 5, 6 };
  private final boolean[] booleanVals = { false, true, false, false, true };
  private final char[] charVals = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
  private final String[] stringArrVals = { "a", "b", "c", "d" };
  private final List<String> stringLstVals = Arrays.asList("e", "f", "g", "h");

  /**
   * This test ensures that {@link SafeAccessor#safeCharAt(CharSequence, long)} works correctly
   * and accepts a general {@link CharSequence}s as well as specific {@link String}s.
   */
  @Test
  public void testSafeCharAt() {
    runSafeGetTest(strVal, strVal.length(), String::charAt, SafeAccessor::safeCharAt);
    runSafeGetTest(charSeqVal, charSeqVal.length(), CharSequence::charAt, SafeAccessor::safeCharAt);
  }

  /**
   * This test ensures that all safeGet methods work correctly.
   */
  @Test
  public void testSafeGet() {
    runSafeGetTest(intVals, intVals.length, (arr, i) -> arr[i], SafeAccessor::safeGet);
    runSafeGetTest(longVals, longVals.length, (arr, i) -> arr[i], SafeAccessor::safeGet);
    runSafeGetTest(booleanVals, booleanVals.length, (arr, i) -> arr[i], SafeAccessor::safeGet);
    runSafeGetTest(charVals, charVals.length, (arr, i) -> arr[i], SafeAccessor::safeGet);
    runSafeGetTest(stringArrVals, stringArrVals.length, (arr, i) -> arr[i], SafeAccessor::safeGet);
    runSafeGetTest(stringLstVals, stringLstVals.size(), List::get, SafeAccessor::safeGet);
  }

  /**
   * This test ensures that all safeSet methods work correctly.
   */
  @Test
  public void testSafeSet() {
    runSafeSetTest(() -> new int[7], 7, SafeAccessor::safeSet, (arr, i) -> arr[i], intVals);
    runSafeSetTest(() -> new long[6], 6, SafeAccessor::safeSet, (arr, i) -> arr[i], longVals);
    runSafeSetTest(() -> new boolean[5], 5, SafeAccessor::safeSet, (arr, i) -> arr[i], booleanVals);
    runSafeSetTest(() -> new char[8], 8, SafeAccessor::safeSet, (arr, i) -> arr[i], charVals);
    runSafeSetTest(() -> new String[4], 4, SafeAccessor::safeSet, (arr, i) -> arr[i], stringArrVals);
    runSafeSetTest(() -> Arrays.asList("", "", "", ""), 4, SafeAccessor::safeSet, List::get, stringLstVals);
  }

  /**
   * Runs a test to verify that a safeGet method works correctly.  Ensures that normal gets,
   * negative gets, as well as looping positive and negative index gets all work.
   */
  private <T, A> void runSafeGetTest(
      A arrList,
      int length,
      BiFunction<A, Integer, T> get,
      BiFunction<A, Long, T> safeGet
  ) {
    for (int i = 0; i < length; ++i) {
      Assertions.assertEquals(get.apply(arrList, i), safeGet.apply(arrList, (long) i), "Index " + i);
    }
    for (int i = -1; i >= -length; --i) {
      Assertions.assertEquals(get.apply(arrList, length + i), safeGet.apply(arrList, (long) i), "Index " + i);
    }
    T firstElem = get.apply(arrList, 0);
    Assertions.assertEquals(firstElem, safeGet.apply(arrList, (long) length));
    Assertions.assertEquals(firstElem, safeGet.apply(arrList, (long) length * 2));
    Assertions.assertEquals(firstElem, safeGet.apply(arrList, (long) length * 1322));
    Assertions.assertEquals(firstElem, safeGet.apply(arrList, (long) -length));
    Assertions.assertEquals(firstElem, safeGet.apply(arrList, (long) -length * 2));
    Assertions.assertEquals(firstElem, safeGet.apply(arrList, (long) -length * 1322));
  }

  /**
   * Runs a test to verify that a safeSet method works correctly.  Ensures that normal sets,
   * negative sets, as well as looping positive and negative index sets all work.
   */
  private <A, T> void runSafeSetTest(
      Supplier<A> newArrList,
      int length,
      SetFn<A, T> safeSet,
      BiFunction<A, Integer, T> get,
      A elems
  ) {
    A arrList = newArrList.get();
    for (int i = 0; i < length; ++i) {
      T expectedElem = get.apply(elems, i);
      safeSet.set(arrList, i, expectedElem);
      Assertions.assertEquals(expectedElem, get.apply(arrList, i), "Index " + i);
    }

    arrList = newArrList.get();
    for (int i = -1; i >= -length; --i) {
      T expectedElem = get.apply(elems, length + i);
      safeSet.set(arrList, i, expectedElem);
      Assertions.assertEquals(expectedElem, get.apply(arrList, length + i), "Index " + i);
    }

    arrList = newArrList.get();
    int[] indexes = { length, 2 * length, -length, -2 * length };
    for (int i = 0; i < indexes.length; ++i) {
      T expectedElem = get.apply(elems, i);
      safeSet.set(arrList, indexes[i], expectedElem);
      Assertions.assertEquals(expectedElem, get.apply(arrList, 0), "Index " + i);
    }
  }

  /**
   * Functional interface to capture the varying SafeAccessor.safeSet methods.
   */
  private interface SetFn<A, T> {
    /**
     * Sets the given element at the index corresponding to the unmoded index in the given array or list.
     */
    void set(A arrList, long unmodedIndex, T elem);
  }
}
