package com.cairns.rich.aoc._2017;

class Day15 extends Base2017 {
  private static final long MASK = 0b11111111_11111111;
  private static final long A_FACTOR = 16_807;
  private static final long B_FACTOR = 48_271;
  private static final long MOD = 2_147_483_647;

  @Override
  protected void run() {
    long aStart = 634;
    long bStart = 301;
    System.out.println(getNumMatch(40_000_000, aStart, 1, bStart, 1));
    System.out.println(getNumMatch(5_000_000, aStart, 4, bStart, 8));
  }

  private int getNumMatch(int numTests, long aCur, int aMod, long bCur, int bMod) {
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
