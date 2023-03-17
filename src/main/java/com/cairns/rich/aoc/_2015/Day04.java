package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;

class Day04 extends Base2015 {
  @Override
  protected Object part1(Loader2 loader) {
    return findLowestFiveZeros(loader.sl(), 5);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return findLowestFiveZeros(loader.sl(), 6);
  }

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
