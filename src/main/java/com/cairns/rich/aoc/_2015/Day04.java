package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;

/**
 * Santa wants to be a crypto boy and give out advent coins with md5 hashes.
 * We are looking for numbers that (when appended to the input) result in an md5 hash with a certain number of leading 0s.
 */
class Day04 extends Base2015 {
  /**
   * Finds the lowest number that results in a hash with 5 leading 0s.
   */
  @Override
  protected Object part1(Loader loader) {
    return findLowestFiveZeros(loader.sl(), 5);
  }

  /**
   * Finds the lowest number that results in a hash with 6 leading 0s.
   */
  @Override
  protected Object part2(Loader loader) {
    return findLowestFiveZeros(loader.sl(), 6);
  }

  /**
   * Looks for the lowest seed number that results in the given number of leading
   * zeros when hashed with the given input.  A simple increasing loop with md5.
   */
  private int findLowestFiveZeros(String input, int numLeadingZeroes) {
    String prefix = "0".repeat(numLeadingZeroes);
    for (int i = 0; true; ++i) {
      String candidate = input + i;
      String hash = md5(candidate);
      if (hash.startsWith(prefix)) {
        return i;
      }
    }
  }
}
