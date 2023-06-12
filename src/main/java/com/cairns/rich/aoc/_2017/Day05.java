package com.cairns.rich.aoc._2017;

import com.cairns.rich.aoc.Loader;
import java.util.function.IntUnaryOperator;

/**
 * The cpu is trapped in a jump loop!  We need to help it figure out how long it will take to escape.
 * Each part has a different rule as to what to change the jump by each step.
 */
class Day05 extends Base2017 {
  /**
   * Computes the number of jumps needed to escape when a jump is incremented by 1 each time.
   */
  @Override
  protected Object part1(Loader loader) {
    return getStepsToEscape(loader, (i) -> 1);
  }

  /**
   * Computes the number of jumps needed to escape when a jump is incremented by 1 if it is under 3 and decremented otherwise.
   */
  @Override
  protected Object part2(Loader loader) {
    return getStepsToEscape(loader, (i) -> (i >= 3) ? -1 : 1);
  }

  /**
   * Computes the number of jumps necessary to escape with the given input.
   * The given modifier function will determine how each jump mutates after each application.
   * An escape is defined as the index falling out of the range of the list of jumps.
   */
  private int getStepsToEscape(Loader loader, IntUnaryOperator modify) {
    int[] jmps = loader.ml(Integer::parseInt).stream().mapToInt(Integer::intValue).toArray();
    int steps = 0;
    for (int i = 0; (0 <= i) && (i < jmps.length); ++steps) {
      int jmp = jmps[i];
      jmps[i] += modify.applyAsInt(jmp);
      i += jmp;
    }
    return steps;
  }
}
