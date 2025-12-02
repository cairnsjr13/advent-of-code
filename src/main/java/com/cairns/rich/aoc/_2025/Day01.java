package com.cairns.rich.aoc._2025;

import com.cairns.rich.aoc.Loader;

/**
 * We need to simulate a safe dial through various rotations and collect information
 * about the zero position.  We can track the looping state by using java's floor mod
 * {@link Math#floorMod(int, int)} function to deal with negative underflows for 0->99.
 */
class Day01 extends Base2025 {
  private static final int START_AT = 50;
  private static final int DIAL_SIZE = 100;

  /**
   * For part 1 we simply need to compute the number of times the dial LANDS on
   * zero after a rotation has completed.  This can be done by checking its state
   * after the floor mod has been applied.
   */
  @Override
  protected Object part1(Loader loader) {
    int numZeros = 0;
    int at = START_AT;
    for (Move move : loader.ml(Move::new)) {
      at = Math.floorMod(at + move.magnitude, DIAL_SIZE);
      if (at == 0) {
        ++numZeros;
      }
    }
    return numZeros;
  }

  /**
   * For part 2 we need to compute the number of times the dial was ever ON zero
   * at any time.  It is important to not double count when the dial lands on zero.
   * To achieve this, we only count a zero if we did not already start on the zero.
   * It is also important to handle rotations that go around more than one full loop.
   */
  @Override
  protected Object part2(Loader loader) {
    int numZeros = 0;
    int at = START_AT;
    for (Move move : loader.ml(Move::new)) {
      boolean wasAtZero = at == 0;
      int newAt = at + move.magnitude;
      numZeros += move.numFullLoops;
      if (!wasAtZero && ((newAt <= 0) || (DIAL_SIZE <= newAt))) {
        ++numZeros;
      }
      at = Math.floorMod(newAt, DIAL_SIZE);
    }
    return numZeros;
  }

  /**
   * Descriptor object for a rotation of the dial.  This can be described as the number
   * of full rotations around the dial (multiples of {@link Day01#DIAL_SIZE}) as well as
   * the directioned rotation magnitude (negative indicating left, positive indicating right).
   */
  private static class Move {
    private final int numFullLoops;
    private final int magnitude;

    private Move(String line) {
      int distance = Integer.parseInt(line.substring(1));
      this.numFullLoops = distance / DIAL_SIZE;
      this.magnitude = (line.charAt(0) == 'R') ? distance % DIAL_SIZE : -(distance % DIAL_SIZE);
    }
  }
}
