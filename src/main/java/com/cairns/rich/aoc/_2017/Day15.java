package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import com.cairns.rich.aoc.Loader.ConfigToken;
import java.util.List;

/**
 * Seems like some random number generators are misbehaving.  We need to
 * use a judge to count the number of matches according to various rules.
 */
class Day15 extends Base2017 {
  private static final long MASK = 0b11111111_11111111;
  private static final long A_FACTOR = 16_807;
  private static final long B_FACTOR = 48_271;
  private static final ConfigToken<Integer> numTestsToken = ConfigToken.of("numTests", Integer::parseInt);

  /**
   * Returns the number of matches for the input when generators return all numbers.
   */
  @Override
  protected Object part1(Loader loader) {
    return getNumMatch(loader, 1, 1);
  }

  /**
   * Returns the number of matches for the input when generators return numbers that are specific multiples.
   */
  @Override
  protected Object part2(Loader loader) {
    return getNumMatch(loader, 4, 8);
  }

  /**
   * Returns the number of matches between the generators with the given inputs.
   */
  private int getNumMatch(Loader loader, int aMod, int bMod) {
    List<Long> input = loader.ml(Long::parseLong);
    int numTests = loader.getConfig(numTestsToken);
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

  /**
   * Finds the next valid value based on the previous value, the factor we should
   * multiply by, and the test value we should ensure the mod is equal to 0 with
   * respect to.  Will loop continuously until we find a valid number.
   */
  private long nextUntilModZero(long cur, int modTest, long factor) {
    do {
      cur = (cur * factor) % Integer.MAX_VALUE;
    }
    while ((modTest > 1) && (cur % modTest != 0));
    return cur;
  }
}
