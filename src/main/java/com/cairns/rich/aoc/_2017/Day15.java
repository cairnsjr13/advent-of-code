package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader2;
import java.util.List;

class Day15 extends Base2017 {
  private static final long MASK = 0b11111111_11111111;
  private static final long A_FACTOR = 16_807;
  private static final long B_FACTOR = 48_271;
  private static final long MOD = 2_147_483_647;

  @Override
  protected Object part1(Loader2 loader) {
    return getNumMatch(loader, 40_000_000, 1, 1);
  }

  @Override
  protected Object part2(Loader2 loader) {
    return getNumMatch(loader, 5_000_000, 4, 8);
  }

  private int getNumMatch(Loader2 loader, int numTests, int aMod, int bMod) {
    List<Long> input = loader.ml(Long::parseLong);
    long aCur = input.get(0);
    long bCur = input.get(1);
    int numMatch = 0;
    for (int i = 0; i < numTests; ++i) {
      aCur = nextUntilModZero(aCur, aMod, A_FACTOR);
      bCur = nextUntilModZero(bCur, bMod, B_FACTOR);
      if ((aCur & MASK) == (bCur & MASK)) {
        ++numMatch;
      }
    }
    return numMatch;
  }

  private long nextUntilModZero(long cur, int modTest, long factor) {
    do {
      cur = (cur * factor) % MOD;
    }
    while ((modTest > 1) && (cur % modTest != 0));
    return cur;
  }
}
