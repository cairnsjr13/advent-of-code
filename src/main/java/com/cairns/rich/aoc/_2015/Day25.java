package com.cairns.rich.aoc._2015;

import com.cairns.rich.aoc.Loader2;
import java.util.List;

class Day25 extends Base2015 {
  @Override
  protected Object part1(Loader2 loader) {
    List<Integer> input = loader.sl(" ", Integer::parseInt);
    int targetRow = input.get(0);
    int targetCol = input.get(1);
    int sequenceNumber = computeSequenceNumber(targetRow, targetCol);
    return computeCode(sequenceNumber);
  }

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

  private long computeCode(int sequenceNumber) {
    long code = 20_151_125;
    for (int i = 1; i < sequenceNumber; ++i) {
      code = (code * 252_533) % 33_554_393;
    }
    return code;
  }
}
