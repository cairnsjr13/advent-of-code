package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import java.util.function.Function;

class Day01 extends Base2017 {
  @Override
  protected Object part1(Loader2 loader) {
    return getSumOfMatches(loader.sl(), (i) -> i + 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    String input = loader.sl();
    return getSumOfMatches(input, (i) -> i + input.length() / 2);
  }

  private int getSumOfMatches(String input, Function<Integer, Integer> nextIFn) {
    int sum = 0;
    for (int i = 0; i < input.length(); ++i) {
      char ch = input.charAt(i);
      int nextI = nextIFn.apply(i);
      if (ch == safeCharAt(input, nextI)) {
        sum += (ch - '0');
      }
    }
    return sum;
  }
}
