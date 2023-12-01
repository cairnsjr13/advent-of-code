package com.cairns.rich.aoc._2023;

import com.cairns.rich.aoc.Loader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * We need to clean up the map's calibration values.  The first and last numbers will give us the actual calibration values.
 */
class Day01 extends Base2023 {
  private static final Map<String, Integer> digitLookup = new HashMap<>();
  private static final Map<String, Integer> digitAndEnglishLookup = new HashMap<>();
  private static final BiPredicate<Integer, Integer> leftmost = (test, betterThan) -> test < betterThan;
  private static final BiPredicate<Integer, Integer> rightmost = (test, betterThan) -> test > betterThan;

  static {
    String[] english = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
    for (int i = 0; i < english.length; ++i) {
      digitLookup.put(Integer.toString(i), i);
      digitAndEnglishLookup.put(english[i], i);
    }
    digitAndEnglishLookup.putAll(digitLookup);
  }

  /**
   * Returns the sum of the calibration values based on the first and last numerical digit.
   */
  @Override
  protected Object part1(Loader loader) {
    return findCalibrationSum(loader, digitLookup);
  }

  /**
   * Returns the sum of the calibration values based on the first and last numerical/English digit.
   */
  @Override
  protected Object part2(Loader loader) {
    return findCalibrationSum(loader, digitAndEnglishLookup);
  }

  /**
   * Helper method to compute the answer with the given lookup map containing valid symbols to search for.
   */
  private long findCalibrationSum(Loader loader, Map<String, Integer> lookup) {
    return loader.ml().stream().mapToLong(
        (line) -> 10 * findEdgeDigit(line, lookup, Integer.MAX_VALUE, String::indexOf, leftmost)
                + findEdgeDigit(line, lookup, -1, String::lastIndexOf, rightmost)
    ).sum();
  }

  /**
   * Finds the digit on an edge of the given line by finding the search key in the lookup map with most extreme
   * index.  The index search is configured by using the given search function starting at the given non-valid
   * initial index and using the isBetter test to determine if the newIndex is better than the oldIndex.
   */
  private int findEdgeDigit(
      String line,
      Map<String, Integer> lookup,
      int initIndex,
      BiFunction<String, String, Integer> find,
      BiPredicate<Integer, Integer> isBetter
  ) {
    int edgeIndex = initIndex;
    int digit = -1;
    for (String search : lookup.keySet()) {
      int index = find.apply(line, search);
      if ((index >= 0) && isBetter.test(index, edgeIndex)) {
        edgeIndex = index;
        digit = lookup.get(search);
      }
    }
    if (digit == -1) {
      throw fail("Couldnt find edge: '" + line + "'");
    }
    return digit;
  }
}
