package com.cairns.rich.aoc._2018;

import com.cairns.rich.aoc.Loader2;
import java.util.stream.LongStream;

class Day09 extends Base2018 {
  @Override
  protected Object part1(Loader2 loader) {
    return getHighestScore(loader, 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getHighestScore(loader, 100);
  }

  private long getHighestScore(Loader2 loader, int lastMarbleFactor) {
    String[] input = loader.sl().split(" ");
    long[] elves = new long[Integer.parseInt(input[0])];
    int lastMarble = Integer.parseInt(input[6]) * lastMarbleFactor;
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
