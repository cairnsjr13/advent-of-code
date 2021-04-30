package com.cairns.rich.aoc._2018;

import java.util.stream.LongStream;

class Day09 extends Base2018 {
  @Override
  protected void run() {
    System.out.println(getHighestScore(9, 25)); 
    System.out.println(getHighestScore(10, 1_618));
    System.out.println(getHighestScore(13, 7_999));
    System.out.println(getHighestScore(17, 1_104));
    System.out.println(getHighestScore(21, 6_111));
    System.out.println(getHighestScore(30, 5_807));
    System.out.println();
    System.out.println(getHighestScore(429, 70_901));
    System.out.println(getHighestScore(429, 7_090_100));
  }
  
  private long getHighestScore(int numElves, int lastMarble) {
    long[] elves = new long[numElves];
    Marble currentMarble = new Marble(0);
    currentMarble.ccw = currentMarble.cw = currentMarble;
    
    int currentElf = 1;
    for (long i = 1; i <= lastMarble; ++i, ++currentElf) {
      if (i % 23 == 0) {
        Marble toRemove = findToRemove(currentMarble);
        safeSet(elves, currentElf, safeGet(elves, currentElf) + i + toRemove.value);
        currentMarble = toRemove.cw;
        toRemove.ccw.cw = toRemove.cw;
        toRemove.cw.ccw = toRemove.ccw;
      }
      else {
        Marble insert = new Marble(i);
        insert.ccw = currentMarble.cw;
        insert.cw = currentMarble.cw.cw;
        currentMarble = insert.ccw.cw = insert.cw.ccw = insert;
      }
    }
    return LongStream.of(elves).max().getAsLong();
  }
  
  private Marble findToRemove(Marble currentMarble) {
    for (int i = 0; i < 7; ++i) {
      currentMarble = currentMarble.ccw;
    }
    return currentMarble;
  }
  
  private static class Marble {
    private final long value;
    private Marble ccw;
    private Marble cw;
    
    private Marble(long value) {
      this.value = value;
    }
  }
}
