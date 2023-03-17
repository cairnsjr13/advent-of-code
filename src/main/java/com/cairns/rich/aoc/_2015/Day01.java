package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;

class Day01 extends Base2015 {
  @Override
  protected Object part1(Loader2 loader) {
    return loader.sl().chars().map((ch) -> (ch == '(') ? 1 : -1).sum();
  }

  @Override
  protected Object part2(Loader2 loader) {
    String input = loader.sl();
    int floor = 0;
    for (int i = 0; i < input.length(); ++i) {
      floor += (input.charAt(i) == '(') ? 1 : -1;
      if (floor == -1) {
        return i + 1;
      }
    }
    throw fail();
  }
}
