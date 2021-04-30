package com.cairns.rich.aoc._2016;

import java.util.HashSet;
import java.util.Set;

class Day01 extends Base2016 {
  private static final Dir[] dirs = Dir.values();
  
  @Override
  protected void run() {
    String input = fullLoader.ml().get(0);
    String[] instructions = input.split(", ");
    Set<Long> seen = new HashSet<>();
    
    Dir dir = Dir.North;
    long x = 0;
    long y = 0;
    for (String instruction : instructions) {
      dir = dir.turn(instruction.charAt(0));
      int steps = Integer.parseInt(instruction.substring(1));
      for (int i = 0; i < steps; ++i) {
        x += dir.deltaX;
        y += dir.deltaY;
        if ((seen != null) && !seen.add(encode(x, y))) {
          seen = null;
          report("Revist", x, y);
        }
      }
    }
    report("Final", x, y);
  }
  
  private long encode(long x, long y) {
    return (x << 32) + y;
  }
  
  private void report(String tag, long x, long y) {
    System.out.println("(" + x + ", " + y + ") " + tag + " - " + (Math.abs(x) + Math.abs(y)));
  }
  
  private enum Dir {
    North(0, 1),
    East(1, 0),
    South(0, -1),
    West(-1, 0);
    
    private final int deltaX;
    private final int deltaY;
    
    private Dir(int deltaX, int deltaY) {
      this.deltaX = deltaX;
      this.deltaY = deltaY;
    }
    
    private Dir turn(char turn) {
      return dirs[((ordinal() + ((turn == 'R') ? 1 : -1)) + dirs.length) % dirs.length];
    }
  }
}
