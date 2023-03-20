package com.cairns.rich.aoc._2022;

import com.cairns.rich.aoc.Loader2;
import java.util.Arrays;
import org.apache.commons.lang3.mutable.MutableInt;

class Day10 extends Base2022 {
  private static final int interestOffset = 20;
  private static final int screenWidth = 40;

  @Override
  protected Object part1(Loader2 loader) {
    MutableInt interestingSum = new MutableInt(0);
    runProgram(loader, interestingSum, new StringBuilder());
    return interestingSum;
  }

  @Override
  protected Object part2(Loader2 loader) {
    StringBuilder screen = new StringBuilder("\n");
    runProgram(loader, new MutableInt(), screen);
    return screen;
  }

  private void runProgram(Loader2 loader, MutableInt interestingSum, StringBuilder screen) {
    int x = 1;
    int cycle = 0;
    for (String[] inst : loader.ml((line) -> line.split(" +"))) {
      ++cycle;
      handleCycle(interestingSum, screen, x, cycle);
      if ("addx".equals(inst[0])) {
        ++cycle;
        handleCycle(interestingSum, screen, x, cycle);
        x += Integer.parseInt(inst[1]);
      }
      else if (!"noop".equals(inst[0])) {
        throw fail(Arrays.toString(inst));
      }
    }
  }

  private void handleCycle(MutableInt interestingSum, StringBuilder screen, int x, int cycle) {
    if ((cycle - interestOffset) % screenWidth == 0) {
      interestingSum.add(x * cycle);
    }
    int pixel = (cycle - 1) % screenWidth;
    screen.append((Math.abs(x - pixel) <= 1) ? 0x2588 : ' '); // TODO: centralize dark pixel
    if (pixel == (screenWidth - 1)) {
      screen.append('\n');
    }
  }
}
