package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;

/**
 * Battery banks need to be turned on to maximize the joltage to the system.
 * By turning on specific batteries and skipping others we can derive a maximum value.
 */
class Day03 extends Base2025 {
  private static final ConfigToken<Integer> numDigitsToken = ConfigToken.of("numDigits", Integer::parseInt);

  /**
   * We need to pick the 2 batteries that lead to the maximum joltage in each bank.
   */
  @Override
  protected Object part1(Loader loader) {
    return computeMaxJoltage(loader);
  }

  /**
   * We need to pick the 12 batteries that lead to the maximum joltage in each bank.
   */
  @Override
  protected Object part2(Loader loader) {
    return computeMaxJoltage(loader);
  }

  /**
   * Computes the sum of the maximum numbers that can be generated from each row (bank) of batteries.
   * The number of digits to consider is controlled by the {@link #numDigitsToken} config.  A greedy
   * algorithm will work here because numbers with more digits are always larger than numbers with
   * less digits.  Additionally, greedily taking the largest available digit in the range between the
   * last used digit and an end index that still allows all remaining digits to be selected.
   *
   * <pre>
   * The first digit is selected from [0, end - (numDigits - 0)].
   * The second digit is selected from [firstDigitI + 1, end - (numDigits - 1)]
   * ...
   * The numDigits digit is selected from [secondToLastDigitI + 1, end]
   * </pre>
   */
  private long computeMaxJoltage(Loader loader) {
    int numDigits = loader.getConfig(numDigitsToken);
    char[][] grid = loader.ml(String::toCharArray).toArray(char[][]::new);
    long totalJoltage = 0;
    for (char[] bank : grid) {
      long bankJoltage = 0;
      int lastUsedIndex = -1;
      for (int digit = 0; digit < numDigits; ++digit) {
        lastUsedIndex = maxIndexIn(bank, lastUsedIndex, bank.length - (numDigits - digit - 1));
        bankJoltage = (bankJoltage * 10) + (bank[lastUsedIndex] - '0');
      }
      totalJoltage += bankJoltage;
    }
    return totalJoltage;
  }

  /**
   * Finds the index of the largest value falling between the two given exclusive indexes.
   */
  private int maxIndexIn(char[] bank, int fromExc, int toExc) {
    int maxIndex = fromExc + 1;
    char max = bank[fromExc + 1];
    for (int i = fromExc + 2; i < toExc; ++i) {
      char val = bank[i];
      if (max < val) {
        maxIndex = i;
        max = val;
      }
    }
    return maxIndex;
  }
}
