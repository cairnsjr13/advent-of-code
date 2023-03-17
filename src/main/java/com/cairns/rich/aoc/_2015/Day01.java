package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;

class Day01 extends Base2015 {
  @Override
  protected void run(Loader2 loader, ResultRegistrar result) {
    String input = loader.sl();
    result.part1(getFinalFloor(input));
    result.part2(getFirstBasementInstruction(input));
  }

  private int getFinalFloor(String input) {
    return input.chars().map((ch) -> (ch == '(') ? 1 : -1).sum();
  }

  private int getFirstBasementInstruction(String input) {
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
