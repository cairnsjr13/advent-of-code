package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;

class Day04 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    String input = loader.sl();
    result.part1(findLowestFiveZeros(input, 5));
    result.part2(findLowestFiveZeros(input, 6));
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
