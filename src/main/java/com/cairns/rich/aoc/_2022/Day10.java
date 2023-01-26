package com.cairns.rich.aoc._2022;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

public class Day10 extends Base2022 {
  private static final int interestOffset = 20;
  private static final int screenWidth = 40;
  
  @Override
  protected void run() throws Throwable {
    List<String[]> insts = fullLoader.ml((line) -> line.split(" +"));
    MutableInt interestingSum = new MutableInt();
    StringBuilder screen = new StringBuilder();
    
    int x = 1;
    int cycle = 0;
    for (String[] inst : insts) {
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
    
    System.out.println(interestingSum);
    System.out.println(screen);
  }
  
  private void handleCycle(MutableInt interestingSum, StringBuilder screen, int x, int cycle) {
    if ((cycle - interestOffset) % screenWidth == 0) {
      interestingSum.add(x * cycle);
    }
    int pixel = (cycle - 1) % screenWidth;
    screen.append((Math.abs(x - pixel) <= 1) ? '\u25A0' : ' '); // TODO: dark character
    if (pixel == (screenWidth - 1)) {
      screen.append('\n');
    }
  }
}
