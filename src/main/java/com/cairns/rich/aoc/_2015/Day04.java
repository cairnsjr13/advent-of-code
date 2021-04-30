package com.cairns.rich.aoc._2015;

import com.google.common.base.Strings;

class Day04 extends Base2015 {
  @Override
  protected void run() {
    System.out.println(findLowestFiveZeros("iwrupvqb", 5));
    System.out.println(findLowestFiveZeros("iwrupvqb", 6));
  }
  
  private int findLowestFiveZeros(String input, int numLeadingZeroes) {
    return quietly(() -> {
      for (int i = 0; true; ++i) {
        String candidate = input + i;
        String hash = md5(candidate);
        if (hash.startsWith(Strings.repeat("0", numLeadingZeroes))) {
          System.out.print(hash + " = ");
          return i;
        }
      }
    });
  }
}
