package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader;
import java.util.List;

/**
 * We need to boot up the weather machine.  Lets do a code generation simulation to get it going.
 */
class Day25 extends Base2015 {
  /**
   * Computes the code by converting a row/col pair into a sequence number and then applying an iterative code gen.
   */
  @Override
  protected Object part1(Loader loader) {
    List<Integer> input = loader.sl(" ", Integer::parseInt);
    int targetRow = input.get(0);
    int targetCol = input.get(1);
    int sequenceNumber = computeSequenceNumber(targetRow, targetCol);
    return computeCode(sequenceNumber);
  }

  /**
   * Converts a col/row pair into a sequential number that can then be used to compute a code.
   */
  private int computeSequenceNumber(int targetRow, int targetCol) {
    int sequenceNumber = 1;
    for (int col = 1; col < targetCol; ++col) {
      sequenceNumber += (col + 1);
    }
    for (int row = 1; row < targetRow; ++row) {
      sequenceNumber += (targetCol + (row - 1));
    }
    return sequenceNumber;
  }

  /**
   * Iteratively computes the code in the given sequenceNumber position.
   */
  private long computeCode(int sequenceNumber) {
    long code = 20_151_125;
    for (int i = 1; i < sequenceNumber; ++i) {
      code = (code * 252_533) % 33_554_393;
    }
    return code;
  }
}
